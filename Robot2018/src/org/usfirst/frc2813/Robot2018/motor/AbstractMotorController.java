package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

import edu.wpi.first.wpilibj.DriverStation;

public abstract class AbstractMotorController implements IMotorController {
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */

	// Motor configuration data
	protected final IMotorConfiguration configuration;
	
	protected final LogType autoCalibrationLogLevelForDebugOutput = LogType.INFO;
	protected final LogType autoCalibrationLogLevelForInfoOutput = LogType.INFO;

	/* ----------------------------------------------------------------------------------------------
	 * Constants
	 * ---------------------------------------------------------------------------------------------- */
	
	// We will use separate profiles for holding, moving to position, and moving at a rate
	public static final PIDProfileSlot PROFILE_SLOT_FOR_HOLD_POSITION     = PIDProfileSlot.HoldingPosition;
	// We will use separate profiles for holding, moving to position, and moving at a rate
	public static final PIDProfileSlot PROFILE_SLOT_FOR_MOVE_TO_POSITION  = PIDProfileSlot.MovingToPosition;
	// We will use separate profiles for holding, moving to position, and moving at a rate
	public static final PIDProfileSlot PROFILE_SLOT_FOR_MOVE_AT_VELOCITY  = PIDProfileSlot.MovingAtVelocity;

	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */

	protected IMotorState currentState;
	protected IMotorState previousState;

	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	protected AbstractMotorController(IMotorConfiguration configuration) {
		this.configuration = configuration;
	}
	
