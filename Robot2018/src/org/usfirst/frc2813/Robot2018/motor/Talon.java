package org.usfirst.frc2813.Robot2018.motor;

import java.util.logging.Logger;

import org.usfirst.frc2813.util.unit.Direction;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * A wrapper class to handle an SRX Talon motor controller
 * Arm rotation has default halted state. Move command takes
 * TODO: Create a base class above this for motor controllers.
 * TODO: This class assumes a MAG SRX encoder is attached in some places.
 */
public class Talon {
	private final TalonSRX                   srx;
	private final Logger                     logger;
	private final TalonSensorPhase           sensorPhase;    // NB: This must never change and must be known at construction
	private final TalonMotorInversion        motorInversion; // NB: This must never change and must be known at construction

	// NB: This is not yet in the firmware apparently, so enable this when it's ready
	public static final boolean AUXILLIARY_PID_SUPPORTED = false;
	// NB: There are other values for different types of sensor.  There's a table.  
	public static final double SRX_MAG_ENCODER_PULSES_PER_REVOLUTION = 4096;
	// NB: Talon SRX matches encoder pulses when it's using the relative sensor
	public static final double TALON_SRX_OUTPUTR_PULSES_PER_REVOLUTION = 4096;
	// Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops.
	// During initialization we use 10ms timeout to setup commands
	public static final int CONFIGURATION_COMMAND_TIMEOUT_MS = 10; // timeout for Talon config function
	// During operation, we use 0ms timeout to avoid stalling on transient bus
	// errors, per CTR examples/documentation.
	public static final int OPERATION_COMMAND_TIMEOUT_MS = 0; // timeout for Talon config function
	// choose to ensure sensor is positive when output is positive
	public static final TalonSensorPhase DEFAULT_SENSOR_PHASE = TalonSensorPhase.Normal;
	// choose based on what direction you want to be positive, this does not affect
	// motor invert.
	public static final TalonMotorInversion DEFAULT_MOTOR_INVERSION = TalonMotorInversion.Normal;
	public static final LimitSwitchNormal DEFAULT_LIMIT_SWITCH_MODE = LimitSwitchNormal.NormallyOpen; // From manual
	public static final LimitSwitchSource DEFAULT_LIMIT_SWITCH_SOURCE = LimitSwitchSource.FeedbackConnector; // From manual
	public static final boolean DEFAULT_LIMIT_SWITCH_CLEAR_POSITION = false; // From manual
	public static final boolean DEFAULT_SOFT_LIMIT_ENABLED = false;
	public static final boolean DEFAULT_CLEAR_POSITION_ON_QUAD_IDX = false;
	public static final int DEFAULT_SOFT_LIMIT_THRESHOLD = 0;
	public static final int DEFAULT_ALLOWABLE_CLOSED_LOOP_ERROR = 0;
	public static final TalonProfileSlot DEFAULT_PROFILE_SLOT_FOR_HOLD_POSITION = TalonProfileSlot.HoldingPosition;
	public static final TalonProfileSlot DEFAULT_PROFILE_SLOT_FOR_MOVE          = TalonProfileSlot.Moving;
	public static final TalonPID DEFAULT_PID_INDEX_FOR_HOLD_POSITION            = TalonPID.Primary;
	public static final TalonPID DEFAULT_PID_INDEX_FOR_MOVE                     = TalonPID.Primary;
	public static final double DEFAULT_F_GAIN = 0; // From manual
	public static final double DEFAULT_P_GAIN = 0.0; // From manual
	public static final double DEFAULT_I_GAIN = 0.0; // From manual
	public static final double DEFAULT_D_GAIN = 0.0; // From manual

	// TODO: Warning: If we use SET_POSITION --- which profile do we use?  moving or holding?  Needs to transition automatically based on error!!!  

