package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.MotorState;
import org.usfirst.frc2813.Robot2018.motor.MotorUnitConversionAdapter;
import org.usfirst.frc2813.Robot2018.motor.talon.Talon;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonSensorPhase;
import org.usfirst.frc2813.Robot2018.motor.victor.Victor;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * Generalized motor subsystem.
 * 
 * The motor is given a name and a configuration.  Then you can use the
 * capabilities described in the configuration.
 *   
 * General Motor commands are found in the org.usfirst.frc2813.Robot2018.commands.motor package 
 *
 */
public final class Motor extends GearheadsSubsystem {
	static {
		Logger.addMe();
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */
	
	private final IMotorController controller;

	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public Motor(MotorConfiguration configuration, VictorSPX victorSPX) {
		this.controller = new MotorUnitConversionAdapter(configuration, new Victor(configuration, victorSPX));
		configure();
	}

	public Motor(MotorConfiguration configuration, TalonSRX talonSRX) {
		this.controller = new MotorUnitConversionAdapter(configuration, new Talon(configuration, talonSRX));
		configure();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - State Inspection
	 * ---------------------------------------------------------------------------------------------- */

	public MotorConfiguration getConfiguration() {
		return controller.getConfiguration();
	}
	// 
	public MotorState getState() {
		return controller.getState();
	}
	public MotorState getPreviousState() {
		return controller.getState();
	}
	// What is the state of the limit switch (if applicable)
	public boolean readLimitSwitch(Direction switchDirection) {
		return controller.readLimitSwitch(switchDirection);
	}
	// Returns the speed if we are moving, otherwise null
	public final Rate getSpeed() {
		return getState().getRate();
	}
	// Returns the position if we are moving, otherwise null.
	public final Length getPosition() {
		return getState().getPosition();
	}
	// Returns the direction if we are moving in a direction (NOT holding a position or moving to a position!)
	public final Direction getDirection() {
		return getState().getDirection();
	}
	// What's my name?
	public final String toString() {
		return getConfiguration().getName();
	}
	/*
	 * Dump our state
	 */
	public void dumpState() {
		Logger.info(String.format("%s: %s", this, getState()));
	}

	/* ----------------------------------------------------------------------------------------------
	 * Subsystem API
	 * ---------------------------------------------------------------------------------------------- */

	// Load default command, if configured
	public final void initDefaultCommand() {
		// Set to hold position by default
		if(getConfiguration().getDefaultCommandFactory() != null) {
			setDefaultCommand(getConfiguration().getDefaultCommandFactory().createCommand(this));
		}
	}

	// Periodic
	public final void periodic() {
		super.periodic();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - Action Commands
	 * ---------------------------------------------------------------------------------------------- */
	
	/*
	 * [ACTION] Change speed while moving.  If we are not moving, has no effect
	 * NB: This is a wrapper around moveInDirecectionAtSpeed, for convenience
	 * ability to alter speed but not direction AND if we aren't moving be able
	 * to call it safely without initiating any movement.
	 */
	public final void changeSpeed(Rate newSpeed) {
		if(newSpeed.getValue() < 0) {
			throw new IllegalArgumentException("moveInDirectionAtSpeed does not accept negative rates.  Change the direction instead.");
		}
		if(getState().getOperation() == MotorOperation.MOVING) {
			// Keep moving, call the official function
			moveInDirectionAtSpeed(getState().getDirection(), newSpeed);
		} else {
			Logger.info(getConfiguration().getName() + " was asked to change speed to " + newSpeed + ", but we aren't moving so we won't do it.");
		}
	}

	/*
	 *  [ACTION] Do whatever we are testing today...
	 */
	public final void encoderRelativePositionTestingMode() {
		dumpState();
	}
	/**
	 *  [ACTION] Disable the device. Required to handle run away bots
	 */
	public void disable() {
		changeState(MotorState.createDisabled());
	}

	/**
	 *  [ACTION] Move in direction at speed
	 * @param newSpeed
	 * @param newDirection
	 */
	public void moveInDirectionAtSpeed(Direction direction, Rate rate) {
		if(rate.getValue() < 0) {
			throw new IllegalArgumentException("moveInDirectionAtSpeed does not accept negative rates.  Change the direction instead.");
		}
		changeState(MotorState.createMoving(direction, rate));
	}

	/**
	 *  [ACTION] Move in direction at speed
	 * @param newSpeed
	 * @param newDirection
	 */
	public void moveInDirectionAtDefaultSpeed(Direction direction) {
		changeState(MotorState.createMoving(direction, getConfiguration().getDefaultRate()));
	}

	/**
	 *  [ACTION] Move to the absolutePosition
	 */
	public void moveToPosition(Length position) {
		changeState(MotorState.createMovingToPosition(position));
	}

	/**
	 * [ACTION] Fight to hold the current position
	 */
	public void holdCurrentPosition() {
		changeState(MotorState.createHoldingPosition());
	}
	
	// 
	// Get controller position in subsystem units
	public final Length readPosition() {
		return toSubsystemUnits(controller.readPosition());
	}

	/* ----------------------------------------------------------------------------------------------
	 * Implementation
	 * ---------------------------------------------------------------------------------------------- */

	// Configure the motor as specified in our configuration
	protected void configure() {
		controller.configure();
	}

	// Guards for state transitions, called by changeState
	// IMPORTANT: Do not call directly	
	protected boolean isStateTransitionAllowed(MotorState proposedState) {
		if (proposedState.equals(getState())) {
			Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state.", getState(), proposedState);
			new Exception().printStackTrace();
			return false;
		}
		return true;
	}
	// Execute for state transitions, called by changeState.  
	// IMPORTANT: Do not call directly	
	protected boolean executeTransition(MotorState proposedState) {
		// Check that state change is actually changing something. If so, do it.
		Logger.info(this + " entering " + proposedState + " state.");
		switch(proposedState.getOperation()) {
		case DISABLED:
			controller.disable();
			break;
		case HOLDING_CURRENT_POSITION:
			controller.holdCurrentPosition();
			break;
		case MOVING:
			controller.move(proposedState.getDirection(), proposedState.getRate());
			break;
		case MOVING_TO_POSITION:
			controller.moveToPosition(proposedState.getPosition());
			break;
		}
		return true;
	}
	/**
	 * All device change come through here first.
	 * Log request
	 * Validate the actual actually changes something
	 * make the change
	 * flush old state so we do not get confused next time
	 * 
	 * @param newState
	 * NOTE: state variables other than the state enum are already updated,
	 * with the old state in oldSpeed, oldPosition and oldDirection
	 * @return true if state change occurred
	 */
	protected boolean changeState(MotorState proposedState) {
		Logger.formatDebug("%s changeState requested: encoderFunctional: %s, current: %s, proposed: %s", this, encoderFunctional, getState(), proposedState);
		if (!encoderFunctional) {
			controller.disable();
			Logger.warning("encoder not functional. Refusing action.");
			return false;	
		}
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(proposedState)) {
			Logger.warning("%s state transition aborted.", this);
			return false;
		}

		// Execute the transition
		if(!executeTransition(proposedState)) {
			Logger.warning("%s state transition failed.", this);
			return false;
		}

		// Check the result
		if(!getState().equals(proposedState)) {
			Logger.warning("%s state transition may have failed.  Expected %s but got %s.", this, proposedState, getState());
			return false;
		}
		
		Logger.info("%s state transition complete.  old: %s new: %s.", this, getState(), getPreviousState());
		// Transition successful, save the state.
		return true;
	}
	/* ----------------------------------------------------------------------------------------------
	 * Units Helpers
	 * ---------------------------------------------------------------------------------------------- */
	
	// Convert a length to sensor units
	protected Length toSensorUnits(Length l) {
		return l.convertTo(getConfiguration().getNativeSensorLengthUOM());
	}
	// Convert a length to motor units	
	protected Length toMotorUnits(Length l) {
		return l.convertTo(getConfiguration().getNativeMotorLengthUOM());
	}	
	// Convert a length to display units	
	protected Length toSubsystemUnits(Length l) {
		return l.convertTo(getConfiguration().getNativeDisplayLengthUOM());
	}
	// Convert a length to sensor units
	protected Rate toSensorUnits(Rate l) {
		return l.convertTo(getConfiguration().getNativeSensorRateUOM());
	}
	// Convert a length to motor units	
	protected Rate toMotorUnits(Rate l) {
		return l.convertTo(getConfiguration().getNativeMotorRateUOM());
	}
	// Convert a length to display units	
	protected Rate toSubsystemUnits(Rate l) {
		return l.convertTo(getConfiguration().getNativeDisplayRateUOM());
	}
}
