package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerUnitConversionAdapter;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.pwm.PWM;
import org.usfirst.frc2813.Robot2018.motor.pwm.PWMWithEncoder;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonSRX;
import org.usfirst.frc2813.Robot2018.motor.victor.VictorSPX;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PWMSpeedController;

/**
 * Generalized motor subsystem.
 * 
 * The motor is given a name and a configuration.  Then you can use the
 * capabilities described in the configuration.
 *   
 * General Motor commands are found in the org.usfirst.frc2813.Robot2018.commands.motor package
 * 
 *  Nomenclature:
 *  
 *  - get/set "target" values -- read the current objective.  If we are going to a position and/or at a 
 *    a particular speed, the motor is going to try to use it's closed loop logic to maintain the 
 *    speed and/or reach the target position.  It may in fact go up and down in either speed and may
 *    overshoot or oscillate on position.  The "target" values are what you told it to try to do.
 *    
 *  - get/set "current' values -- state read directly from the motor controller on what it is actually
 *    doing at this moment.  In some cases we can't actually get up to the moment status on the closed
 *    loop behavior, but wherever we can - we will use "current".
 *    
 *    Talon can give you 'current' position and rate information.  In closed loop modes, depending on 
 *    the mode in use, we can often get the closed loop error to determine whether we are going
 *    'forward' or 'reverse' at that moment in time. 
 *    
 *    NOTE: Because we have getTargetState() there will be no "getTargetXXX()" functions for parameters
 *    of the current target state, previous target state, etc. as those are redundant and 
 *    unnecessarily complicate things.
 *
 */
public final class Motor extends GearheadsSubsystem implements IMotor {

	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */
	
