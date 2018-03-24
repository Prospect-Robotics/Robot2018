package org.usfirst.frc2813.Robot2018.motor.simulated;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.ISimulatedMotorController;
import org.usfirst.frc2813.Robot2018.motor.PIDProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Time;

/**
 * A wrapper class to handle an SRX Talon motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class Simulated extends AbstractMotorController implements ISimulatedMotorController {

	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */
	
	private PIDProfileSlot   lastSlot                 = PIDProfileSlot.HoldingPosition;
	private int encoderValue 						  = 0;
	private long lastCommandTimestamp 				  = System.currentTimeMillis();
	private long lastEncoderPositionUpdate            = 0;
	
	/* ----------------------------------------------------------------------------------------------
	 * Debugging
	 * ---------------------------------------------------------------------------------------------- */
	
	private static LogType logLevel = LogType.DEBUG;
	private static void debug(String message) {
		Logger.print(logLevel, message);
	}
	private static void warning(String message) {
		Logger.print(LogType.ERROR, message);
	}	
	private static void error(String message) {
		Logger.print(LogType.ALWAYS, message);
	}

	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public Simulated(IMotorConfiguration configuration) {
		super(configuration);
		initialize();
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Public IMotorController functions
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		setEncoderPosition(toSensorUnits(sensorPosition).getValueAsInt());
		return true;
	}

	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		return isHardLimitReached(direction);
	}

	private synchronized void setEncoderPosition(int pos) {
		debug("Setting sensor position.  Was " + encoderValue + " Now " + pos);
		this.encoderValue = pos;
	}

	@Override
	public final synchronized Length getCurrentPosition() {
		if(!updating) {
			updateEncoderPosition();
		}
		return configuration.getNativeSensorLengthUOM().create(encoderValue);
	}

	private IMotorState lastCompletedCommand = null; 
	private boolean updating = false;
	private synchronized void updateEncoderPosition() {
		// Do not accidentally loop forever, but we do need getCurentPosition() to update if it's not being called indirectly by updateEncoderPosition
		if(updating) {
			return;
		}
		updating = true;		
		debug("Updating: " + this + " - " + getDiagnostics());
		// Don't try to update encoder position before our first command
		if(currentState == null) {
			updating = false;
			return;
		}
		// Waiting for something new...
		if(currentState == lastCompletedCommand) {
			updating = false;
			return;
		}
		// Determine a rate
		Rate rate = getCurrentRate();
		debug("Current Rate: " + rate);
		// Determine starting position
		Length start = getConfiguration().getNativeSensorLengthUOM().create(encoderValue);
		// Determine the current time
		long now = System.currentTimeMillis();
		if(lastEncoderPositionUpdate == 0) {
			lastEncoderPositionUpdate = lastCommandTimestamp; 
		}
		// Determine an elapsed time
		Time elapsedTime = TimeUOM.Milliseconds.create(now - lastEncoderPositionUpdate); // TimeUOM.Milliseconds.create(System.currentTimeMillis() - lastCommandTimestamp);
		debug("Elapsed: " + elapsedTime);
		// Potential travel distance
		Length distance = rate.getLength(elapsedTime);
		// Determine where we should be based on command and elapsed time
		switch(currentState.getOperation()) {
		case DISABLED:
		case HOLDING_CURRENT_POSITION:
			lastCompletedCommand = currentState;
			updating = false;
			return;
		case CALIBRATING_SENSOR_IN_DIRECTION:
		case MOVING_IN_DIRECTION_AT_RATE:
		case MOVING_TO_ABSOLUTE_POSITION:
		case MOVING_TO_RELATIVE_POSITION:
			// We are moving
			break;
		default:
			throw new IllegalArgumentException(this + " unsupported operation: " + currentState.getOperation());
		}
		// No change to encoder position unless you are simulating instability
		boolean resetEncoderFromHardLimit = false;
		Direction targetDirection = currentState.getTargetDirection();
		if(currentState.getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION || currentState.getOperation() == MotorOperation.MOVING_TO_RELATIVE_POSITION) {
			targetDirection = 
					currentState.getTargetAbsolutePosition().getCanonicalValue() < configuration.getNativeSensorLengthUOM().create(encoderValue).getCanonicalValue() 
					? Direction.REVERSE 
					: Direction.FORWARD
					;
		}
		if(targetDirection == null) {
			throw new IllegalStateException("Simulator error.  targetDirection is null!");
		}
		debug("RawDistance: " + distance);		
		Length distanceWithSign = distance.multiply(targetDirection.getMultiplierAsDouble());
		debug("distanceWithSign: " + distanceWithSign);		
		// Where would we end up if we were going in the same direction the entire time
		Length projectedAbsolutePosition = start.add(distanceWithSign);
		debug("projectedAbsolutePosition: " + projectedAbsolutePosition);		
		// OK, see where PID would have stopped us
		Length targetAbsolutePosition = currentState.getTargetAbsolutePosition();
		if(targetAbsolutePosition != null) {
			projectedAbsolutePosition = clampToLimit(targetDirection, targetAbsolutePosition /* limit */, projectedAbsolutePosition);
			if(projectedAbsolutePosition.getCanonicalValue() == targetAbsolutePosition.getCanonicalValue()) {
				warning("Target Position Reached.");
				lastCompletedCommand = currentState;
			}
		}
		debug("projectedAbsolutePosition: " + projectedAbsolutePosition);		
		debug("Start=" + start + " CommandDistance=" + distanceWithSign + " Projected=" + projectedAbsolutePosition + " Target=" + targetAbsolutePosition);
		// Next, see if a soft limit would have stopped us.
		Length softLimit = null;
		if(getHasSoftLimit(targetDirection) && isLimitReached(targetDirection, softLimit = getSoftLimit(targetDirection), projectedAbsolutePosition)) {
			warning("Soft Limit Reached.  Clamping to " + softLimit + ".");
			projectedAbsolutePosition = clampToLimit(targetDirection, softLimit, projectedAbsolutePosition);
			lastCompletedCommand = currentState;
		}
		// Next, see if a hard limit would have stopped us.
		Length hardLimit = null;
		if(getHasHardLimit(targetDirection) && isLimitReached(targetDirection, hardLimit = getHardLimit(targetDirection), projectedAbsolutePosition)) {
			warning("Hard Limit Reached.  Clamping to " + hardLimit + ".");
			projectedAbsolutePosition = clampToLimit(targetDirection, hardLimit, projectedAbsolutePosition);
			resetEncoderFromHardLimit = targetDirection.isPositive() ? configuration.getForwardHardLimitSwitchResetsEncoder() : configuration.getReverseHardLimitSwitchResetsEncoder();
			lastCompletedCommand = currentState;
		}
		// Lastly, did we break the robot?
		Length physicalLimit = getPhysicalLimit(targetDirection);
		if(isLimitExceeded(targetDirection, physicalLimit, projectedAbsolutePosition)) {
			projectedAbsolutePosition = clampToLimit(targetDirection, getPhysicalLimit(targetDirection), projectedAbsolutePosition);
			lastCompletedCommand = currentState;
			error(this + ": KABOOM!  You just broke the robot.  Moved " + targetDirection + " beyond " + physicalLimit + " and broke the hardware.  Projected=" + projectedAbsolutePosition);
		}
		// Now do the hardware-based encoder reset if necessary, which is always zero
		if(resetEncoderFromHardLimit) {
			warning("Resetting encoder due to " + targetDirection + " hard limit.");
			projectedAbsolutePosition = projectedAbsolutePosition.getUOM().create(0);
		}
		// Now update the encoder value from our projection
		int newEncoderValue = toSensorUnits(projectedAbsolutePosition).getValueAsInt();
		debug("newEncoderValue: " + newEncoderValue);		
		if(this.encoderValue != newEncoderValue) {
			setEncoderPosition(newEncoderValue);
		}
		updating = false;
	}

	@Override
	public Rate getCurrentRate() {
		Rate rate = configuration.getDefaultRate().getUOM().create(0);
		// Determine the range for rates
		switch(currentState.getOperation()) {
		case CALIBRATING_SENSOR_IN_DIRECTION:
		case MOVING_IN_DIRECTION_AT_RATE:
			Direction targetDirection = currentState.getTargetDirection();
			if(currentState.getTargetRate() != null) {
				debug("currentState.getTargetRate(): " + currentState.getTargetRate());
				rate = toSensorUnits(currentState.getTargetRate());
			} else {
				debug("configuration.getDefaultRate(): " + configuration.getDefaultRate());
				rate = toSensorUnits(configuration.getDefaultRate());
			}
			clampToLimit(getMinimumRate(targetDirection), getMaximumRate(targetDirection), rate);
			clampToLimit(getMinimumRate(targetDirection), getMaximumRate(targetDirection), rate);
			debug("VELOCITY RATE: " + rate);
			break;
		case DISABLED:
		case HOLDING_CURRENT_POSITION:
			// leave at zero
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
		case MOVING_TO_RELATIVE_POSITION:
			if(encoderValue == currentState.getTargetAbsolutePosition().getValueAsInt()) {
				break; // leave at zero, we're done 
			}
			// Assume PID is flying for now
			rate = encoderValue < currentState.getTargetAbsolutePosition().getValue() 
					? getMaximumForwardRate() 
					: getMaximumReverseRate();
			break;
		default:
			throw new UnsupportedOperationException("Unsupported operation: " + getTargetState());
		}
		// TODO: Improve rate simulation, maybe add fake output for testing PID at some point.  
		return rate;
	}

	@Override
	public boolean supportsMotorInversion() {
		return true;
	}

	@Override
	public boolean supportsSensorInversion() {
		return true;
	}

	protected boolean executeTransition(IMotorState proposedState) {
		this.lastSlot = getAppropriatePIDProfileSlot(proposedState);
		this.lastCommandTimestamp = System.currentTimeMillis();
		this.lastEncoderPositionUpdate = 0;
		return true;
	}

	/* ----------------------------------------------------------------------------------------------
	 * Internal Helper Functions - Motor Specific
	 * ---------------------------------------------------------------------------------------------- */
	
	public String getDiagnotics() {
		return super.getDiagnostics()
				+ (configuration.hasAll(IMotorConfiguration.Disconnected) ? "" :
				  " [...todo...]");
	}
	
	@Override
	public void configure() {
		// Start disabled
		changeState(MotorStateFactory.createDisabled(this));
	}

	@Override
	public String toString() {
		return configuration.getName() + "." + this.getClass().getSimpleName();  
	}
		
	/**
	 * At periodic intervals, we'll update our projected position.  Why not wait and do it lazily?  Cause we'll go boom when we hit a physical limit, and we'll reset counter if necessary!
	 */
	@Override
	public void periodic() {
		if(!updating) {
			updateEncoderPosition();
		}
		super.periodic(); // NB: Always update encoder position before calling superclass, which depends on simulated data!	
	}

	@Override
	protected PIDProfileSlot getPIDProfileSlot() {
		return lastSlot;
	}

	@Override
	protected boolean setPIDProfileSlot(PIDProfileSlot profileSlot) {
		this.lastSlot = profileSlot;
		return true;
	}
	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection) {
		return isSoftLimitReached(switchDirection);
	}
}
