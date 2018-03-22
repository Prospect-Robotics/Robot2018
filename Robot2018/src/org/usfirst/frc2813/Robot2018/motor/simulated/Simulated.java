package org.usfirst.frc2813.Robot2018.motor.simulated;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.ISimulatedMotorController;
import org.usfirst.frc2813.Robot2018.motor.PID;
import org.usfirst.frc2813.Robot2018.motor.PIDProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Time;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

/**
 * A wrapper class to handle an SRX Talon motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class Simulated extends AbstractMotorController implements ISimulatedMotorController {

	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */
	
	private PIDProfileSlot   lastSlot                 = PIDProfileSlot.HoldingPosition;
	private int encoderValue = 0;
	private long lastCommandTimestamp = System.currentTimeMillis();

	/* ----------------------------------------------------------------------------------------------
	 * Constants
	 * ---------------------------------------------------------------------------------------------- */
	
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
		this.encoderValue = toSensorUnits(sensorPosition).getValueAsInt();
		return true;
	}

	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		return isHardLimitReached(direction);
	}

	private void updateEncoderPosition() {
		// Don't try to update encoder position before our first command
		if(currentState == null) {
			return;
		}
		// Determine a rate
		Rate rate = getCurrentRate();
		// Determine starting position
		Length start = currentState.getStartingAbsolutePosition();
		// Determine an elapsed time
		Time elapsedTime = TimeUOM.Milliseconds.create(System.currentTimeMillis() - lastCommandTimestamp);
		// Potential travel distance
		Length distance = rate.getLength(elapsedTime);
		
		// Determine where we should be based on command and elapsed time
		switch(currentState.getOperation()) {
		case DISABLED:
		case HOLDING_CURRENT_POSITION:
			// Do not update during simulation in disabled mode
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
		if(currentState.getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION || currentState.getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION) {
			targetDirection = 
					currentState.getTargetAbsolutePosition().getCanonicalValue() < getCurrentPosition().getCanonicalValue() 
					? Direction.REVERSE 
					: Direction.FORWARD
					;
		}
		if(targetDirection == null) {
			Logger.info(this + " targetDirection is NULL: " + currentState);
			throw new IllegalStateException("KABOOM!");
		}
		Length distanceWithSign = distance.multiply(targetDirection.getMultiplierAsDouble());
		// Where would we end up if we were going in the same direction the entire time
		Length projectedAbsolutePosition = start.add(distanceWithSign);
		// OK, see where PID would have stopped us
		Length targetAbsolutePosition = currentState.getTargetAbsolutePosition();
		if(targetAbsolutePosition != null) {
			if(targetAbsolutePosition.getCanonicalValue() < projectedAbsolutePosition.getCanonicalValue()) {
				projectedAbsolutePosition = targetAbsolutePosition;
			}
		}
		// Next, see if a soft limit would have stopped us.
		if(getHasSoftLimit(targetDirection)) {
			Length softLimit = getSoftLimit(targetDirection);
			if(isLimitReached(targetDirection, softLimit, projectedAbsolutePosition)) { 
				projectedAbsolutePosition = softLimit;
			}
		}
		// Next, see if a hard limit would have stopped us.
		if(getHasHardLimit(targetDirection)) {
			Length hardLimit = getHardLimit(targetDirection);
			if(isLimitExceeded(targetDirection, hardLimit, projectedAbsolutePosition)) { 
				projectedAbsolutePosition = hardLimit;
				resetEncoderFromHardLimit = targetDirection.isPositive() 
						? configuration.getForwardHardLimitSwitchResetsEncoder() 
						: configuration.getReverseHardLimitSwitchResetsEncoder(); 
			}
		}
		// Lastly, did we break the robot?
		Length physicalLimit = getPhysicalLimit(targetDirection);
		if(isLimitExceeded(targetDirection, physicalLimit, projectedAbsolutePosition)) {
			throw new IllegalStateException(this + ": KABOOM!  You just broke the robot.  Moved " + targetDirection + " beyond " + physicalLimit + " and broke the hardware.  Projected=" + projectedAbsolutePosition);
		}
		// Now do the hardware-based encoder reset if necessary, which is always zero
		if(resetEncoderFromHardLimit) {
			projectedAbsolutePosition = projectedAbsolutePosition.getUOM().create(0);
		}
		// Now update the encoder value from our projection
		this.encoderValue = projectedAbsolutePosition.getValueAsInt();
	}
	
	@Override
	public final Length getCurrentPosition() {
		updateEncoderPosition();
		return configuration.getNativeSensorLengthUOM().create(encoderValue);
	}

	@Override
	public Rate getCurrentRate() {
		Rate rate = configuration.getDefaultRate();
		if(currentState.getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE) {
			// Determine the range for rates
			Rate minimumRate = 
					currentState.getTargetDirection().isPositive() 
					? configuration.getMinimumForwardRate() 
					: configuration.getMinimumReverseRate();
			Rate maximumRate = 
					currentState.getTargetDirection().isPositive() 
					? configuration.getMaximumForwardRate() 
					: configuration.getMaximumReverseRate();

			// Is motor going too slow and stalls?
			if(currentState.getTargetRate().getCanonicalValue() < minimumRate.getCanonicalValue()) {
				rate = configuration.getNativeSensorRateUOM().create(0);
			}
			// Is motor going too fast and maxxed out?
			if(currentState.getTargetRate().getCanonicalValue() > maximumRate.getCanonicalValue()) {
				rate = maximumRate;
			}
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
		super.periodic();
		updateEncoderPosition();
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