	private final IMotorController controller;
	private IMotorState currentState;
	private IMotorState previousState;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public Motor(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.VictorSPX victorSPX) {
		this.controller = new MotorControllerUnitConversionAdapter(configuration, new VictorSPX(configuration, victorSPX));
		this.currentState = this.previousState = MotorStateFactory.createDisabled(this);
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	public Motor(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.TalonSRX talonSRX) {
		this.controller = new MotorControllerUnitConversionAdapter(configuration, new TalonSRX(configuration, talonSRX));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	public Motor(IMotorConfiguration configuration, PWMSpeedController speedController) {
		this.controller = new MotorControllerUnitConversionAdapter(configuration, new PWM(configuration, speedController));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	public Motor(IMotorConfiguration configuration, PWMSpeedController speedController, Encoder sensor) {
		this.controller = new MotorControllerUnitConversionAdapter(configuration, new PWMWithEncoder(configuration, speedController, sensor));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - State Inspection
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public IMotorConfiguration getConfiguration() {
		return controller.getConfiguration();
	}

	@Override
	public IMotorState getTargetState() {
		return currentState;
	}
	
	@Override
	public IMotorState getPreviousTargetState() {
		return previousState;
	}
	
	/**
	 * Get the controller's target state
	 * @see IMotorController.getTargetState()
	 */
	public IMotorState getControllerState() {
		return controller.getTargetState();
	}
	/**
	 * Get the controller's previous target state
	 * @see IMotorController.getPreviousTargetState()
	 */
	public IMotorState getControllerPreviousTargetState() {
		return controller.getPreviousTargetState();
	}
	// What is the state of the limit switch (if applicable)
	public boolean getCurrentLimitSwitchStatus(Direction switchDirection) {
		return controller.getCurrentLimitSwitchStatus(switchDirection);
	}
	// Returns the speed if we are moving, otherwise null
	public final Rate getTargetSpeed() {
		return getTargetState().getTargetRate();
	}
	// Returns the position if we are moving, otherwise null.
	public final Length getTargetPosition() {
		return getTargetState().getTargetAbsolutePosition();
	}
	// Returns the direction if we are moving in a direction (NOT holding a position or moving to a position!)
	public final Direction getTargetDirection() {
		return getTargetState().getTargetDirection();
	}
	// What's my name?
	public final String toString() {
		return getConfiguration().getName();
	}
	/*
	 * Dump our state
	 */
	public void dumpDiagnostics() {
		Logger.info(getDiagnostics());
	}

	public String getDiagnostics() {
		return String.format("%s - [%s @ %s] [%s]", this, getTargetState(), getCurrentPosition(), controller.getDiagnostics());
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
	public final void setTargetRate(Rate newSpeed) {
		if(newSpeed.getValue() < 0) {
			throw new IllegalArgumentException("moveInDirectionAtSpeed does not accept negative rates.  Change the direction instead.");
		}
		if(getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE) {
			// Keep moving, call the official function
			moveInDirectionAtRate(getTargetState().getTargetDirection(), newSpeed);
		} else {
			Logger.info(getConfiguration().getName() + " was asked to change speed to " + newSpeed + ", but we aren't moving so we won't do it.");
		}
	}

	/*
	 *  [ACTION] Do whatever we are testing today...
	 */
	public final void encoderRelativePositionTestingMode() {
		controller.resetEncoderSensorPosition(getConfiguration().getNativeSensorLengthUOM().create(0));
		dumpDiagnostics();
	}
	
	@Override
	public boolean disable() {
		return changeState(MotorStateFactory.createDisabled(this));
	}

	@Override
	public boolean moveInDirectionAtRate(Direction direction, Rate rate) {
		if(rate.getValue() < 0) {
			throw new IllegalArgumentException("moveInDirectionAtSpeed does not accept negative rates.  Change the direction instead.");
		}
		return changeState(MotorStateFactory.createMovingInDirectionAtRate(this, direction, rate));
	}

	@Override
	public boolean moveInDirectionAtDefaultRate(Direction direction) {
		return changeState(MotorStateFactory.createMovingInDirectionAtRate(this, direction, getConfiguration().getDefaultRate()));
	}

	@Override
	public boolean moveToAbsolutePosition(Length absolutePosition) {
		return changeState(MotorStateFactory.createMovingToAbsolutePosition(this, absolutePosition));
	}

	@Override
	public boolean holdCurrentPosition() {
		return changeState(MotorStateFactory.createHoldingPosition(this));
	}

	@Override
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance) {
		return changeState(MotorStateFactory.createMovingToRelativePosition(this, direction, relativeDistance));
	}

	@Override
	public final Length getCurrentPosition() {
		return toSubsystemUnits(controller.getCurrentPosition());
	}

	/* ----------------------------------------------------------------------------------------------
	 * Implementation
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public void configure() {
		if(getConfiguration().has(IMotorConfiguration.Disconnected)) {
			Logger.error("" + this + " has it's motor configured to disconnected.  Not configuring anything.");
			return;
		}
		setName(getConfiguration().getName());
		controller.configure();
	}

	// Guards for state transitions, called by changeState
	// IMPORTANT: Do not call directly	
	protected boolean isStateTransitionAllowed(IMotorState proposedState) {
		if (proposedState.equals(getTargetState()) && proposedState.getOperation() != MotorOperation.DISABLED) {
			Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state.", getTargetState(), proposedState);
			new Exception().printStackTrace();
			return false;
		}
		return true;
	}
	
	// Execute for state transitions, called by changeState.  
	// IMPORTANT: Do not call directly	
	protected boolean executeTransition(IMotorState proposedState) {
		// Check that state change is actually changing something. If so, do it.
		Logger.info(this + " entering " + proposedState);
		switch(proposedState.getOperation()) {
		case DISABLED:
			(new Throwable()).printStackTrace();
			controller.disable();
			break;
		case HOLDING_CURRENT_POSITION:
			controller.holdCurrentPosition();
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			controller.moveInDirectionAtRate(proposedState.getTargetDirection(), proposedState.getTargetRate());
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			controller.moveToAbsolutePosition(proposedState.getTargetAbsolutePosition());
			break;
		case MOVING_TO_RELATIVE_POSITION:
			controller.moveToRelativePosition(proposedState.getTargetDirection(), proposedState.getTargetRelativeDistance());
			break;
		default:
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
	protected boolean changeState(IMotorState proposedState) {
		Logger.printFormat(LogType.DEBUG, "%s changeState requested: encoderFunctional: %s, current: %s, proposed: %s", this, encoderFunctional, getTargetState(), proposedState);
		if (!encoderFunctional) {
			controller.disable();
			Logger.warning("encoder not functional. Refusing action.");
			return false;	
		}
		if(getConfiguration().has(IMotorConfiguration.Disconnected)) {
			controller.disable();
			Logger.warning("Motor configuration says it's disconnected. Refusing action.");
			return false;	
		}
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(proposedState)) {
			Logger.warning(this + " state transition aborted.");
			return false;
		}

		// Execute the transition
		if(!executeTransition(proposedState)) {
			Logger.warning(this + " state transition failed.");
			return false;
		}

		// See if there was any translation and report on the alterations (units typically)
		if(!getControllerState().equals(proposedState)) {
			Logger.info(this + " - Translation Occurred [Target: " + proposedState + " Controller: " + getControllerState()); 
		}
		
		Logger.debug(this + " state transition complete.  old: " + getTargetState() + " status: " + proposedState + ".");
		Logger.debug(this + "] " + getDiagnostics());
		this.previousState = this.currentState;
		this.currentState = proposedState;
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

	/**
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#getCurrentRate()
	 */
	@Override 
	public Rate getCurrentRate() {
		return controller.getCurrentRate();
	}

	/*
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#getCurrentPositionError()
	 */
	@Override
	public Length getCurrentPositionError() {
		return controller.getCurrentPositionError();
	}

	/*
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#getCurrentRateError()
	 */
	@Override
	public Rate getCurrentRateError() {
		return controller.getCurrentRateError();
	}

	/**
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#getCurrentRateErrorWithin(Rate)
	 */
	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		return controller.getCurrentRateErrorWithin(marginOfError);
	}

	/**
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotor#getCurrentPositionErrorWithin(Length)
	 */
	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		return controller.getCurrentPositionErrorWithin(marginOfError);
	}
}
