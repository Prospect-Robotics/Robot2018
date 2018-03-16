package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.talon.TalonPID;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonProfileSlot;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public abstract class AbstractMotorController implements IMotorController {
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */

	// Motor configuration data
	protected final MotorConfiguration configuration;

	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */

	protected MotorState currentState = MotorState.createDisabled();
	protected MotorState previousState = MotorState.createDisabled();
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	protected AbstractMotorController(MotorConfiguration configuration) {
		this.configuration = configuration;
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * API
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public abstract void configure();

	@Override
	public MotorState getState() {
		return currentState;
	}

	@Override
	public MotorState getPreviousState() {
		return previousState;
	}	

	public MotorConfiguration getConfiguration() {
		return configuration;
	}
	// 
	public MotorState getMotorStateWithParameters() {
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
		return changeState(MotorState.createDisabled());
	}

	/**
	 * [ACTION] Set to an absolute encoder position to move to and hold, closed loop
	 */
	@Override
	public final boolean moveToPosition(Length position) {
		return changeState(MotorState.createMovingToPosition(position));
	}
	/*
	 * It is assumed that the units are correct.
	 * If you need a translator, use MotorUnitConversionAdapter in front!
	 */
	@Override
	public final boolean move(Direction direction, Rate rate) {
		if(rate.getValue() == 0) {
			Logger.info(" was told to move with speed zero.  Holding position instead.");
			return holdCurrentPosition();
		} if(rate.getValue() < 0) {
			disable();
			throw new IllegalArgumentException("Move speed must not be negative.  Disabling the motor.");
		}
		else {
			return changeState(MotorState.createMoving(direction, rate));
		}
	}
	/**
	 * [ACTION] Hold the current position, resist movement
	 */
	public final boolean holdCurrentPosition() {
		if(!changeState(MotorState.createHoldingPosition())) 
			return false;
		autoZeroSensorPositionsIfNecessary();
		return true;
	}
	
	@Override
	public final boolean moveADistance(Length distance) {
		// TODO - open loop move
		return false;
	}

	@Override
	public final boolean resetEncoderSensorPosition(Length position) {
		Logger.info("resetEncoderSensorPosition to " + position + ".");
		if(!changeState(MotorState.createDisabled())) {
			Logger.info("Could not change state to disabled.  No resetting sensor position.");
			return false;
		}
		if(!resetEncoderSensorPositionImpl(position)) {
			Logger.error("Failed to read back the sensor position we set.  Leaving motor disabled.  Expected " + position + " but got " + readPosition());
			return false;
		}
		if(previousState.getOperation().isHoldingCurrentPosition()) {
			Logger.info("Returning to hold position mode from hardware limit switch software reset of sensor.");
			changeState(MotorState.createHoldingPosition());
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
			if(!configuration.hasAll(MotorConfiguration.ControlPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + MotorConfiguration.ControlPosition + " capability.  Refusing request for " + proposedState + ".");
			}
			break;
		case MOVING:
			if(!configuration.hasAll(MotorConfiguration.ControlDirection)) {
				throw new UnsupportedOperationException(this + " does not have the " + MotorConfiguration.ControlDirection + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getDirection().isPositive() && !configuration.hasAll(MotorConfiguration.Forward)) {
				throw new UnsupportedOperationException(this + " does not have the " + MotorConfiguration.Forward + " capability.  Refusing request for " + proposedState + ".");
			}
			if(proposedState.getDirection().isNegative() && !configuration.hasAll(MotorConfiguration.Reverse)) {
				throw new UnsupportedOperationException(this + " does not have the " + MotorConfiguration.Reverse + " capability.  Refusing request for " + proposedState + ".");
			}
			if(!configuration.hasAll(MotorConfiguration.ControlRate)) {
				Logger.warning(this + " does not have the " + MotorConfiguration.ControlRate + " capability.  Rate will be ignored.");
			}
			if(proposedState.getRate().getValue() < 0) {
				throw new IllegalArgumentException(this + " was asked to " + proposedState + ", but negative rate is not supported.  Use the direction parameter instead.");
			}
			break;
		case MOVING_TO_POSITION:
			if(!configuration.hasAll(MotorConfiguration.ControlPosition)) {
				throw new UnsupportedOperationException(this + " does not have the " + MotorConfiguration.ControlPosition + " capability.  Refusing request for " + proposedState + ".");
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
		Logger.formatDebug("%s Changing state from %s to %s.", this, currentState, proposedState);
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(proposedState)) {
			Logger.formatWarning("%s state transition disallowed.", this);
			return false;
		}

		// Execute the transition
		if(!executeTransition(proposedState)) {
			Logger.formatWarning("%s state transition failed.", this);
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
		return this + " " + getState() + 
		(configuration.has(MotorConfiguration.Disconnected)
				? " <<<< DISCONNECTED BY CONFIGURATION >>>>" 
				: (
					" @ " + readPosition() 
					+ (configuration.has(MotorConfiguration.ReverseHardLimitSwitch) ? " [RLimit=" + readLimitSwitch(Direction.REVERSE) + "]" : "")
					+ (configuration.has(MotorConfiguration.ForwardHardLimitSwitch) ? " [FLimit=" + readLimitSwitch(Direction.FORWARD) + "]" : "")
				)
		);
	}

	// Returns true if we zeroed and are now holding position at zero
	protected boolean autoZeroSensorPositionsIfNecessary() {
		boolean resetEncoders = false;
		if (configuration.has(MotorConfiguration.ForwardHardLimitSwitch) && configuration.getForwardHardLimitSwitchResetsEncoder() && readLimitSwitch(Direction.FORWARD)) {
			if(!readPosition().equals(configuration.getForwardLimit())) {
				Logger.info("Forward limit switch encountered and position is not the limit.  Changing sensor value from " + readPosition() + " to " + configuration.getForwardSoftLimit() + "."); 
				resetEncoderSensorPosition(toSensorUnits(configuration.getForwardSoftLimit()));
			}
			resetEncoders = true; 
		}
		if (configuration.has(MotorConfiguration.ReverseHardLimitSwitch) && configuration.getReverseHardLimitSwitchResetsEncoder() && readLimitSwitch(Direction.NEGATIVE)) {
			if(!readPosition().equals(configuration.getReverseLimit())) {
				Logger.info("Reverse limit switch encountered and position is not the limit.  Changing sensor value from " + readPosition() + " to " + configuration.getReverseSoftLimit() + "."); 
				resetEncoderSensorPosition(toSensorUnits(configuration.getReverseSoftLimit()));
			}
			resetEncoders = true; 
		}
		return resetEncoders;
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
	protected abstract boolean executeTransition(MotorState proposedState);
}
