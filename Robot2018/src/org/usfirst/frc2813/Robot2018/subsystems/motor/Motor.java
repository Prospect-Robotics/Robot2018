package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerUnitConversionAdapter;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.pwm.PWM;
import org.usfirst.frc2813.Robot2018.motor.pwm.PWMWithEncoder;
import org.usfirst.frc2813.Robot2018.motor.simulated.Simulated;
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
 *  To run standalone tests, change base class to StandaloneGearheadsSubsystem
 *  
 *  IMPORTANT: This motor class doesn't know about units used by the actual motor!! You should NEVER
 *  be talking about pulses in here.  See the units adapter class which handles the translation layer!!!
 *
 */
public final class Motor extends GearheadsSubsystem implements IMotor {

	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */
	
	private final IMotorConfiguration configuration;
	private final IMotorController hardwareController;
	private final IMotorController simulatedController;
	private IMotorState currentState;
	private IMotorState previousState;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */

	/**
	 * Create a motor subsystem with a VictorSPX
	 * @param configuration The motor configuration specifying how this subsystem shall behave
	 * @param victorSPX The CTRE Victor SPX to be managed 
	 */
	public Motor(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.VictorSPX victorSPX) {
		this.configuration = configuration;
		this.hardwareController  = new MotorControllerUnitConversionAdapter(configuration, new VictorSPX(configuration, victorSPX));
		this.simulatedController = new MotorControllerUnitConversionAdapter(configuration, new Simulated(configuration));
		this.currentState = this.previousState = MotorStateFactory.createDisabled(this);
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	/**
	 * Create a motor subsystem with a TalonSRX
	 * @param configuration The motor configuration specifying how this subsystem shall behave
	 * @param talonSRX The CTRE Talon SRX to be managed 
	 */
	public Motor(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.TalonSRX talonSRX) {
		this.configuration = configuration;
		this.hardwareController = new MotorControllerUnitConversionAdapter(configuration, new TalonSRX(configuration, talonSRX));
		this.simulatedController = new MotorControllerUnitConversionAdapter(configuration, new Simulated(configuration));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	/**
	 * Create a motor subsystem with a PWM speed controller only
	 * @param configuration The motor configuration specifying how this subsystem shall behave
	 * @param speedController The PWM speed controller to be used 
	 */
	public Motor(IMotorConfiguration configuration, PWMSpeedController speedController) {
		this.configuration = configuration;
		this.hardwareController = new MotorControllerUnitConversionAdapter(configuration, new PWM(configuration, speedController));
		this.simulatedController = new MotorControllerUnitConversionAdapter(configuration, new Simulated(configuration));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	/**
	 * Create a motor subsystem with a PWM and an encoder
	 * @param configuration The motor configuration specifying how this subsystem shall behave
	 * @param speedController The PWM speed controller to be used 
	 * @param encoder The encoder to be used 
	 */
	public Motor(IMotorConfiguration configuration, PWMSpeedController speedController, Encoder encoder) {
		this.configuration = configuration;
		this.hardwareController = new MotorControllerUnitConversionAdapter(configuration, new PWMWithEncoder(configuration, speedController, encoder));
		this.simulatedController = new MotorControllerUnitConversionAdapter(configuration, new Simulated(configuration));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
	}

	/**
	 * Create a motor subsystem with a simulated motor controller
	 * @param configuration The motor configuration specifying how this subsystem shall behave
	 */
	public Motor(IMotorConfiguration configuration) {
		this.configuration = configuration;
		this.hardwareController = null;
		this.simulatedController = new MotorControllerUnitConversionAdapter(configuration, new Simulated(configuration));
		this.currentState = MotorStateFactory.createDisabled(this);
		this.previousState = MotorStateFactory.createDisabled(this);
		configure();
		enableEmulator();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - Emulation Support
	 * ---------------------------------------------------------------------------------------------- */

	/**
	 * enable emulation mode. Override to initialize emulation state
	 */
	public void enableEmulator() {
		if(!isEmulated()) {
			super.enableEmulator(); // STANDALONE TESTING: COMMENT OUT THIS LINE
			if(hardwareController != null) {
				Logger.info(this + " is enabling simulation.  Disabling real motor.");
				hardwareController.disable();
			}
			Logger.info(this + " is enabling simulation.  Configuring simulated motor.");
			simulatedController.configure();
		}
	}

	/**
	 * disable emulation mode
	 */
	public void disableEmulator() {
		if(isEmulated()) {
			Logger.info(this + " is disabling simulation.  Disabling simulated motor.");
			simulatedController.disable();
			if(hardwareController == null) {
				Logger.error(this + " was configured with only simulated motor, so you cannot configure real hardware.  Subsystem disabled.");
			} else {
				Logger.info(this + " is disabling simulation.  Reconfiguring real motor.");
				hardwareController.configure();
			}
			super.disableEmulator(); // STANDALONE TESTING: COMMENT OUT THIS LINE
		}
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - State Inspection
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public IMotorConfiguration getConfiguration() {
		return configuration;
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
	 * Get the motor controller driven by this subsystem
	 * @return the motor controller interface 
	 */
	private IMotorController getMotorController() {
		if(isEmulated()) {
			return simulatedController;
		} else {
			 if(hardwareController != null) {
				 return hardwareController;
			 } else {
				 Logger.error(this + " was asked for the motor controller with simulation disabled, but there's no 'real' hardware.  Returning the simulated motor controller.  This is probably bad.");
				 return simulatedController;
			 }
		}
	}
	
	/**
	 * Get the controller's target state
	 * @see IMotorController#getTargetState()
	 * @return the controller's target state (current command) 
	 */
	protected IMotorState getControllerTargetState() {
		return getMotorController().getTargetState();
	}
	/**
	 * Get the controller's previous target state
	 * @see IMotorController#getPreviousTargetState()
	 * @return the controller's previous target state (current command) 
	 */
	protected IMotorState getControllerPreviousTargetState() {
		return getMotorController().getPreviousTargetState();
	}

	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction switchDirection) {
		return getMotorController().getCurrentHardLimitSwitchStatus(switchDirection);
	}

	@Override
	public final String toString() {
		return getConfiguration().getName();
	}
	@Override
	public void dumpDiagnostics() {
		Logger.info(getDiagnostics());
	}
	@Override
	public String getDiagnostics() {
		return String.format("%s - [%s @ %s] [%s]", this, getTargetState(), getCurrentPosition(), getMotorController().getDiagnostics());
	}
	/* ----------------------------------------------------------------------------------------------
	 * Subsystem API
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public final void initDefaultCommand() {
		// Set to hold position by default
		if(getConfiguration().getDefaultCommandFactory() != null) {
			GearheadsCommand c = getConfiguration().getDefaultCommandFactory().createCommand(this);
			if(c != null) {
				setDefaultCommand(c);
				c.setIsDefaultCommand(true);
			}
		}
	}

	@Override
	public void periodic() {
		super.periodic();
		if(isRobotEnabled()) {
			getMotorController().periodic();
		}
		dumpSubsystemStatusAtIntervals();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Public API - Action Commands
	 * ---------------------------------------------------------------------------------------------- */

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
	public boolean calibrateSensorInDirection(Direction targetDirection) {
		return changeState(MotorStateFactory.createCalibrateSensorInDirection(this, targetDirection));
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
		return getMotorController().getCurrentPosition();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Implementation
	 * ---------------------------------------------------------------------------------------------- */

	@Override
	public void configure() {
		if(getConfiguration().hasAll(IMotorConfiguration.Disconnected)) {
			Logger.error("" + this + " has it's motor configured to disconnected.  Not configuring anything.");
			return;
		}
		setName(getConfiguration().getName());
		getMotorController().configure();
	}

	/** Guards for state transitions, called by changeState
	 * IMPORTANT: Do not call directly
	 * @param proposedState The next operating being requested
	 * @return true if it's ok to transition to the next state/command, false to block it.  
	 */	
	protected boolean isStateTransitionAllowed(IMotorState proposedState) {
		if (proposedState.equals(getTargetState()) && proposedState.getOperation() != MotorOperation.DISABLED) {
			Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state.", getTargetState(), proposedState);
			new Exception().printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Execute for state transitions, called by changeState.  
  	 * IMPORTANT: Do not call directly	
	 * @param proposedState The next operating being requested
	 * @return true if we transitioned to the next state/command, false if it failed.  
	 */	
	protected boolean executeTransition(IMotorState proposedState) {
		// Check that state change is actually changing something. If so, do it.
		Logger.info(this + " entering " + proposedState);
		switch(proposedState.getOperation()) {
		case DISABLED:
			getMotorController().disable();
			break;
		case HOLDING_CURRENT_POSITION:
			getMotorController().holdCurrentPosition();
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			getMotorController().moveInDirectionAtRate(proposedState.getTargetDirection(), proposedState.getTargetRate());
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			getMotorController().moveToAbsolutePosition(proposedState.getTargetAbsolutePosition());
			break;
		case MOVING_TO_RELATIVE_POSITION:
			getMotorController().moveToRelativePosition(proposedState.getTargetDirection(), proposedState.getTargetRelativeDistance());
			break;
		case CALIBRATING_SENSOR_IN_DIRECTION:
			getMotorController().calibrateSensorInDirection(proposedState.getTargetDirection());
			break;
		default:
			break;
		}
		return true;
	}
	/**
	 * All device change come through here first.  This is a very high level sanity check
	 * to short-circuit wasted time and effort.  Most of the functionality is in the 
	 * MotorController base class.
	 * 
	 * - We log the request
	 * - We validate that something is allowed transition
	 * - We validate that this is actually a change in command/operation/parameter
	 * - We make the change
	 * - We update our record of the current and previous state
	 * 
	 * @param proposedState The next operating being requested
	 * @return true if state transition happened
	 */
	protected boolean changeState(IMotorState proposedState) {
		Logger.printFormat(LogType.DEBUG, "%s changeState requested: encoderFunctional: %s, current: %s, proposed: %s", this, encoderFunctional, getTargetState(), proposedState);
		if (!encoderFunctional) {
			getMotorController().disable();
			Logger.warning("encoder not functional. Refusing action.");
			return false;	
		}
		if(getConfiguration().hasAll(IMotorConfiguration.Disconnected)) {
			getMotorController().disable();
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
		if(!getControllerTargetState().equals(proposedState)) {
			Logger.info(this + " - Scaling Occurred [Target: " + proposedState + " Controller: " + getControllerTargetState()); 
		}

		Logger.debug(this + " state transition complete.  old: " + getTargetState() + " status: " + proposedState + ".");
		Logger.debug(this + "] " + getDiagnostics());
		this.previousState = this.currentState;
		this.currentState = proposedState;
		// Transition successful, save the state.
		return true;
	}

	
	private static final long DISPLAY_INTERVAL = 2500;
	private long lastPositionReport = System.currentTimeMillis() - DISPLAY_INTERVAL;
	private void dumpSubsystemStatusAtIntervals() {
		if(System.currentTimeMillis() - lastPositionReport >= DISPLAY_INTERVAL) {
			lastPositionReport = System.currentTimeMillis();
			Logger.info("[[PERIODIC]] " + getDiagnostics());
		}
	}

	@Override 
	public Rate getCurrentRate() {
		return getMotorController().getCurrentRate();
	}

	@Override
	public Length getCurrentPositionError() {
		return getMotorController().getCurrentPositionError();
	}

	@Override
	public Rate getCurrentRateError() {
		return getMotorController().getCurrentRateError();
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		return getMotorController().getCurrentRateErrorWithin(marginOfError);
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		return getMotorController().getCurrentPositionErrorWithin(marginOfError);
	}
	@Override
	public boolean isDisconnected() {
		return getConfiguration().hasAll(IMotorConfiguration.Disconnected);
	}
	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection) {
		return getMotorController().getCurrentSoftLimitSwitchStatus(switchDirection);
	}
	@Override
	public Length getPhysicalLimit(Direction direction) {
		return getMotorController().getPhysicalLimit(direction);
	}
	@Override
	public boolean getHasHardLimit(Direction direction) {
		return getMotorController().getHasHardLimit(direction);
	}
	@Override
	public boolean getHasHardOrSoftLimit(Direction direction) {
		return getMotorController().getHasHardOrSoftLimit(direction);
	}
	@Override
	public boolean getHasSoftLimit(Direction direction) {
		return getMotorController().getHasSoftLimit(direction);
	}
	@Override
	public Length getSoftLimit(Direction direction) {
		return getMotorController().getSoftLimit(direction);
	}
	@Override
	public Rate getMinimumForwardRate() {
		return getMotorController().getMinimumForwardRate();
	}
	@Override
	public Rate getMaximumForwardRate() {
		return getMotorController().getMaximumForwardRate();
	}
	@Override
	public Rate getMaximumReverseRate() {
		return getMotorController().getMaximumReverseRate();
	}
	@Override
	public Rate getMinimumReverseRate() {
		return getMotorController().getMinimumReverseRate();
	}
	@Override
	public Rate getMaximumRate(Direction direction) {
		return getMotorController().getMaximumRate(direction);
	}
	@Override
	public Rate getMinimumRate(Direction direction) {
		return getMotorController().getMinimumRate(direction);
	}
	@Override
	public Length getHardLimit(Direction direction) {
		return getMotorController().getHardLimit(direction);
	}
	@Override
	public boolean isHardLimitExceeded(Direction direction) { 
		return getMotorController().isHardLimitExceeded(direction); 
	}
	@Override
	public boolean isHardLimitReached(Direction direction) { 
		return getMotorController().isHardLimitReached(direction); 
	}
	@Override
	public boolean isHardLimitNeedingCalibration(Direction direction) { 
		return getMotorController().isHardLimitNeedingCalibration(direction); 
	}
	@Override
	public boolean isSoftLimitExceeded(Direction direction) { 
		return getMotorController().isSoftLimitExceeded(direction); 
	}
	@Override
	public boolean isSoftLimitReached(Direction direction) { 
		return getMotorController().isSoftLimitReached(direction); 
	}
	@Override
	public boolean isPhysicalLimitExceeded(Direction direction) { 
		return getMotorController().isPhysicalLimitExceeded(direction); 
	}
	@Override
	public boolean isPhysicalLimitReached(Direction direction) { 
		return getMotorController().isPhysicalLimitReached(direction); 
	}
}