	protected void initialize() {
		 currentState = MotorStateFactory.createDisabled(this);
		 previousState = MotorStateFactory.createDisabled(this);
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * API
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public abstract void configure();

	@Override
	public IMotorState getTargetState() {
		return currentState;
	}

	@Override
	public IMotorState getPreviousTargetState() {
		return previousState;
	}	

	public IMotorConfiguration getConfiguration() {
		return configuration;
	}
	// 
	public IMotorState getMotorStateWithParameters() {
		return currentState;
	}

	@Override
	public final String getName() {
		return configuration.getName();
	}
	
	/**
	 * [ACTION] Stop output of the motor
	 */
	public final boolean disable() {
		return changeState(MotorStateFactory.createDisabled(this));
	}

	/**
	 * [ACTION] Set to an absolute encoder position to move to and hold, closed loop
	 */
	@Override
	public final boolean moveToAbsolutePosition(Length targetPosition) {
		return changeState(MotorStateFactory.createMovingToAbsolutePosition(this, targetPosition));
	}
	/*
	 * It is assumed that the units are correct.
	 * If you need a translator, use MotorUnitConversionAdapter in front!
	 */
	@Override
	public final boolean moveInDirectionAtRate(Direction targetDirection, Rate targetRate) {
		if(targetRate.getValue() == 0) {
			Logger.info(" was told to move with speed zero.  Holding position instead.");
			return holdCurrentPosition();
		} if(targetRate.getValue() < 0) {
			disable();
			throw new IllegalArgumentException("Move speed must not be negative.  Disabling the motor.");
		}
		else {
			return changeState(MotorStateFactory.createMovingInDirectionAtRate(this, targetDirection, targetRate));
		}
	}

	@Override
	public boolean moveInDirectionAtDefaultRate(Direction direction) {
		return moveInDirectionAtRate(direction, configuration.getDefaultRate());
	}
	
	/**
	 * [ACTION] Hold the current position, resist movement
	 */
	public final boolean holdCurrentPosition() {
		if(!changeState(MotorStateFactory.createHoldingPosition(this))) 
			return false;
		return true;
	}

	@Override
	public final boolean moveToRelativePosition(Direction targetDirection, Length targetRelativeDistance) {
		return changeState(MotorStateFactory.createMovingToRelativePosition(this, targetDirection, targetRelativeDistance)); 
	}

	@Override
	public boolean calibrateSensorInDirection(Direction targetDirection) {
		return changeState(MotorStateFactory.createCalibrateSensorInDirection(this, targetDirection)); 
	}

	@Override
	public final boolean resetEncoderSensorPosition(Length position) {
		if(getCurrentPosition().equals(position)) {
//			Logger.error("BUG: " + this + " got resetEncoderSensorPosition(" + position + ") when it's already correct.");
//			(new Throwable()).printStackTrace();
			return true;
		}
		Direction directionOfTravel; 
		Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition to " + position + ".");
		IMotorState stateBeforeResettingEncoders = getTargetState();
		Length positionBeforeResettingEncoders = getCurrentPosition();
		if(!changeState(MotorStateFactory.createDisabled(this))) {
			Logger.print(autoCalibrationLogLevelForDebugOutput, this + " could not change state to disabled.  No resetting sensor position.");
			return false;
		}
		if(!resetEncoderSensorPositionImpl(position)) {
			Logger.print(autoCalibrationLogLevelForDebugOutput, this + " failed to reset encoders.  Leaving motor disabled.  Expected " + position + " but got " + getCurrentPosition());
			return false;
		}
		switch(stateBeforeResettingEncoders.getOperation()) {
		case CALIBRATING_SENSOR_IN_DIRECTION:
			Logger.print(autoCalibrationLogLevelForDebugOutput, this +" resetEncoderSensorPosition(" + position + ") is transitioning to holding operation after completion of manual calibrating sensor operation.");
			changeState(MotorStateFactory.createHoldingPosition(this));
			break;
		case DISABLED:
			// Stay disabled, no further change required.
			Logger.print(autoCalibrationLogLevelForDebugOutput, this + "already disabled.  No return.");
			break;
		case HOLDING_CURRENT_POSITION:
			Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is returning to holding operation after completion of auto-calibrating sensor operation.");
			changeState(MotorStateFactory.createHoldingPosition(this));
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			directionOfTravel = stateBeforeResettingEncoders.getTargetDirection();
			if(getCurrentHardLimitSwitchStatus(directionOfTravel)) {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning to holding operation after completion of auto-calibrating sensor operation.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning back to moving away from the hardware limit after completion of auto-calibrating operation.");
				changeState(stateBeforeResettingEncoders);
			}
			
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			directionOfTravel = stateBeforeResettingEncoders.getTargetAbsolutePosition().getValue() > getCurrentPosition().getValue() ? Direction.FORWARD : Direction.REVERSE; 
			if(getCurrentHardLimitSwitchStatus(directionOfTravel)) {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning to holding position, as the absolute position is beyond the hardware limit.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning back to move to absolute position after completion of auto-calibrating sensor operation.");
				changeState(MotorStateFactory.createMovingToAbsolutePosition(this, stateBeforeResettingEncoders.getTargetAbsolutePosition()));
			}
			break;
		case MOVING_TO_RELATIVE_POSITION:
			directionOfTravel = stateBeforeResettingEncoders.getTargetAbsolutePosition().getValue() > getCurrentPosition().getValue() ? Direction.FORWARD : Direction.REVERSE;
			Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning to an adjusted relative position.");
			// NB: 
			Length adjustedRelativePosition = stateBeforeResettingEncoders.getTargetRelativeDistance().add(positionBeforeResettingEncoders.subtract(position));
			IMotorState relative = MotorStateFactory.createMovingToRelativePosition(this, stateBeforeResettingEncoders.getTargetDirection(), adjustedRelativePosition);
			if(getCurrentHardLimitSwitchStatus(directionOfTravel)) {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning to holding position, as the relative position was beyond the hardware limit.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.print(autoCalibrationLogLevelForDebugOutput, this + " resetEncoderSensorPosition(" + position + ") is transitioning back to move to an adjusted relative position after completion of auto-calibrating sensor operation: " + relative);
				changeState(relative);			
			}
			break;
		default:
			throw new IllegalStateException("Encountered an operation we don't recognize in resetEncoderSensorPosition: " + stateBeforeResettingEncoders.getOperation());
			
		}
		return true;
	}

	/* ----------------------------------------------------------------------------------------------
	 * Core of implementation
	 * ---------------------------------------------------------------------------------------------- */

	// Guards for state transitions, called by changeState
	// IMPORTANT: Do not call directly	
	protected boolean isStateTransitionAllowed(IMotorState proposedState) {
		// Validate the state transition before we do anything
		if (currentState.equals(proposedState) && proposedState.getOperation() != MotorOperation.DISABLED) {
			Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s to %s.", currentState, proposedState);
//			new Exception().printStackTrace();
			return true;
		}
		switch(proposedState.getOperation()) {
		case DISABLED:
			break;
		case HOLDING_CURRENT_POSITION:
			if(!configuration.hasAll(IMotorConfiguration.ControlPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.ControlPosition + " capability.  Refusing request for " + proposedState + ".");
			}
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			if(!configuration.hasAll(IMotorConfiguration.ControlDirection)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.ControlDirection + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getTargetDirection().isPositive() && !configuration.hasAll(IMotorConfiguration.Forward)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.Forward + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getTargetDirection().isNegative() && !configuration.hasAll(IMotorConfiguration.Reverse)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.Reverse + " capability.  Refusing request for " + proposedState + ".");
			}
			if(!configuration.hasAll(IMotorConfiguration.ControlRate)) {
				Logger.warning(this + " does not have the " + IMotorConfiguration.ControlRate + " capability.  Rate will be ignored.");
			}
			if(proposedState.getTargetRate().getValue() < 0) {
				throw new IllegalArgumentException(this + " was asked to " + proposedState + ", but negative rate is not supported.  Use the direction parameter instead.");
			}
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			if(!configuration.hasAll(IMotorConfiguration.ControlPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.ControlPosition + " capability.  Refusing request for " + proposedState + ".");
			}
			break;
		case MOVING_TO_RELATIVE_POSITION:
			if(!configuration.hasAll(IMotorConfiguration.ControlPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.ControlPosition + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getTargetDirection().isPositive() && !configuration.hasAll(IMotorConfiguration.Forward)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.Forward + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getTargetDirection().isNegative() && !configuration.hasAll(IMotorConfiguration.Reverse)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.Reverse + " capability.  Refusing request for " + proposedState + ".");
			}
			break;
		case CALIBRATING_SENSOR_IN_DIRECTION:
			if(!configuration.hasAll(IMotorConfiguration.ReadPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + IMotorConfiguration.ReadPosition + " capability.  Refusing request for " + proposedState + ".");
			}
			if(!configuration.hasAny(
					proposedState.getTargetDirection().isPositive() 
					? IMotorConfiguration.LocalForwardHardLimitSwitch|IMotorConfiguration.RemoteForwardHardLimitSwitch
					: IMotorConfiguration.LocalReverseHardLimitSwitch|IMotorConfiguration.RemoteReverseHardLimitSwitch))
			{
				throw new UnsupportedOperationException(this + " does not have either a local or remote hard limit switch in the " + proposedState.getTargetDirection() + " direction.  Refusing request for " + proposedState + ".");
			}
		default:
			break;
		}
		return true;
	}

