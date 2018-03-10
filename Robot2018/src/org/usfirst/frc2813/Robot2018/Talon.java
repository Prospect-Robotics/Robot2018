package org.usfirst.frc2813.Robot2018;

import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc2813.Robot2018.Direction;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * A wrapper class to handle an SRX Talon motor controller
 * Arm rotation has default halted state. Move command takes
 * TODO: Create a base class above this for motor controllers.
 */
public class Talon {
	private final TalonSRX srx;
	private final Log log;

	public static final double PULSES_PER_REVOLUTION = 4096;
	// Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops.
	public static final int MAINTAIN_PID_LOOP_IDX = 0;
	public static final int MOVE_PIDLOOP_IDX = 1;
	public static final int MAINTAIN_SLOT_IDX = 0;
	public static final int MOVE_SLOT_IDX = 1;
	// During initialization we use 10ms timeout to setup commands
	private static final int CONFIGURATION_COMMAND_TIMEOUT_MS = 10; // timeout for Talon config function
	// During operation, we use 0ms timeout to avoid stalling on transient bus
	// errors, per CTR examples/documentation.
	private static final int OPERATION_COMMAND_TIMEOUT_MS = 0; // timeout for Talon config function
	// choose to ensure sensor is positive when output is positive
	public static boolean DEFAULT_SENSOR_PHASE = true;
	// choose based on what direction you want to be positive, this does not affect
	// motor invert.
	public static boolean DEFAULT_MOTOR_INVERSION = false;

	// Just a description
	private final String label;
	
	private int currentPidIndex = MAINTAIN_PID_LOOP_IDX;    // Remember last used pid index, help us implement state transitions 
	
	// Current state 
	private TalonState state;
	// Last control mode send to controller
	private ControlMode lastControlMode = ControlMode.Position; // Remember last assigned control mode, help us implement state transitions
	// Last arg sent for controlMode
	private double lastControlModeValue = 0;
	private int lastSlotIndex = MAINTAIN_SLOT_IDX;
	private int lastPIDIndex = MAINTAIN_PID_LOOP_IDX;

