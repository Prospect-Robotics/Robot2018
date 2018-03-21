package org.usfirst.frc2813.Robot2018.motor.simulated;

import java.util.Iterator;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.ISimulatedMotorController;
import org.usfirst.frc2813.Robot2018.motor.PID;
import org.usfirst.frc2813.Robot2018.motor.PIDConfiguration;
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

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

/**
 * A wrapper class to handle an SRX Talon motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class Simulated extends AbstractMotorController implements ISimulatedMotorController {
	
	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */
	
	// Last arg sent for controlMode
	private PIDProfileSlot lastSlot                 = PIDProfileSlot.HoldingPosition;
	private PID         lastPID                  = PID.Primary;
	// last control mode parameter
	protected ControlMode    lastControlMode          = ControlMode.Position; // Remember last assigned control mode, help us implement state transitions
	private double           lastControlModeValue     = 0;
	private int encoderValue = 0;
	private long lastCommandTimestamp = System.currentTimeMillis();

	/* ----------------------------------------------------------------------------------------------
	 * Constants
	 * ---------------------------------------------------------------------------------------------- */
	
	// We will use separate profiles for holding and moving
	public static final PIDProfileSlot PROFILE_SLOT_FOR_HOLD_POSITION = PIDProfileSlot.HoldingPosition;
	// We will use separate profiles for holding and moving
	public static final PIDProfileSlot PROFILE_SLOT_FOR_MOVE          = PIDProfileSlot.Moving;
	// We will use the primary PID loop, not the aux
	public static final PID PID_INDEX_FOR_HOLD_POSITION            = PID.Primary;
	// We will use the primary PID loop, not the aux
	public static final PID PID_INDEX_FOR_MOVE                     = PID.Primary;
	
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
	
	private Length getPhysicalLimit(Direction direction) {
		return direction.isPositive() ? configuration.getForwardLimit() : configuration.getReverseLimit();
	}
	
	private boolean getHasHardLimit(Direction direction) {
		if(direction.isPositive()) {
			if(configuration.getForwardHardLimitSwitchNormal() == LimitSwitchNormal.Disabled) {
				return false;
			}
			return configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition) 
					&& configuration.hasAny(IMotorConfiguration.LocalForwardHardLimitSwitch|IMotorConfiguration.RemoteForwardHardLimitSwitch);
		} else {
			if(configuration.getReverseHardLimitSwitchNormal() == LimitSwitchNormal.Disabled) {
				return false;
			}
			return configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition) 
					&& configuration.hasAny(IMotorConfiguration.LocalReverseHardLimitSwitch|IMotorConfiguration.RemoteReverseHardLimitSwitch);
		}
	}
	
	private boolean getHasSoftLimit(Direction direction) {
		if(direction.isPositive()) {
			return configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.ForwardSoftLimitSwitch); 
		} else {
			return configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.ReverseSoftLimitSwitch);
		}
	}
	
	private Length getHardSwitchLimit(Direction direction) {
		if(getHasHardLimit(direction)) {
			return direction.isPositive() ? configuration.getForwardLimit() : configuration.getReverseLimit(); 
		}
		return null;
	}
	
	private static boolean isLimitExceeded(Direction direction, Length limit, Length position) {
		if(direction.isPositive()) {
			return position.getCanonicalValue() >= limit.getCanonicalValue(); 
		} else {
			return position.getCanonicalValue() <= limit.getCanonicalValue();
		}
	}

	@Override
	public boolean getCurrentLimitSwitchStatus(Direction direction) {
		if(getHasHardLimit(direction)) {
			return isLimitExceeded(direction, getHardSwitchLimit(direction), getCurrentPosition());
		}
		return false;
	}
	
	private Length getSoftLimit(Direction direction) {
		if(getHasSoftLimit(direction)) {
			return direction.isPositive() ? configuration.getForwardSoftLimit() : configuration.getReverseSoftLimit();
		}
		return null;
	}
	
	private boolean getCurrentSoftLimitSwitchStatus(Direction direction) {
		if(getHasSoftLimit(direction)) {
			return isLimitExceeded(direction, getSoftLimit(direction), getCurrentPosition());
		}
		return false;
	}

	private void updateEncoderPosition() {
		// Handle auto-hard reset
		if(getCurrentLimitSwitchStatus(Direction.FORWARD) && configuration.getForwardHardLimitSwitchResetsEncoder()) {
			this.encoderValue = 0;
		}
		if(getCurrentLimitSwitchStatus(Direction.REVERSE) && configuration.getReverseHardLimitSwitchResetsEncoder()) {
			this.encoderValue = 0;
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
			// No change to encoder position unless you are simulating instability
			if(distance.getValue() > 0) {
				throw new java.lang.IllegalStateException(this + " in " + currentState + " but calculated a simulated distance.");
			}
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
			if(isLimitExceeded(targetDirection, softLimit, projectedAbsolutePosition)) { 
				projectedAbsolutePosition = softLimit;
			}
		}
		// Next, see if a hard limit would have stopped us.
		if(getHasHardLimit(targetDirection)) {
			Length hardLimit = getHardSwitchLimit(targetDirection);
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
			throw new IllegalStateException(this + ": KABOOM!  You just broke the robot.  Moved " + targetDirection + " beyond " + physicalLimit + " and broke the hardware.");
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
		// New PID/Slot are almost always maintain
		PIDProfileSlot   newSlotIndex        = lastSlot;
		PID         	 newPIDIndex         = lastPID;
		ControlMode      newControlMode      = ControlMode.Disabled;
		double           newControlModeValue = 0;

		// Figure out the new control mode and argument
		switch(proposedState.getOperation()) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newSlotIndex   = PROFILE_SLOT_FOR_HOLD_POSITION;
			newPIDIndex    = PID_INDEX_FOR_HOLD_POSITION;
			newControlModeValue = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_HOLD_POSITION;
			newPIDIndex    = PID_INDEX_FOR_HOLD_POSITION;
			newControlModeValue = getCurrentPosition().getValue();
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_TO_RELATIVE_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toMotorUnits(proposedState.getTargetRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
			break;
		case CALIBRATING_SENSOR_IN_DIRECTION:
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toMotorUnits(configuration.getDefaultRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
		default:
			break;
		}

		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		this.lastSlot = newSlotIndex;
		this.lastPID = newPIDIndex;
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

	public String toString() {
		return configuration.getName() + "." + this.getClass().getSimpleName();  
	}

	protected boolean isUsingPIDSlotIndexForHolding() {
		return lastSlot != null && lastSlot.equals(PROFILE_SLOT_FOR_HOLD_POSITION);
	}
	
	protected boolean updatePIDSlotIndex(boolean holding) {
		if(null == lastPID || null == lastSlot) {
			return false;
		}
		this.lastSlot = holding ? PROFILE_SLOT_FOR_HOLD_POSITION : PROFILE_SLOT_FOR_MOVE;
		return true;
	}
	
	/**
	 * At periodic intervals, we'll update our projected position.  Why not wait and do it lazily?  Cause we'll go boom when we hit a physical limit, and we'll reset counter if necessary!
	 */
	public void periodic() {
		super.periodic();
		updateEncoderPosition();
	}
}
