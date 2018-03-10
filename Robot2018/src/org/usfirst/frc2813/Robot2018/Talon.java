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
	/*
	 * TODO: We have two conflicting ideas here - "primary closed loop" and "cascaded closed loop" pid indexes vs "maintain" and "move".
	 * "maintain" and "move" can both be either primary or cascaded.
	 */
	public static final int PRIMARY_CLOSED_LOOP_PID_INDEX = 0;
	public static final int CASCADED_CLOSED_LOOP_PID_INDEX = 1;
	// Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops.
	public static final int MAINTAIN_PID_LOOOP_IDX = 0;
	public static final int MOVE_PIDLOOP_IDX = 1;
	// During initialization we use 10ms timeout to setup commands
	private static final int CONFIGURATION_COMMAND_TIMEOUT_MS = 10;  // timeout for Talon config function
	// During operation, we use 0ms timeout to avoid stalling on transient bus errors, per CTR examples/documentation.
	private static final int OPERATION_COMMAND_TIMEOUT_MS = 0;  // timeout for Talon config function
	// choose to ensure sensor is positive when output is positive
	public static boolean DEFAULT_SENSOR_PHASE = true;
	// choose based on what direction you want to be positive, this does not affect motor invert.
	public static boolean DEFAULT_MOTOR_INVERSION = false;

	// Remember mode and position.  We only want to zero encoders when we are stopped
	private ControlMode currentMode = ControlMode.Position;
	private Direction currentDirection;
	private Double targetPosition = null;
	private final String label;
	
	public Talon(TalonSRX srx, String label, Log log) {
		this.label = label;
		this.srx = srx;
		this.log = log;
		
		// set the peak and nominal outputs, 12V means full
		srx.configNominalOutputForward(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configNominalOutputReverse(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputForward(1, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputReverse(-1, CONFIGURATION_COMMAND_TIMEOUT_MS);

		srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);

		/*
		 * This section initializes the relative encoder to the absolute value from the absolute encoder position, so that any 
		 * previous calibration of zero will be preserved.
		 */
		int absolutePosition = srx.getSensorCollection().getPulseWidthPosition();
		/* NB: We have not yet set phase or inversion, so we will factor that into the absolute position we calculate */
		if (!DEFAULT_SENSOR_PHASE)
			absolutePosition *= -1;
		if (DEFAULT_MOTOR_INVERSION)
			absolutePosition *= -1;
		
		srx.setSensorPhase(DEFAULT_SENSOR_PHASE);
		srx.setInverted(DEFAULT_MOTOR_INVERSION);
		srx.setSelectedSensorPosition(absolutePosition, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);

		/*
		 * set the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table in Section 17.2.1 for native
		 * units per rotation.
		 */
		srx.configAllowableClosedloopError(0, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
	}
	
	public void configSoftLimitSwitch(Direction direction, int limit) {
		if (direction.isNegative()) {
			srx.configReverseSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configReverseSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
		else {
			srx.configForwardSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configForwardSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void configHardLimitSwitch(Direction direction) {
		if (direction.isNegative()) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, CASCADED_CLOSED_LOOP_PID_INDEX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
		else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, CASCADED_CLOSED_LOOP_PID_INDEX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void initPID() {
		int absolutePosition = srx.getSensorCollection().getPulseWidthPosition();
		if (DEFAULT_SENSOR_PHASE) {
			absolutePosition *= -1;
		}
		if (DEFAULT_MOTOR_INVERSION) {
			absolutePosition *= -1;
		}
		srx.setSelectedSensorPosition(absolutePosition, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
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
		}
		else {
			return srx.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}

	private void set(ControlMode mode, double arg) {
		/*
		 * Figure out state variables from current operation:
		 * 
		 * currentDirection - What canonical direction are we going?  positive, negative or neutral.
		 * targetPosition   - The set position, or null if we are not in a position holding mode.
		 * currentMode      - The controller mode 
		 */
		this.currentMode = mode;
		switch(mode) {
		case Disabled:
			this.currentDirection = Direction.NEUTRAL;
			this.targetPosition   = null;
			break;
		case Current:
		case Velocity:
		case PercentOutput:
			this.targetPosition = null;
			if(arg == 0) {
				this.currentDirection = Direction.NEUTRAL;
			} else if(arg > 0) {
				this.currentDirection = Direction.POSITIVE;
			} else {
				this.currentDirection = Direction.NEGATIVE;
			}
			break;
		case Position:
			this.targetPosition = Double.valueOf(arg);
			int currentPosition = readPosition();
			if(arg == currentPosition) {
				this.currentDirection = Direction.NEUTRAL;
			} else if(arg > currentPosition) {
				this.currentDirection = Direction.POSITIVE;
			} else {
				this.currentDirection = Direction.NEGATIVE;
			}
			break;
		default:
			throw new IllegalArgumentException("Unsupported controlMode: " + mode);
		}
		log.print("SET " + " [mode=" + mode + ", arg=" + arg + "]");
		srx.set(mode, arg);
	}

	public int readPosition() {
		int position = srx.getSelectedSensorPosition(PRIMARY_CLOSED_LOOP_PID_INDEX);
		log.print("GET " + " = @ " + position);
		return position;
	}

	/*
	 * Set to an absolute encoder position
	 */
	public void setPosition(int position) {
		set(ControlMode.Position, position);
	}

	public void zeroEncoders() {
		// 	TODO: account for inversions
		if(readLimitSwitch(Direction.BACKWARD /* any value that is 'reverse' will do*/)
			&& (currentMode == ControlMode.Position && targetPosition <= readPosition()) /* moving backwards + limit == bad or holding at zero */
			|| (currentMode == ControlMode.Velocity && targetPosition < 0)) /* moving backwards (negative velocity) + limit == bad */ 
		{
			log.print("ZEROING ENCODERS");
			set(ControlMode.Velocity, 0);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, PRIMARY_CLOSED_LOOP_PID_INDEX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, PRIMARY_CLOSED_LOOP_PID_INDEX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			set(ControlMode.Position, 0);
			log.print("ENCODERS ZERO'D");
		}
	}

	/*
	 * Set to a speed and direction.  Direction will determine whether speed is logical forward (positive) or reverse (negative)
	 * (not necessarily the same as the motor direction.  Remember it can be inverted by configuration, to hide the difference) 
	 */
	public void setSpeedAndDirection(double speed, Direction direction) {
		srx.selectProfileSlot(1, 1);
		double speedParam = speed * direction.getMultiplierAsDouble();
		log.print("setSpeedAndDirection [Speed=" + speed + " Direction=" + direction.getLabel() + " Velocity=" + speedParam + "]");
		set(ControlMode.Velocity, speedParam);
	}

	// stop arm from moving - this is active as we require pid to resist gravity
	public void halt() {
		srx.selectProfileSlot(0, 0);
		setPosition(readPosition());
		log.print("HALT");
	}
}