	public Talon(TalonSRX srx, String label, Log log) {
		this.label = label;
		this.srx = srx;
		this.log = log;

		// set the peak and nominal outputs, 12V means full
		srx.configNominalOutputForward(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configNominalOutputReverse(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputForward(1, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputReverse(-1, CONFIGURATION_COMMAND_TIMEOUT_MS);

		srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOP_IDX,
				CONFIGURATION_COMMAND_TIMEOUT_MS);

		/*
		 * This section initializes the relative encoder to the absolute value from the
		 * absolute encoder position, so that any previous calibration of zero will be
		 * preserved.
		 */
		int absolutePosition = srx.getSensorCollection().getPulseWidthPosition();
		/*
		 * NB: We have not yet set phase or inversion, so we will factor that into the
		 * absolute position we calculate
		 */
		if (!DEFAULT_SENSOR_PHASE)
			absolutePosition *= -1;
		if (DEFAULT_MOTOR_INVERSION)
			absolutePosition *= -1;

		srx.setSensorPhase(DEFAULT_SENSOR_PHASE);
		srx.setInverted(DEFAULT_MOTOR_INVERSION);
		srx.setSelectedSensorPosition(absolutePosition, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.setSelectedSensorPosition(absolutePosition, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		/*
		 * set the allowable closed-loop error, Closed-Loop output will be neutral
		 * within this range. See Table in Section 17.2.1 for native units per rotation.
		 */
		srx.configAllowableClosedloopError(MAINTAIN_SLOT_IDX, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configAllowableClosedloopError(MOVE_SLOT_IDX, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		
		// Start disabled
		set(TalonState.DISABLED, 0);
	}
	
	public IMotorState getState() {
		return this.state;
	}

	public void configSoftLimitSwitch(Direction direction, int limit) {
		if (direction.isNegative()) {
			srx.configReverseSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configReverseSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		} else {
			srx.configForwardSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configForwardSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void configHardLimitSwitch(Direction direction) {
		if (direction.isNegative()) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOP_IDX,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MOVE_PIDLOOP_IDX,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		} else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOP_IDX,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MOVE_PIDLOOP_IDX,
					CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void setPID(int slot, double p, double i, double d) {
		srx.config_kF(slot, 0, CONFIGURATION_COMMAND_TIMEOUT_MS); // typically kF stays zero.
		srx.config_kP(slot, p, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.config_kI(slot, i, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.config_kD(slot, d, CONFIGURATION_COMMAND_TIMEOUT_MS);
	}

	public boolean readLimitSwitch(Direction direction) {
		if (direction.isNegative()) {
			return srx.getSensorCollection().isRevLimitSwitchClosed();
		} else {
			return srx.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}

	/**
	 * Get a label for a control mode
	 */
	public static String getControlModeLabel(ControlMode mode) {
		switch (mode) {
		case Current:
			return "Current";
		case Disabled:
			return "Disabled";
		case Follower:
			return "Follower";
		case MotionMagic:
			return "MotionMagic";
		case MotionMagicArc:
			return "MotionMagicArc";
		case MotionProfile:
			return "MotionProfile";
		case MotionProfileArc:
			return "MotionProfileArc";
		case PercentOutput:
			return "PercentOutput";
		case Position:
			return "Postion";
		case Velocity:
			return "Velocity";
		default:
			return "" + mode;
		}
	}

	/*
	 * All changes to state are done here, and recorded here.
	 * Optionally reported to the log here.
	 */
	
	private void set(TalonState newState, double arg) {
		/*
		 * Figure out state variables from current operation:
		 */
		ControlMode newControlMode = ControlMode.Disabled;
		double newControlModeValue = arg;
		int newSlotIndex  = lastSlotIndex;
		int newPIDIndex   = lastPIDIndex;
		if(newState == TalonState.DISABLED) {
			newControlModeValue = 0; // NB: Not used here
			newControlMode = ControlMode.Disabled;
			newSlotIndex   = MAINTAIN_SLOT_IDX;
			newPIDIndex    = MAINTAIN_PID_LOOP_IDX;
		} else if(newState == TalonState.HOLDING_POSITION) {
			newControlMode = ControlMode.Position;
			newSlotIndex   = MAINTAIN_SLOT_IDX;
			newPIDIndex    = MAINTAIN_PID_LOOP_IDX;
		} else if(newState == TalonState.SET_POSITION) {
			newControlMode = ControlMode.Position;
			// NB: SET_POSITION means "move to position", so use MOVE behavior
			newSlotIndex   = MOVE_SLOT_IDX;
			newPIDIndex    = MOVE_PIDLOOP_IDX;
		} else if(newState == TalonState.MOVING) {
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = MOVE_SLOT_IDX;
			newPIDIndex    = MOVE_PIDLOOP_IDX;
		} else {
			throw new IllegalArgumentException("Unsupported state: " + newState);
		}
		// NB: This log statement shows old and current values, so you can see transitions completely
		// TODO: Add a debug flag for this
		log.print(""
				+ "set Changes "
				+ "[State (" + this.state + "-> " + newState + ") "
				+ ", ControlMode (" + getControlModeLabel(this.lastControlMode) + "/" + this.lastControlModeValue + " -> " + getControlModeLabel(newControlMode) + "/" + newControlModeValue + ")" 
				+ ", SlotIndex (" + lastSlotIndex + " -> " + newSlotIndex + ")"
				+ ", PIDIndex ("  + lastPIDIndex + " -> " + newPIDIndex + ")"
				+ "]");
		// TODO: Add a debug flag for this
		// NB: This log statement shows current values
		log.print(""
				+ "set "
				+ "[State=" + newState
				+ ", ControlMode=" + getControlModeLabel(newControlMode)
				+ ", ControlModeValue=" + newControlModeValue
				+ ", SlotIndex=" + newSlotIndex 
				+ ", PIDIndex=" + newPIDIndex
				+ "]");
		srx.set(ControlMode.Disabled, 0); // NB: Not sure this is necessary...
		srx.selectProfileSlot(this.lastSlotIndex, this.lastPIDIndex);
		srx.set(newControlMode, arg);
		this.state = newState;
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		this.lastSlotIndex = newSlotIndex;
		this.lastPIDIndex = newPIDIndex;
	}

	public int readPosition() {
		int position = srx.getSelectedSensorPosition(currentPidIndex);
		log.print("readPosition " + " = @ " + position);
		return position;
	}

	// Returns true if we zeroed and are now holding position at zero
	public boolean zeroEncodersIfNecessary() {
		// TODO: account for inversions
		if (readLimitSwitch(Direction.NEGATIVE) 
				&& (state == TalonState.HOLDING_POSITION && lastControlModeValue <= readPosition()) /* moving backwards + limit == bad or holding at zero */
				|| (state == TalonState.MOVING && lastControlModeValue < 0)) /* moving backwards (negative velocity) + limit == bad */
		{
			log.print("ZEROING ENCODERS");
			TalonState oldState = state;
			set(TalonState.DISABLED, 0);
			// Zero out absolute encoder values for both PID slots
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			// Zero out relative encoder values for both PID slots
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, MAINTAIN_PID_LOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, MOVE_PIDLOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			set(TalonState.HOLDING_POSITION, readPosition());
			log.print("ENCODERS ZEROD");
			return true;
		}
		return false;
	}
	/*
	 * [ACTION] Set to an absolute encoder position
	 */
	public void setPosition(int position) {
		set(TalonState.SET_POSITION, position);
	}
	
	/*
	 * [ACTION] Hold the current position, resist movement
	 */
	public void holdCurrentPosition() {
		set(TalonState.HOLDING_POSITION, readPosition());
		zeroEncodersIfNecessary();
	}

	/*
	 * [ACTION] Set to a speed and direction. Direction will determine whether speed is
	 * logical forward (positive) or reverse (negative) (not necessarily the same as
	 * the motor direction. Remember it can be inverted by configuration, to hide
	 * the difference)
	 */
	public void move(Direction direction, double speed) {
		if(speed == 0) {
			System.out.println(label + " was told to move with speed zero.  Holding position instead.");
			holdCurrentPosition();
		} else {
			double speedParam = speed * direction.getMultiplierAsDouble();
			log.print("move [Direction (" + direction.getLabel() + ") x Speed (" + speed + ") -> " + speedParam + "]");
			set(TalonState.MOVING, speedParam);
		}
	}

	/*
	 * [ACTION] Stop output of the motor
	 */
	public void disable() {
		set(TalonState.DISABLED, 0);
	}
	public boolean isDisabled() {
		return state == TalonState.DISABLED;
	}
	public boolean isHoldingCurrentPosition() {
		return state == TalonState.HOLDING_POSITION;
	}
	public boolean isMovingToPosition() {
		return state == TalonState.SET_POSITION;
	}
	public boolean isMoving() {
		return state == TalonState.MOVING;
	}
}
