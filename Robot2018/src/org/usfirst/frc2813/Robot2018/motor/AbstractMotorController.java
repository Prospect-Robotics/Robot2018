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

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

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
	 * @param motorState The state we want to change to
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
	 * Interface To Subclasses
	 * ---------------------------------------------------------------------------------------------- */
	
	/**
	 * Call the subclass to do the actual sensor position setting
	 * @param sensorPosition The new sensor value
	 * @return true if reset works, false otherwise.
	 */
	protected abstract boolean resetEncoderSensorPositionImpl(Length sensorPosition);
	/**
	 * Call the subclass to do the actual execution of the state transition
	 * @param proposedState The state to transition to
	 * @return true if execute works, false otherwise.
	 */
	protected abstract boolean executeTransition(IMotorState proposedState);
	/**
	 * What PID profile are we running
	 * @return What PID profile are we running
	 */
	protected abstract PIDProfileSlot getPIDProfileSlot();
	/**
	 * Call the subclass to change PID profile slots as we transition between moving and holding.
	 * @param profileSlot The PID profile to transition to
	 * @return true if update works, false otherwise.
	 */
	protected abstract boolean setPIDProfileSlot(PIDProfileSlot profileSlot);
	
	@Override
	public final String toString() {
		return "<" + configuration.getName() + "." + this.getClass().getSimpleName() + ">";
	}

	protected static int SENSOR_RESET_TOLERANCE_PULSES = 50;
	/** 
	 * Returns true if we zeroed and are now holding position at zero
	 * @return true/false
	 */
	protected boolean autoResetSensorPositionIfNecessary() {
		Length newSensorValueCFG = getConfiguration().getSensorPositionIfResetIsNecessary(this);
		Length newSensorValueOLD = null;
		boolean resetEncoders = false;
		// Do we need to handle forward limit
		if (configuration.hasAny(MotorConfiguration.LocalForwardHardLimitSwitch|MotorConfiguration.RemoteForwardHardLimitSwitch) 
				&& configuration.getForwardHardLimitSwitchResetsEncoder() && getCurrentHardLimitSwitchStatus(Direction.FORWARD)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - getConfiguration().toSensorUnits(getConfiguration().getForwardLimit()).getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
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
				Logger.print(autoCalibrationLogLevelForInfoOutput, this + " forward limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + getConfiguration().toSensorUnits(getConfiguration().getForwardLimit()) + ".");
				newSensorValueOLD = getConfiguration().toSensorUnits(getConfiguration().getForwardLimit());
			}
		}
		// Do we need to handle reverse limit
		if (configuration.hasAny(MotorConfiguration.LocalReverseHardLimitSwitch|MotorConfiguration.RemoteReverseHardLimitSwitch) 
				&& configuration.getReverseHardLimitSwitchResetsEncoder() && getCurrentHardLimitSwitchStatus(Direction.REVERSE)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - getConfiguration().toSensorUnits(getConfiguration().getReverseLimit()).getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
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
				Logger.print(autoCalibrationLogLevelForInfoOutput, this + " reverse limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + getConfiguration().toSensorUnits(getConfiguration().getReverseLimit()) + "."); 
				newSensorValueOLD = getConfiguration().toSensorUnits(getConfiguration().getReverseLimit());
			}
		}
//		if(newSensorValueOLD != newSensorValueCFG && (newSensorValueOLD == null || newSensorValueCFG == null || !newSensorValueCFG.equals(newSensorValueOLD))) {
//			Logger.error("We got different answers from getSensorPositionIfResetIsNecessary and autoResetSensorPositionIfNecessary.  It's not right yet.");
//			(new Throwable()).printStackTrace();
//		}
		if(newSensorValueOLD != null) {
			resetEncoders = resetEncoderSensorPosition(newSensorValueOLD);
		}
		return resetEncoders;
	}

	private static final Length POSITION_TOLERANCE_FOR_PID_PROFILE = LengthUOM.Inches.create(0.25);
	/**
	 * Determine the appropriate PID profile for the state specified, taking into account
	 * current hardware values.
	 * @param The state to check and determine an appropriate PID profile slot
	 * @return The appropriate PID profile slot
	 */
	protected static PIDProfileSlot getAppropriatePIDProfileSlot(IMotorState state) {
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
	 * @return The appropriate PID profile slot
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
	 * @return a display string showing the sensor and "subsystem" units. 
	 */
	public String describeLength(Length v) {
		return getConfiguration().toSensorUnits(v) + " (or " + getConfiguration().toDisplayUnits(v) + ")";
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

	@Override
	public void periodic() {
		autoResetSensorPositionIfNecessary();
		updatePIDProfileSlotForCurrentState();
		checkOperationComplete();
		getConfiguration().checkForLimitErrors(this);
	}

	@Override
	public boolean isHardLimitExceeded(Direction direction) {
		return getConfiguration().isHardLimitExceeded(this, direction);
	}
	@Override
	public boolean isHardLimitReached(Direction direction) {
		return getConfiguration().isHardLimitReached(this, direction);
	}
	@Override
	public boolean isHardLimitNeedingCalibration(Direction direction) {
		return getConfiguration().isHardLimitNeedingCalibration(this, direction);
	}
	@Override
	public boolean isSoftLimitExceeded(Direction direction) {
		return getConfiguration().isSoftLimitExceeded(this, direction);
	}
	@Override
	public boolean isSoftLimitReached(Direction direction) {
		return getConfiguration().isSoftLimitReached(this, direction);
	}
	@Override
	public boolean isPhysicalLimitExceeded(Direction direction) {
		return getConfiguration().isPhysicalLimitExceeded(this,  direction);
	}
	@Override
	public boolean isPhysicalLimitReached(Direction direction) {
		return getConfiguration().isPhysicalLimitReached(this,  direction);
	}
	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction direction) {
		return getConfiguration().getCurrentSoftLimitSwitchStatus(this, direction);
	}
	
}