	/**
	 * All changes to state are done here, and recorded here.
	 * Optionally reported to the log here.
	 */
	protected final boolean changeState(IMotorState motorState) {
		Logger.printFormat(LogType.DEBUG, "%s Changing state from %s to %s.", this, currentState, motorState);
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(motorState)) {
			Logger.printFormat(LogType.WARNING, "%s state transition disallowed.", this);
			return false;
		}

		// Execute the transition
		if(!executeTransition(motorState)) {
			Logger.printFormat(LogType.WARNING, "%s state transition failed.", this);
			return false;
		}
		
		// Transition successful, save the state.
		this.previousState = this.currentState;
		this.currentState = motorState;
		Logger.info(this + " transition complete: " + getDiagnostics() + " (was " + previousState + ")");		
		return true;
	}
	
	/*
	 * Dump state information
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorController#dumpState()
	 */
	public void dumpDiagnostics() {
		Logger.info(getDiagnostics());
	}
	
	public String getDiagnostics() {
		return this + " " + getTargetState() + 
		(configuration.hasAll(IMotorConfiguration.Disconnected)
				? " <<<< DISCONNECTED BY CONFIGURATION >>>>" 
				: (
					" @ " + getCurrentPosition() 
					+ (configuration.hasAll(IMotorConfiguration.LocalReverseHardLimitSwitch) ? " [RLimit=" + getCurrentHardLimitSwitchStatus(Direction.REVERSE) + "]" : "")
					+ (configuration.hasAll(IMotorConfiguration.LocalForwardHardLimitSwitch) ? " [FLimit=" + getCurrentHardLimitSwitchStatus(Direction.FORWARD) + "]" : "")
				)
		);
	}

	@Override
	public Length getCurrentPositionError() {
		return getTargetState().getCurrentPositionError();
	}

	@Override
	public Rate getCurrentRateError() {
		return getTargetState().getCurrentRateError();
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		return getTargetState().getCurrentRateErrorWithin(marginOfError);
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		return getTargetState().getCurrentPositionErrorWithin(marginOfError);
	}

	/* ----------------------------------------------------------------------------------------------
	 * Units Helpers
	 * ---------------------------------------------------------------------------------------------- */
	
	// Convert a length to sensor units
	protected final Length toSensorUnits(Length l) {
		return l == null ? null : configuration.getNativeSensorLengthUOM().create(l.convertTo(configuration.getNativeSensorLengthUOM()).getValueAsInt());
	}