	// Current state
	private MotorControllerState state;
	// Last control mode send to controller
	private ControlMode lastControlMode = ControlMode.Position; // Remember last assigned control mode, help us implement state transitions
	// Last arg sent for controlMode
	private double lastControlModeValue = 0;
	private TalonProfileSlot lastSlotIndex = DEFAULT_PROFILE_SLOT_FOR_HOLD_POSITION;
	private TalonPID         lastPIDIndex  = DEFAULT_PID_INDEX_FOR_HOLD_POSITION;
	private TalonProfileSlot slotIndexForHoldPosition = DEFAULT_PROFILE_SLOT_FOR_HOLD_POSITION;
	private TalonProfileSlot slotIndexForMove         = DEFAULT_PROFILE_SLOT_FOR_MOVE;
	private TalonPID         PIDIndexForMove          = DEFAULT_PID_INDEX_FOR_MOVE;
	private TalonPID         PIDIndexForHoldPosition  = DEFAULT_PID_INDEX_FOR_HOLD_POSITION;

	public double getEncoderTicksPerRevolution() {
		// TODO: switch(getSelectedSensor(lastPIDIndex)) and return correct value based on encoder
		return SRX_MAG_ENCODER_PULSES_PER_REVOLUTION;
	}
	
	public TalonProfileSlot getSlotIndexForHoldPosition() {
		return slotIndexForHoldPosition;
	}
	public TalonProfileSlot getSlotIndexForMove() {
		return slotIndexForMove;
	}
	public void setSlotIndexForHoldPosition(TalonProfileSlot slotIndex) {
		slotIndexForHoldPosition = slotIndex;
		if((state == MotorControllerState.HOLDING_POSITION || state == MotorControllerState.SET_POSITION) && lastSlotIndex != slotIndex) {
			// TODO: Need to update state
			logger_info("TODO: Need to updateSate after slot index for holding is changed.");
		}
	}
	public void setSlotIndexForMove(TalonProfileSlot slotIndex) {
		slotIndexForMove = slotIndex;
		if((state == MotorControllerState.MOVING || state == MotorControllerState.SET_POSITION) && lastSlotIndex != slotIndex) {
			// NB: SET_POSITION needs to use the profile for "moving" when it's got a large error and switch to the profile for holding when the error is small
			// TODO: Need to update state
			logger_info("TODO: Need to updateSate after slot index for move changes while in moving or when high error in set_position state...");
		}
	}

