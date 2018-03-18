package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
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

	protected IMotorState currentState = MotorStateFactory.createDisabled(this);
	protected IMotorState previousState = MotorStateFactory.createDisabled(this);

	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	protected AbstractMotorController(IMotorConfiguration configuration) {
		this.configuration = configuration;
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
		autoZeroSensorPositionsIfNecessary();
		return true;
	}
	
	@Override
	public final boolean moveToRelativePosition(Direction direction, Length relativeDistance) {
		// TODO - open loop move
		return false;
	}

	@Override
	public final boolean resetEncoderSensorPosition(Length position) {
		Logger.info("resetEncoderSensorPosition to " + position + ".");
		if(!changeState(MotorStateFactory.createDisabled(this))) {
			Logger.info("Could not change state to disabled.  No resetting sensor position.");
			return false;
		}
		if(!resetEncoderSensorPositionImpl(position)) {
			Logger.error("Failed to read back the sensor position we set.  Leaving motor disabled.  Expected " + position + " but got " + getCurrentPosition());
			return false;
		}
		if(previousState.getOperation().isHoldingCurrentPosition()) {
			Logger.info("Returning to hold position mode from hardware limit switch software reset of sensor.");
			changeState(MotorStateFactory.createHoldingPosition(this));
		} else {
			Logger.info("Leaving motor disabled after reached hardware limit switch while not in holding position mode.");
			return false;
		}
		return true;
	}

	/* ----------------------------------------------------------------------------------------------
	 * Core of implementation
	 * ---------------------------------------------------------------------------------------------- */

	// Guards for state transitions, called by changeState
	// IMPORTANT: Do not call directly	
	protected boolean isStateTransitionAllowed(MotorState proposedState) {
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
		default:
			break;
		
		}

		return true;
	}

	/**
	 * All changes to state are done here, and recorded here.
	 * Optionally reported to the log here.
	 */
	protected final boolean changeState(MotorState proposedState) {
		Logger.printFormat(LogType.DEBUG, "%s Changing state from %s to %s.", this, currentState, proposedState);
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(proposedState)) {
			Logger.printFormat(LogType.WARNING, "%s state transition disallowed.", this);
			return false;
		}

		// Execute the transition
		if(!executeTransition(proposedState)) {
			Logger.printFormat(LogType.WARNING, "%s state transition failed.", this);
			return false;
		}
		
		// Transition successful, save the state.
		this.previousState = this.currentState;
		this.currentState = proposedState;
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
		(configuration.has(IMotorConfiguration.Disconnected)
				? " <<<< DISCONNECTED BY CONFIGURATION >>>>" 
				: (
					" @ " + getCurrentPosition() 
					+ (configuration.has(IMotorConfiguration.LocalReverseHardLimitSwitch) ? " [RLimit=" + getCurrentLimitSwitchStatus(Direction.REVERSE) + "]" : "")
					+ (configuration.has(IMotorConfiguration.LocalForwardHardLimitSwitch) ? " [FLimit=" + getCurrentLimitSwitchStatus(Direction.FORWARD) + "]" : "")
				)
		);
	}

	// Returns true if we zeroed and are now holding position at zero
	protected boolean autoZeroSensorPositionsIfNecessary() {
		boolean resetEncoders = false;
/*
NB: This is doing strange things.  

		if (configuration.has(MotorConfiguration.ForwardHardLimitSwitch) && configuration.getForwardHardLimitSwitchResetsEncoder() && readLimitSwitch(Direction.FORWARD)) {
			if(!readPosition().equals(configuration.getForwardLimit())) {
				Logger.info("Forward limit switch encountered and position is not the limit.  Changing sensor value from " + readPosition() + " to " + configuration.getForwardLimit() + "."); 
				resetEncoderSensorPosition(toSensorUnits(configuration.getForwardLimit()));
			}
			resetEncoders = true; 
		}
		if (configuration.has(MotorConfiguration.ReverseHardLimitSwitch) && configuration.getReverseHardLimitSwitchResetsEncoder() && readLimitSwitch(Direction.NEGATIVE)) {
			if(!readPosition().equals(configuration.getReverseLimit())) {
				Logger.info("Reverse limit switch encountered and position is not the limit.  Changing sensor value from " + readPosition() + " to " + configuration.getReverseLimit() + "."); 
				resetEncoderSensorPosition(toSensorUnits(configuration.getReverseLimit()));
			}
			resetEncoders = true; 
		}
*/		
		return resetEncoders;
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
}