//	// Convert a length to motor units	
//	protected final Length toMotorUnits(Length l) {
//		return l == null ? null : l.convertTo(configuration.getNativeMotorLengthUOM());
//	}	
	// Convert a length to display units	
	protected final Length toSubsystemUnits(Length l) {
		return l == null ? null : l.convertTo(configuration.getNativeDisplayLengthUOM());
	}
	// Convert a length to sensor units
	protected final Rate toSensorUnits(Rate r) {
		return r == null ? null : configuration.getNativeSensorRateUOM().create(r.convertTo(configuration.getNativeSensorRateUOM()).getValueAsInt());
	}
//	// Convert a length to motor units	
//	protected final Rate toMotorUnits(Rate r) {
//		return r == null ? null : r.convertTo(configuration.getNativeMotorRateUOM());
//	}
	// Convert a length to display units	
	protected final Rate toSubsystemUnits(Rate r) {
		return r == null ? null : r.convertTo(configuration.getNativeDisplayRateUOM());
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Interface To Subclasses
	 * ---------------------------------------------------------------------------------------------- */
	
	protected abstract boolean resetEncoderSensorPositionImpl(Length sensorPosition);
	protected abstract boolean executeTransition(IMotorState proposedState);
	protected abstract PIDProfileSlot getPIDProfileSlot();
	protected abstract boolean setPIDProfileSlot(PIDProfileSlot profileSlot);
	
	public String toString() {
		return getName();
	}
	
	public Length getForwardLimit() {
		return toSensorUnits(configuration.getForwardLimit());
	}
	public Length getReverseLimit() {
		return toSensorUnits(configuration.getReverseLimit());
	}
	public Length getForwardSoftLimit() {
		return toSensorUnits(configuration.getForwardSoftLimit());
	}
	public Length getReverseSoftLimit() {
		return toSensorUnits(configuration.getReverseSoftLimit());
	}
	protected static int SENSOR_RESET_TOLERANCE_PULSES = 50;
	// Returns true if we zeroed and are now holding position at zero
	protected boolean autoResetSensorPositionIfNecessary() {
		boolean resetEncoders = false;
		// Do we need to handle forward limit
		if (configuration.hasAny(MotorConfiguration.LocalForwardHardLimitSwitch|MotorConfiguration.RemoteForwardHardLimitSwitch) 
				&& configuration.getForwardHardLimitSwitchResetsEncoder() && getCurrentHardLimitSwitchStatus(Direction.FORWARD)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - getForwardLimit().getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
				/*
				 * If we are moving in reverse from a forward limit switch, don't mess with it
				 */
				if ( /* moving at rate or to position in reverse */ ((getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE || getTargetState().getOperation() == MotorOperation.MOVING_TO_RELATIVE_POSITION) && getTargetState().getTargetDirection().equals(Direction.REVERSE)) 
					|| /* moving to absolute position in reverse */ (getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && getTargetState().getTargetAbsolutePosition().getCanonicalValue() < getTargetState().getStartingAbsolutePosition().getCanonicalValue())
				)
				{
					Logger.print(autoCalibrationLogLevelForInfoOutput, this + " forward limit switch encountered and position is not the limit, but we're moving away from the limit, so we are leaving it alone.");
					return false;
				}
				Logger.print(autoCalibrationLogLevelForInfoOutput, this + " forward limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + getForwardLimit() + "."); 
				resetEncoders = resetEncoderSensorPosition(getForwardLimit());
			}
		}
		// Do we need to handle reverse limit
		if (configuration.hasAny(MotorConfiguration.LocalReverseHardLimitSwitch|MotorConfiguration.RemoteReverseHardLimitSwitch) 
				&& configuration.getReverseHardLimitSwitchResetsEncoder() && getCurrentHardLimitSwitchStatus(Direction.REVERSE)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - getReverseLimit().getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
				/*
				 * If we are moving in reverse from a forward limit switch, don't mess with it
				 */
				if ( /* moving at rate or to position in reverse */ ((getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE || getTargetState().getOperation() == MotorOperation.MOVING_TO_RELATIVE_POSITION) && getTargetState().getTargetDirection().equals(Direction.FORWARD)) 
					|| /* moving to absolute position in reverse */ (getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && getTargetState().getTargetAbsolutePosition().getCanonicalValue() > getTargetState().getStartingAbsolutePosition().getCanonicalValue())
				)
				{
					Logger.print(autoCalibrationLogLevelForInfoOutput, this + " reverse limit switch encountered and position is not the limit, but we're moving away from the limit, so we are leaving it alone.");
					return false;
				}
				Logger.print(autoCalibrationLogLevelForInfoOutput, this + " reverse limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + getReverseLimit() + "."); 
				resetEncoders = resetEncoderSensorPosition(getReverseLimit());
			}
		}
		return resetEncoders;
	}

	private static final Length POSITION_TOLERANCE_FOR_PID_PROFILE = LengthUOM.Inches.create(0.25);
	/**
	 * Determine the appropriate PID profile for the state specified, taking into account
	 * current hardware values.
	 */
	protected PIDProfileSlot getAppropriatePIDProfileSlot(IMotorState state) {
		switch(state.getOperation()) {
		case HOLDING_CURRENT_POSITION:
		case DISABLED:
			return PROFILE_SLOT_FOR_HOLD_POSITION;
		case MOVING_TO_ABSOLUTE_POSITION:
		case MOVING_TO_RELATIVE_POSITION:
			if(state.getCurrentPositionErrorWithin(POSITION_TOLERANCE_FOR_PID_PROFILE)) {
				return PROFILE_SLOT_FOR_HOLD_POSITION;
			} else {
				return PROFILE_SLOT_FOR_MOVE_TO_POSITION;
			}
		case MOVING_IN_DIRECTION_AT_RATE:
		case CALIBRATING_SENSOR_IN_DIRECTION:
			return PROFILE_SLOT_FOR_MOVE_AT_VELOCITY;
		default:
			throw new IllegalStateException("Unsupported operation: " + state.getOperation());
		}
	}

	/**
	 * Determine the appropriate PID profile for the current operation and motor state
	 */
	protected PIDProfileSlot getAppropriatePIDProfileSlotForCurrentState() {
		return getAppropriatePIDProfileSlot(getTargetState());
	}

	/**
	 * Determine the appropriate PID profile for the current operation and motor state, and 
	 * change if necessary
	 */
	protected void updatePIDProfileSlotForCurrentState() {
		PIDProfileSlot correctPIDProfileSlot = getAppropriatePIDProfileSlotForCurrentState();
		if(!getPIDProfileSlot().equals(correctPIDProfileSlot)) {
			if(getTargetState().isMovingToPosition()) {
				Logger.info(this + " updating PID profile to " + correctPIDProfileSlot + ".  We are " + (correctPIDProfileSlot.equals(PIDProfileSlot.HoldingPosition) ? "close to" : "far from") + " the target.  PositionError=" + getTargetState().getCurrentPositionError());	
			} else {
				Logger.warning(this + " updating PID profile to " + correctPIDProfileSlot + ", but we are not in a state that requires changing PID profiles.");
				(new Throwable()).printStackTrace();
			}
			setPIDProfileSlot(correctPIDProfileSlot);
		}
	}
	
	/**
	 * Display a value in both sensor and display units for debugging
	 * @return 
	 */
	private String bothUnits(Length v) {
		return toSensorUnits(v) + " (or " + toSubsystemUnits(v) + ")";
	}

	/**
	 * For any operation that can 'finish' and we change states automatically, this is the place to put the checks
	 */
	private void checkOperationComplete() {
		/*
		 * The only command that ever "completes" is calibration
		 */		
		Direction targetDirection = getTargetState().getTargetDirection();
		if(getTargetState().getOperation().equals(MotorOperation.CALIBRATING_SENSOR_IN_DIRECTION) && getCurrentHardLimitSwitchStatus(targetDirection)) {
			changeState(MotorStateFactory.createHoldingPosition(this));
		}
	}
	/*
	 * Check the indicated hard limit switch and report if it needs calibration
	 */
	private void checkForHardLimitError(Direction targetDirection) {
		if(isHardLimitNeedingCalibration(targetDirection)) {
			Logger.warning(this + " WARNING WARNING WARNING - " + targetDirection + " HARD LIMIT has drifted.  We're past the limit, but the switch is not active.  Limit: " + bothUnits(getHardLimit(targetDirection)) + " Position: " + bothUnits(getCurrentPosition()));
		}
		/* NB: We check for sensor is active, but sensor position != 0 elsewhere.  It can actually have a range.  We keep setting to zero until the motor
		       moves away from the limit switch.  There may be a range of sensor positions that still trigger the switch.  So we can't just check to see if it's 
		       position == "the limit", we do that another way.  See autoResetSensorPositionIfNecessary
		*/
	}
	/*
	 * Check the indicated hard limit switch and report if it needs calibration
	 */
	private void checkForSoftLimitError(Direction targetDirection) {
		if(isSoftLimitExceeded(targetDirection)) {
			Logger.warning(this + " WARNING WARNING WARNING - " + targetDirection + " soft limit has been exceeded.  Limit: " + bothUnits(getSoftLimit(targetDirection)) + " Position: " + bothUnits(getCurrentPosition()));
		}
	}
	/*
	 * Check the indicated specified limit has been exceeded
	 */
	private void checkForPhysicalLimitError(Direction targetDirection) {
		if(isPhysicalLimitExceeded(targetDirection)) {
			Logger.warning(this + " WARNING WARNING WARNING - " + targetDirection + " configured limit has been exceeded.  Update the limits.  Limit: " + bothUnits(getPhysicalLimit(targetDirection)) + " Position: " + bothUnits(getCurrentPosition()));
		}
	}
	/*
	 * Check if either direction has a sensor calibration issue
	 */
	private void checkForLimitErrors() {
		checkForHardLimitError(Direction.FORWARD);
		checkForHardLimitError(Direction.REVERSE);
		checkForSoftLimitError(Direction.FORWARD);
		checkForSoftLimitError(Direction.REVERSE);
		checkForPhysicalLimitError(Direction.FORWARD);
		checkForPhysicalLimitError(Direction.REVERSE);
	}
	
	@Override
	public void periodic() {
		if(DriverStation.getInstance().isEnabled()) {
			autoResetSensorPositionIfNecessary();
			updatePIDProfileSlotForCurrentState();
			checkOperationComplete();
			checkForLimitErrors();
		}
	}

	@Override
	public boolean isDisconnected() {
		return getConfiguration().hasAll(IMotorConfiguration.Disconnected);
	}
	
	@Override
	public Length getPhysicalLimit(Direction direction) {
		return direction.isPositive() ? getForwardLimit() : getReverseLimit();
	}

	@Override
	public boolean getHasHardLimit(Direction direction) {
		if(direction.isPositive()) {
			return configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition) 
					&& configuration.hasAny(IMotorConfiguration.LocalForwardHardLimitSwitch|IMotorConfiguration.RemoteForwardHardLimitSwitch)
					&& configuration.getForwardHardLimitSwitchNormal() != LimitSwitchNormal.Disabled;
					
		} else {
			return configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition) 
					&& configuration.hasAny(IMotorConfiguration.LocalReverseHardLimitSwitch|IMotorConfiguration.RemoteReverseHardLimitSwitch)
					&& configuration.getReverseHardLimitSwitchNormal() != LimitSwitchNormal.Disabled;
		}
	}

	@Override
	public boolean getHasSoftLimit(Direction direction) {
		if(direction.isPositive()) {
			return configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.ForwardSoftLimitSwitch); 
		} else {
			return configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.ReverseSoftLimitSwitch);
		}
	}
	
	@Override
	public Length getSoftLimit(Direction direction) {
		if(getHasSoftLimit(direction)) {
			return direction.isPositive() ? getForwardSoftLimit() : getReverseSoftLimit();
		}
		return null;
	}

	@Override
	public Rate getMinimumForwardRate() {
		return toSensorUnits(configuration.getMinimumForwardRate());
	}
	@Override
	public Rate getMaximumForwardRate() {
		return toSensorUnits(configuration.getMaximumForwardRate());
	}
	@Override
	public Rate getMaximumReverseRate() {
		return toSensorUnits(configuration.getMaximumReverseRate());
	}
	@Override
	public Rate getMinimumReverseRate() {
		return toSensorUnits(configuration.getMinimumReverseRate());
	}
	@Override
	public Rate getMaximumRate(Direction direction) {
		return direction.isPositive()
				? getMaximumForwardRate()
				: getMaximumReverseRate();
	}
	@Override
	public Rate getMinimumRate(Direction direction) {
		return direction.isPositive()
				? getMinimumForwardRate()
				: getMinimumReverseRate();
	}
	
	@Override
	public Length getHardLimit(Direction direction) {
		if(getHasHardLimit(direction)) {
			return direction.isPositive() ? getForwardLimit() : getReverseLimit(); 
		}
		return null;
	}

	/**
	 * Shortcut to see if position exceeds limit
	 */
	protected static boolean isLimitExceeded(Direction direction, Length limit, Length position) {
		if(direction.isPositive()) {
			return position.getCanonicalValue() > limit.getCanonicalValue(); 
		} else {
			return position.getCanonicalValue() < limit.getCanonicalValue();
		}
	}

	/**
	 * Shortcut to see if position meets or exceeds limit
	 */
	protected static boolean isLimitReached(Direction direction, Length limit, Length position) {
		if(direction.isPositive()) {
			return position.getCanonicalValue() >= limit.getCanonicalValue(); 
		} else {
			return position.getCanonicalValue() <= limit.getCanonicalValue();
		}
	}

	/**
	 * Clamp a length to a limit
	 */
	protected static Length clampToLimit(Direction direction, Length limit, Length input) {
		return isLimitExceeded(direction, limit, input) ? limit : input;
	}
	/**
	 * Clamp a rate to a limit range
	 */
	protected static Rate clampToLimit(Rate lowerLimit, Rate upperLimit, Rate input) {
		Rate r = input;
		if(upperLimit != null && input.getCanonicalValue() > upperLimit.getCanonicalValue()) {
			r = upperLimit;
		} 
		if(lowerLimit != null && input.getCanonicalValue() < lowerLimit.getCanonicalValue()) {
			r = lowerLimit;
		}
		return r;
	}

	@Override
	public boolean isHardLimitExceeded(Direction direction) {
		return getHasHardLimit(direction) && isLimitExceeded(direction, getHardLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean isHardLimitReached(Direction direction) {
		return getHasHardLimit(direction) && isLimitReached(direction, getHardLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean isHardLimitNeedingCalibration(Direction direction) {
		return getHasHardLimit(direction) && isLimitReached(direction, getHardLimit(direction), getCurrentPosition()) 
				&& !getCurrentHardLimitSwitchStatus(direction);
	}
	@Override
	public boolean isSoftLimitExceeded(Direction direction) {
		return getHasSoftLimit(direction) && isLimitExceeded(direction, getSoftLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean isSoftLimitReached(Direction direction) {
		return getHasSoftLimit(direction) && isLimitReached(direction, getSoftLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean isPhysicalLimitExceeded(Direction direction) {
		return isLimitExceeded(direction, getPhysicalLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean isPhysicalLimitReached(Direction direction) {
		return isLimitReached(direction, getPhysicalLimit(direction), getCurrentPosition());
	}
	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction direction) {
		if(getHasSoftLimit(direction)) {
			return isLimitExceeded(direction, getSoftLimit(direction), getCurrentPosition());
		}
		return false;
	}
	@Override
	public boolean getHasHardOrSoftLimit(Direction direction) {
		return getHasHardLimit(direction) || getHasSoftLimit(direction);
	}
	
}