	public Talon(TalonSRX srx, Logger logger, TalonMotorInversion motorInversion, TalonSensorPhase sensorPhase) {
		this.srx = srx;
		this.logger = logger;
		this.motorInversion = motorInversion;
		this.sensorPhase = sensorPhase;

		// set the peak and nominal outputs, 12V means full
		srx.configNominalOutputForward(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configNominalOutputReverse(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputForward(1, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputReverse(-1, CONFIGURATION_COMMAND_TIMEOUT_MS);
//		correctRelativeEncodersFromAbsolute();
		// Reset PID controllers
		configurePID(TalonProfileSlot.ProfileSlot0);
		configurePID(TalonProfileSlot.ProfileSlot1);
		configurePID(TalonProfileSlot.ProfileSlot2);
		configurePID(TalonProfileSlot.ProfileSlot3);
		/*
		 * set the allowable closed-loop error, Closed-Loop output will be neutral
		 * within this range. See Table in Section 17.2.1 for native units per rotation.
		 */
		configureAllowableClosedLoopError(TalonProfileSlot.ProfileSlot0);
		configureAllowableClosedLoopError(TalonProfileSlot.ProfileSlot1);
		configureAllowableClosedLoopError(TalonProfileSlot.ProfileSlot2);
		configureAllowableClosedLoopError(TalonProfileSlot.ProfileSlot3);
		// Configure limit switches on startup to all off
		resetHardLimitSwitchClearsPositionAutomatically(Direction.POSITIVE); // reset defalts
		resetHardLimitSwitchClearsPositionAutomatically(Direction.NEGATIVE); // reset defalts
		disableSoftLimitSwitch(Direction.POSITIVE); // reset defaults
		disableSoftLimitSwitch(Direction.NEGATIVE); // reset defaults
		configureHardLimitSwitch(Direction.POSITIVE); // reset defaults
		configureHardLimitSwitch(Direction.NEGATIVE); // reset defaults
		// Disable clearing position on quad index, we don't support/use it and this restores SRX default.
		configureClearPositionOnQuadIdx();
		// Start disabled
		set(MotorControllerState.DISABLED, 0);
	}

	void logger_info(String x) {
		System.out.println("Talon: " + x);
	}
	public Talon(TalonSRX srx, Logger logger) {
		this(srx, logger, DEFAULT_MOTOR_INVERSION, DEFAULT_SENSOR_PHASE);
	}

	public MotorControllerState getState() {
		return state;
	}

	/*
	 * Configure the soft limit switch.  Threshold is in pulses, see PULSES_PER_REVOLUTION (4096/rotation). 
	 */
	public void configureSoftLimitSwitch(Direction direction, boolean enabled, int thresholdInPulses) {
		if (direction.isNegative()) {
			srx.configReverseSoftLimitEnable(enabled, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configReverseSoftLimitThreshold(thresholdInPulses, CONFIGURATION_COMMAND_TIMEOUT_MS);
		} else {
			srx.configForwardSoftLimitEnable(enabled, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configForwardSoftLimitThreshold(thresholdInPulses, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}
	/*
	 * Enable the soft limit and set the threshold.
	 */
	public void configureSoftLimitSwitch(Direction direction, int thresholdInPulses) {
		configureSoftLimitSwitch(direction, true, thresholdInPulses);	
	}
	/*
	 * Reset soft limit switch to OFF, threshold zero.
	 * NOTE: Calling this is equivalent to disableSoftLimitSwitch.
	 */
	public void configureSoftLimitSwitch(Direction direction) {
		configureSoftLimitSwitch(direction, DEFAULT_SOFT_LIMIT_ENABLED, DEFAULT_SOFT_LIMIT_THRESHOLD);	
	}
	/*
	 * Disable the soft limit and reset the threshold
	 */
	public void disableSoftLimitSwitch(Direction direction) {
		configureSoftLimitSwitch(direction, false, DEFAULT_SOFT_LIMIT_THRESHOLD);
	}

	/*
	 * Toggle whether the hard limit resets the position sensor automatically.  Default is off.
	 */
	public void setHardLimitSwitchClearsPositionAutomatically(Direction direction, boolean clearPositionAutomatically) {
			ParamEnum parameter = direction.isNegative() ? ParamEnum.eClearPositionOnLimitR : ParamEnum.eClearPositionOnLimitF;
			srx.configSetParameter(
					parameter, 
					clearPositionAutomatically ? 1 : 0, 
					0 /* unused */, 
					0 /* unused */, 
					CONFIGURATION_COMMAND_TIMEOUT_MS);
	}
	/*
	 * Configure hardware limit switch to default behavior - NOT auto clear the sensor position when activated. 
	 */
	public void resetHardLimitSwitchClearsPositionAutomatically(Direction direction) {
		setHardLimitSwitchClearsPositionAutomatically(direction, DEFAULT_LIMIT_SWITCH_CLEAR_POSITION);
	}
	/*
	 * Configure whether quad encoder's index pin will automatically clear the encoder positions
	 */
	public void configureClearPositionOnQuadIdx(boolean enabled) {
		srx.configSetParameter(ParamEnum.eClearPositionOnQuadIdx, enabled ? 1 : 0, 0 /* unused */, 0 /* unused */, CONFIGURATION_COMMAND_TIMEOUT_MS);
	}	
	/*
	 * Reset to default, do not clear sensor position on quad encoder's index pin
	 */
	public void configureClearPositionOnQuadIdx() {
		configureClearPositionOnQuadIdx(DEFAULT_CLEAR_POSITION_ON_QUAD_IDX);
	}
	/*
	 * Set the "hard" limit switch connection and logic.  You can set the source to Disabled,
	 * Notes: 
	 *     Set limitSwitchSource to Deactivated to disable the feature entirely.
	 *     You can choose
	 *         Deactivated - This is typically not needed because default behavior is to expect a hard-wired limit switch with "normally open" logic, which means if there's no switch then nothing happens.
	 *                       You may want to use this to be able to have limit switches control a "typical range of motion" that is less than the physical range of motion.
	 *                       Example: Use limits to keep arm within +/- 30 degrees during tele-op, but use a soft limit in an auto firing sequence to go way back beyond the typical range to "fire over the shoulder".
	 *         FeedbackConnector - The physically wired switch 
	 *         RemoteTalonSRX - Another Talon (think follower mode + keep encoders sync'd together with one physical switch) 
	 *         RemoteCANifier - This is a CAN connected limit switch
	 */
	public void configureHardLimitSwitch(Direction direction, LimitSwitchSource limitSwitchSource, LimitSwitchNormal limitSwitchMode) {
		if (direction.isNegative()) {
			srx.configReverseLimitSwitchSource(limitSwitchSource, limitSwitchMode, CONFIGURATION_COMMAND_TIMEOUT_MS);
		} else {
			srx.configForwardLimitSwitchSource(limitSwitchSource, limitSwitchMode, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}
	/*
	 * Set the allowable closed loop error for the PID profile indicated.  
	 * When PID is running with this profile, this is how much error it will accept before reacting (I think)
	 * See getClosedLoopError(TalonPidLoopIndex) to see the current actual error.
	 */
	public void configureAllowableClosedLoopError(TalonProfileSlot profileSlot, int allowableClosedLoopError) {
		srx.configAllowableClosedloopError(profileSlot.getProfileSlotIndex(), allowableClosedLoopError, CONFIGURATION_COMMAND_TIMEOUT_MS);
	}
	/*
	 * Reset the allowable closed loop error for the indicated profile to default.  
	 */
	public void configureAllowableClosedLoopError(TalonProfileSlot profileSlot) {
		configureAllowableClosedLoopError(profileSlot, DEFAULT_ALLOWABLE_CLOSED_LOOP_ERROR);
	}
	/*
	 * Configure the limit switch source to one of (Deactivated, FeedbackConnector (physical switch), RemoteTalon, or RemoteCANifier), with default "normally open" logic levels.
	 */
	public void configureHardLimitSwitch(Direction direction, LimitSwitchSource limitSwitchSource) {
		configureHardLimitSwitch(direction, limitSwitchSource, DEFAULT_LIMIT_SWITCH_MODE);
	}
	/*
	 * Configure default behavior for the switch input.  By default, "hard" limit switch uses hardware but it can also use network and CAN bus (confusingly).  
	 * 1.  enabled 
	 * 2.  set to use the hardware limit switch pins on the MAG Encoder or SRX breakout board.
	 * 3.  set to normally open
	 */
	public void configureHardLimitSwitch(Direction direction) {
		configureHardLimitSwitch(direction, DEFAULT_LIMIT_SWITCH_SOURCE, DEFAULT_LIMIT_SWITCH_MODE);
	}

	/*
	 * Configure PID values
	 */
	public void configurePID(TalonProfileSlot profileSlot, double p, double i, double d, double f) {
		srx.config_kF(profileSlot.getProfileSlotIndex(), f, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.config_kP(profileSlot.getProfileSlotIndex(), p, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.config_kI(profileSlot.getProfileSlotIndex(), i, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.config_kD(profileSlot.getProfileSlotIndex(), d, CONFIGURATION_COMMAND_TIMEOUT_MS);
		// I'm not sure if someone has to call selectProfileSlot to reload the values or if it's automatically looking at the right ones (guessing the latter)
	}
	/*
	 * Configure PID values
	 */
	public void configurePID(TalonProfileSlot profileSlot, double p, double i, double d) {
		configurePID(profileSlot, p, i, d, DEFAULT_D_GAIN);
	}

	/*
	 * Configure PID values, using defaults
	 */
	public void configurePID(TalonProfileSlot profileSlot) {
		configurePID(profileSlot, DEFAULT_F_GAIN, DEFAULT_P_GAIN, DEFAULT_I_GAIN, DEFAULT_D_GAIN);
	}

	public boolean readLimitSwitch(Direction direction) {
		if (direction.isNegative()) {
			return srx.getSensorCollection().isRevLimitSwitchClosed();
		} else {
			return srx.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}
	/**
	 * All changes to state are done here, and recorded here.
	 * Optionally reported to the log here.
	 */
	private void set(MotorControllerState newState, double arg) {
		/**
		 * Figure out state variables from current operation:
		 */
		MotorControllerState oldState = this.state;
		ControlMode newControlMode = ControlMode.Disabled;
		double newControlModeValue = arg;
		// New PID/Slot are almost always maintain
		TalonProfileSlot newSlotIndex  = lastSlotIndex;
		TalonPID         newPIDIndex   = lastPIDIndex;
		switch(newState) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newSlotIndex   = slotIndexForHoldPosition;
			newPIDIndex    = PIDIndexForHoldPosition;
			break;
		case HOLDING_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = slotIndexForHoldPosition;
			newPIDIndex    = PIDIndexForHoldPosition;
			break;
		case SET_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = slotIndexForMove;
			newPIDIndex    = PIDIndexForMove;
			break;
		case MOVING:
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = slotIndexForMove;
			newPIDIndex    = PIDIndexForMove;
			break;
		}
		//
		if(oldState != newState) {
			logger_info("set [transitioned from " + oldState + " to " + newState + "]");
		}
		// NB: This log statement shows old and current values, so you can see transitions completely
		logger_info(""
				+ "set Changes "
				+ "[State (" + oldState + "-> " + newState + ") "
				+ ", ControlMode (" + this.lastControlMode + "/" + this.lastControlModeValue + " -> " + newControlMode + "/" + newControlModeValue + ")"
				+ ", SlotIndex (" + lastSlotIndex + " -> " + newSlotIndex + ")"
				+ ", PIDIndex ("  + lastPIDIndex + " -> " + newPIDIndex + ")"
				+ "]");
		// NB: This log statement shows current values
		logger_info(""
				+ "set "
				+ "[State=" + newState
				+ ", ControlMode=" + newControlMode
				+ ", ControlModeValue=" + newControlModeValue
				+ ", SlotIndex=" + newSlotIndex
				+ ", PIDIndex=" + newPIDIndex
				+ "]");
		// Select the profile to use for the control loop (this is almost certainly going to be closed loop)
		srx.selectProfileSlot(newSlotIndex.getProfileSlotIndex(), newPIDIndex.getPIDIndex());
		srx.set(newControlMode, newControlModeValue);
		this.state = newState;
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		this.lastSlotIndex = newSlotIndex;
		this.lastPIDIndex = newPIDIndex;
	}
	
	public int readPosition() {
		int position = srx.getSelectedSensorPosition(lastPIDIndex.getPIDIndex());
		logger_info("readPosition " + " = @ " + position);
		return position;
	}

	/*
	 * Set the reference point for both relative and absolute encoders for a given PID
	 */
	
	private void correctEncoderSensorPositions(TalonPID talonPID, int sensorPosition) {
		if(talonPID.equals(TalonPID.Auxilliary) && !AUXILLIARY_PID_SUPPORTED) {
			logger.warning("WARNING: correctEncoderSensorPositions will not be run on auxilliary PID loop.  Firmware support for aux PID is not released yet.");
			return;
		}
		// Select relative & reset
		srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, talonPID.getPIDIndex(), CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.setSelectedSensorPosition(sensorPosition, talonPID.getPIDIndex(), CONFIGURATION_COMMAND_TIMEOUT_MS);
		// Leave us with relative sensor still.  TODO: remember and restore.
	}

	/*
	This is for a forced encoder assignment for debug/testing 
	*/
	public void setEncoderPosition(int encoderPosition) {
		logger_info("setEncoderPosition [new value =" + encoderPosition + "]");
		set(MotorControllerState.DISABLED, 0);
		// Zero out absolute encoder values for both PID slots
		correctEncoderSensorPositions(TalonPID.Primary, encoderPosition);
		correctEncoderSensorPositions(TalonPID.Auxilliary, encoderPosition);
		logger_info("setEncoderPosition holding at new value =" + encoderPosition + ".");
//		set(MotorControllerState.HOLDING_POSITION, readPosition());
	}

	// Returns true if we zeroed and are now holding position at zero
	public boolean zeroEncodersIfNecessary() {
		// TODO: account for inversions, account for which direction HAS the hard limit!
		if (readLimitSwitch(Direction.NEGATIVE)
				&& (state.isHoldingCurrentPosition() && lastControlModeValue <= readPosition()) /* moving backwards + limit == bad or holding at zero */
				|| (state.isMoving() && lastControlModeValue < 0)) /* moving backwards (negative velocity) + limit == bad */
		{
			logger_info("ZEROING ENCODERS");
			setEncoderPosition(0);
			return true;
		}
		return false;
	}
	// TODO: Rename TalonPIDINdex TalonPID
	// TOOD: Rename TalonProfileSlotIndex TalonProfile

	/**
	 * [ACTION] Set to an absolute encoder position
	 */
	public void setPosition(int position) {
		set(MotorControllerState.SET_POSITION, position);
	}

	/**
	 * [ACTION] Hold the current position, resist movement
	 */
	public void holdCurrentPosition() {
		set(MotorControllerState.HOLDING_POSITION, readPosition());
//		zeroEncodersIfNecessary();
	}

	/**
	 * [ACTION] Set to a speed and direction. Direction will determine whether speed is
	 * logical forward (positive) or reverse (negative) (not necessarily the same as
	 * the motor direction. Remember it can be inverted by configuration, to hide
	 * the difference)
	 */
	public void move(Direction direction, double speed) {
		if(speed == 0) {
			logger_info(" was told to move with speed zero.  Holding position instead.");
			holdCurrentPosition();
		} else {
			double speedParam = speed * direction.getMultiplierAsDouble();
			logger_info(String.format("move %s [Direction (%f) x Speed (%f) -> Velocity %f]", direction, direction.getMultiplierAsDouble(), speed, speedParam));
			set(MotorControllerState.MOVING, speedParam);
		}
	}

	/**
	 * Talon only uses the velocity
	 */
	public void setVelocity(double speed) {
		if(state == MotorControllerState.MOVING) {
			set(MotorControllerState.MOVING, speed);
		}
	}

	/**
	 * [ACTION] Stop output of the motor
	 */
	public void disable() {
		set(MotorControllerState.DISABLED, 0);
	}
	
	public void dumpState() {
		logger_info(""
				+ "dumpState[SETTINGS] = "
				+ "[State=" + state
				+ ", ControlMode=" + lastControlMode
				+ ", ControlModeValue=" + lastControlModeValue
				+ ", SlotIndex=" + lastSlotIndex
				+ ", PIDIndex=" + lastPIDIndex
				+ "]");
		logger_info(""
				+ "dumpState[HW] = "
				+ "[LimitF=" + readLimitSwitch(Direction.POSITIVE)
				+ ", LimitR=" + readLimitSwitch(Direction.NEGATIVE)
				+ ", PWMPos=" + srx.getSensorCollection().getPulseWidthPosition()
				+ ", QuadPos=" + srx.getSensorCollection().getQuadraturePosition()
				+ "]");
	}
}
