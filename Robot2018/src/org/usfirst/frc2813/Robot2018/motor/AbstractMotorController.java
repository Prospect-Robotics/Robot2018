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

public abstract class AbstractMotorController implements IMotorController {
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */

	// Motor configuration data
	protected final IMotorConfiguration configuration;

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
		Logger.info("resetEncoderSensorPosition to " + position + ".");
		IMotorState stateBeforeResettingEncoders = getTargetState();
		Length positionBeforeResettingEncoders = getCurrentPosition();
		if(!changeState(MotorStateFactory.createDisabled(this))) {
			Logger.info("Could not change state to disabled.  No resetting sensor position.");
			return false;
		}
		if(!resetEncoderSensorPositionImpl(position)) {
			Logger.error("Failed to reset encoders.  Leaving motor disabled.  Expected " + position + " but got " + getCurrentPosition());
			return false;
		}
		switch(stateBeforeResettingEncoders.getOperation()) {
		case CALIBRATING_SENSOR_IN_DIRECTION:
			Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning to holding operation after completion of manual calibrating sensor operation.");
			changeState(MotorStateFactory.createHoldingPosition(this));
			break;
		case DISABLED:
			// Stay disabled, no further change required.
			break;
		case HOLDING_CURRENT_POSITION:
			Logger.info("resetEncoderSensorPosition(" + position + ") is returning to holding operation after completion of auto-calibrating sensor operation.");
			changeState(MotorStateFactory.createHoldingPosition(this));
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			directionOfTravel = stateBeforeResettingEncoders.getTargetDirection();
			if(getCurrentLimitSwitchStatus(directionOfTravel)) {
				Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning to holding operation after completion of auto-calibrating sensor operation.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning back to moving away from the hardware limit after completion of auto-calibrating operation.");
				changeState(stateBeforeResettingEncoders);
			}
			
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			directionOfTravel = stateBeforeResettingEncoders.getTargetAbsolutePosition().getValue() > getCurrentPosition().getValue() ? Direction.FORWARD : Direction.REVERSE; 
			if(getCurrentLimitSwitchStatus(directionOfTravel)) {
				Logger.error("resetEncoderSensorPosition(" + position + ") is transitioning to holding position, as the absolute position is beyond the hardware limit.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning back to move to absolute position after completion of auto-calibrating sensor operation.");
				changeState(MotorStateFactory.createMovingToAbsolutePosition(this, stateBeforeResettingEncoders.getTargetAbsolutePosition()));
			}
			break;
		case MOVING_TO_RELATIVE_POSITION:
			directionOfTravel = stateBeforeResettingEncoders.getTargetAbsolutePosition().getValue() > getCurrentPosition().getValue() ? Direction.FORWARD : Direction.REVERSE;
			Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning to an adjusted relative position.");
			// NB: 
			Length adjustedRelativePosition = stateBeforeResettingEncoders.getTargetRelativeDistance().add(positionBeforeResettingEncoders.subtract(position));
// DEBUGGING
			IMotorState relative = MotorStateFactory.createMovingToRelativePosition(this, stateBeforeResettingEncoders.getTargetDirection(), adjustedRelativePosition);
			IMotorState absolute = MotorStateFactory.createMovingToAbsolutePosition(this, stateBeforeResettingEncoders.getTargetAbsolutePosition());
			if(!relative.equals(absolute)) {
				Logger.error("Relative and absolute commands calculated did not match!");
			}
// DEBUGGING
			if(getCurrentLimitSwitchStatus(directionOfTravel)) {
				Logger.error("resetEncoderSensorPosition(" + position + ") is transitioning to holding position, as the relative position was beyond the hardware limit.");
				changeState(MotorStateFactory.createHoldingPosition(this));
			} else {
				Logger.info("resetEncoderSensorPosition(" + position + ") is transitioning back to move to an adjusted relative position after completion of auto-calibrating sensor operation: " + relative);
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
		Logger.info(this + " transition complete: " + getDiagnostics());		
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
					+ (configuration.hasAll(IMotorConfiguration.LocalReverseHardLimitSwitch) ? " [RLimit=" + getCurrentLimitSwitchStatus(Direction.REVERSE) + "]" : "")
					+ (configuration.hasAll(IMotorConfiguration.LocalForwardHardLimitSwitch) ? " [FLimit=" + getCurrentLimitSwitchStatus(Direction.FORWARD) + "]" : "")
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
		return l.convertTo(configuration.getNativeSensorLengthUOM());
	}
	// Convert a length to motor units	
	protected final Length toMotorUnits(Length l) {
		return l.convertTo(configuration.getNativeMotorLengthUOM());
	}	
	// Convert a length to display units	
	protected final Length toSubsystemUnits(Length l) {
		return l.convertTo(configuration.getNativeDisplayLengthUOM());
	}
	// Convert a length to sensor units
	protected final Rate toSensorUnits(Rate l) {
		return l.convertTo(configuration.getNativeSensorRateUOM());
	}
	// Convert a length to motor units	
	protected final Rate toMotorUnits(Rate l) {
		return l.convertTo(configuration.getNativeMotorRateUOM());
	}
	// Convert a length to display units	
	protected final Rate toSubsystemUnits(Rate l) {
		return l.convertTo(configuration.getNativeDisplayRateUOM());
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Interface To Subclasses
	 * ---------------------------------------------------------------------------------------------- */
	
	protected abstract boolean resetEncoderSensorPositionImpl(Length sensorPosition);
	protected abstract boolean executeTransition(IMotorState proposedState);
	protected abstract boolean isUsingPIDSlotIndexForHolding();
	protected abstract boolean updatePIDSlotIndex(boolean holding);
	
	public String toString() {
		return getName();
	}

	protected static int SENSOR_RESET_TOLERANCE_PULSES = 50;
	// Returns true if we zeroed and are now holding position at zero
	protected boolean autoResetSensorPositionIfNecessary() {
		boolean resetEncoders = false;
		// Do we need to handle forward limit
		if (configuration.hasAny(MotorConfiguration.LocalForwardHardLimitSwitch|MotorConfiguration.RemoteForwardHardLimitSwitch) 
				&& configuration.getForwardHardLimitSwitchResetsEncoder() && getCurrentLimitSwitchStatus(Direction.FORWARD)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - configuration.getForwardLimit().getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
				/*
				 * If we are moving in reverse from a forward limit switch, don't mess with it
				 */
				if ( /* moving at rate or to position in reverse */ ((getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE || getTargetState().getOperation() == MotorOperation.MOVING_TO_RELATIVE_POSITION) && getTargetState().getTargetDirection().equals(Direction.REVERSE)) 
					|| /* moving to absolute position in reverse */ (getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && getTargetState().getTargetAbsolutePosition().getCanonicalValue() < getTargetState().getStartingAbsolutePosition().getCanonicalValue())
				)
				{
					Logger.debug("Forward limit switch encountered and position is not the limit, but we're moving away from the limit, so we are leaving it alone.  Changing sensor value from " + getCurrentPosition() + " to " + configuration.getForwardLimit() + ".");
				}
				Logger.debug("Forward limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + configuration.getForwardLimit() + "."); 
				resetEncoders = resetEncoderSensorPosition(toSensorUnits(configuration.getForwardLimit()));
			}
		}
		// Do we need to handle reverse limit
		if (configuration.hasAny(MotorConfiguration.LocalReverseHardLimitSwitch|MotorConfiguration.RemoteReverseHardLimitSwitch) 
				&& configuration.getReverseHardLimitSwitchResetsEncoder() && getCurrentLimitSwitchStatus(Direction.REVERSE)) 
		{
			if(Math.abs(getCurrentPosition().getValue() - configuration.getReverseLimit().getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
				/*
				 * If we are moving in reverse from a forward limit switch, don't mess with it
				 */
				if ( /* moving at rate or to position in reverse */ ((getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE || getTargetState().getOperation() == MotorOperation.MOVING_TO_RELATIVE_POSITION) && getTargetState().getTargetDirection().equals(Direction.FORWARD)) 
					|| /* moving to absolute position in reverse */ (getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && getTargetState().getTargetAbsolutePosition().getCanonicalValue() > getTargetState().getStartingAbsolutePosition().getCanonicalValue())
				)
				{
					Logger.debug("Reverse limit switch encountered and position is not the limit, but we're moving away from the limit, so we are leaving it alone.  Changing sensor value from " + getCurrentPosition() + " to " + configuration.getReverseLimit() + ".");
				}
				Logger.debug("Reverse limit switch encountered and position is not the limit.  Changing sensor value from " + getCurrentPosition() + " to " + configuration.getReverseLimit() + "."); 
				resetEncoders = resetEncoderSensorPosition(toSensorUnits(configuration.getReverseLimit()));
			}
		}
		return resetEncoders;
	}

	public void checkOperationComplete() {
 		switch(currentState.getOperation()) {
		case CALIBRATING_SENSOR_IN_DIRECTION:
			// NB: We handle transition out of this state in autoResetSensorPositionIfNecessary, which is already polled by periodic. 
			break;
		case DISABLED:
			// Nothing to do here
			break;
		case HOLDING_CURRENT_POSITION:
			// Nothing to do here
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			// Nothing to do here
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
		case MOVING_TO_RELATIVE_POSITION:
			boolean shouldBeHolding = getTargetState().getCurrentPositionErrorWithin(LengthUOM.Inches.create(0.25));
			if(isUsingPIDSlotIndexForHolding() != shouldBeHolding) {
				Logger.info(this + " updating PID.  We are " + (shouldBeHolding ? "" : "not ") + "close to target.");
				updatePIDSlotIndex(shouldBeHolding);
			}
			break;
		default:
			throw new UnsupportedOperationException(this + " got an unrecognized operation: " + currentState + " in periodic.");		
		}
	}
	/*
	 * We will change behavior as we reach our target...
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#periodic()
	 */
	public void periodic() {
		autoResetSensorPositionIfNecessary();
		checkOperationComplete();
	}
}
