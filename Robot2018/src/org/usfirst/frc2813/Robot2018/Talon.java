package org.usfirst.frc2813.Robot2018;

import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc2813.Robot2018.Direction;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Counter.Mode;

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
	private TalonSRX srx;

	public static final double PULSES_PER_REVOLUTION = 4096;

	public static final int PRIMARY_CLOSED_LOOP_SENSOR = 0;
	public static final int CASCADED_CLOSED_LOOP_SENSOR = 1;

	// During initialization we use 10ms timeout to setup commands
	private static final int CONFIGURATION_COMMAND_TIMEOUT_MS = 10;  // timeout for Talon config function
	// During operation, we use 0ms timeout to avoid stalling on transient bus errors, per CTR examples/documentation.
	private static final int OPERATION_COMMAND_TIMEOUT_MS = 0;  // timeout for Talon config function

	/**
	 * Which PID slot to pull gains from. Starting 2018, you can choose from
	 * 0,1,2 or 3. Only the first two (0,1) are visible in web-based
	 * configuration.
	 */
	private static final int SLOT_IDX = 0;

	// Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops.
	public static final int MAINTAIN_PID_LOOOP_IDX = 0;
	public static final int MOVE_PIDLOOP_IDX = 1;

	// choose to ensure sensor is positive when output is positive
	public static boolean K_SENSOR_PHASE = true;

	// choose based on what direction you want to be positive, this does not affect motor invert.
	public static boolean K_MOTOR_INVERT = false;

	// These directions map to the Talon reverse limit switch
	static public Set<Direction> reverseDirections = new HashSet<>();
	static {
		reverseDirections.add(Direction.BACKWARD);
		reverseDirections.add(Direction.LEFT);
		reverseDirections.add(Direction.IN);
		reverseDirections.add(Direction.DOWN);
	}

	private String label;
	
	// Remember mode and position.  We only want to zero encoders when we are stopped
	private ControlMode currentMode = ControlMode.Position;
	private double targetPosition = 0;
	
	public Talon(TalonSRX srx, String label) {
		this.srx = srx;
		this.label = label;
		
		// set the peak and nominal outputs, 12V means full
		srx.configNominalOutputForward(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configNominalOutputReverse(0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputForward(1, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.configPeakOutputReverse(-1, CONFIGURATION_COMMAND_TIMEOUT_MS);

		srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
		srx.setSensorPhase(K_SENSOR_PHASE);
		srx.setInverted(K_MOTOR_INVERT);

		/*
		 * This section initializes the relative encoder to the absolute value from the absolute encoder position, so that any 
		 * previous calibration of zero will be preserved.
		 */
		int absolutePosition = srx.getSensorCollection().getPulseWidthPosition();
		if (!K_SENSOR_PHASE)
			absolutePosition *= -1;
		if (K_MOTOR_INVERT)
			absolutePosition *= -1;
		srx.setSelectedSensorPosition(absolutePosition, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);

		/*
		 * set the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table in Section 17.2.1 for native
		 * units per rotation.
		 */
		srx.configAllowableClosedloopError(0, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
	}
	
	private void log(String s) {
		System.out.println(label + ": " + s);
	}

	public void configSoftLimitSwitch(Direction direction, int limit) {
		if (reverseDirections.contains(direction)) {
			srx.configReverseSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configReverseSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
		else {
			srx.configForwardSoftLimitEnable(true, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configForwardSoftLimitThreshold(limit, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void configHardLimitSwitch(Direction direction) {
		if (reverseDirections.contains(direction)) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, CASCADED_CLOSED_LOOP_SENSOR, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
		else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, CASCADED_CLOSED_LOOP_SENSOR, CONFIGURATION_COMMAND_TIMEOUT_MS);
		}
	}

	public void initPID() {
		int absolutePosition = srx.getSensorCollection().getPulseWidthPosition();
		if (K_SENSOR_PHASE) {
			absolutePosition *= -1;
		}
		if (K_MOTOR_INVERT) {
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
		if (reverseDirections.contains(direction)) {
			return srx.getSensorCollection().isRevLimitSwitchClosed();
		}
		else {
			return srx.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}

	private void set(ControlMode mode, double position) {
		this.currentMode = mode;
		this.targetPosition = position;
		log("SET " + " [mode=" + mode + ", position=" + position + "]");
		srx.set(mode, position);
	}

	public int readPosition() {
		int position = srx.getSelectedSensorPosition(PRIMARY_CLOSED_LOOP_SENSOR);
		log("GET " + " = @ " + position);
		return position;
	}

	public void setPosition(int position) {
		set(ControlMode.Position, position);
	}

	public void zeroEncoders() {
		// 	TODO: account for inversions
		if(readLimitSwitch(Direction.BACKWARD /* any value that is 'reverse' will do*/)
			&& (currentMode == ControlMode.Position && targetPosition <= readPosition()) /* moving backwards + limit == bad or holding at zero */
			|| (currentMode == ControlMode.Velocity && targetPosition < 0)) /* moving backwards (negative velocity) + limit == bad */ 
		{
			set(ControlMode.Velocity, 0);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, PRIMARY_CLOSED_LOOP_SENSOR, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, MAINTAIN_PID_LOOOP_IDX, CONFIGURATION_COMMAND_TIMEOUT_MS);
			srx.setSelectedSensorPosition(0, PRIMARY_CLOSED_LOOP_SENSOR, CONFIGURATION_COMMAND_TIMEOUT_MS);
			set(ControlMode.Position, 0);
			log("ZEROING ENCODERS");
		}
	}

	public void setSpeedAndDirection(double speed, Direction direction) {
		srx.selectProfileSlot(1, 1);
		double speedParam = speed;
		if (reverseDirections.contains(direction)) {
			speedParam *= -1;
		}
		set(ControlMode.Velocity, speedParam);
		log("SET velocity = " + speedParam);
	}

	// stop arm from moving - this is active as we require pid to resist gravity
	public void halt() {
		srx.selectProfileSlot(0, 0);
		setPosition(readPosition());
		log("HALT");
	}
}
