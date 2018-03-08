package org.usfirst.frc2813.Robot2018;

import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc2813.Robot2018.Direction;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * A wrapper class to handle an SRX Talon motor controller
 * Arm rotation has default halted state. Move command takes
 */
public class Talon {
	private TalonSRX srx;

	public static final int PULSES_PER_REVOLUTION = 4096;
	public static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.
	public static final int PRIMARY_CLOSED_LOOP_SENSOR = 0;
	public static final int CASCADED_CLOSED_LOOP_SENSOR = 1;

	private static final int TIMEOUT_TO_CONFIGURE_MS = 10;  // timeout for Talon config function

	/*
	 * Which PID slot to pull gains from. Starting 2018, you can choose from
	 * 0,1,2 or 3. Only the first two (0,1) are visible in web-based
	 * configuration.
	 */
	private static final int kSlotIdx = 0;

	/*
	 * Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops. For
	 * now we just want the primary one.
	 */
	private static final int MAINTAIN_PID_LOOOP_IDX = 0;
	private static final int movePIDLoopIdx = 1;

	/*
	 * set to zero to skip waiting for confirmation, set to nonzero to wait and
	 * report to DS if action fails.
	 */
	private static final int K_TIMEOUT_MS = 10;

	/* choose to ensure sensor is positive when output is positive */
	public static boolean K_SENSOR_PHASE = true;

	/* choose based on what direction you want to be positive,
		this does not affect motor invert. */
	public static boolean K_MOTOR_INVERT = false;
	// These directions map to the Talon reverse limit switch
    static public Set<Direction> reverseDirections = new HashSet<>();
    static {
        reverseDirections.add(Direction.BACKWARD);
        reverseDirections.add(Direction.LEFT);
        reverseDirections.add(Direction.IN);
        reverseDirections.add(Direction.DOWN);
    }

	public Talon(TalonSRX srx) {
        this.srx = srx;
    }

	public void configSoftLimitSwitch(Direction direction, int limit) {
        if (reverseDirections.contains(direction)) {
            srx.configReverseSoftLimitEnable(true, 10);
            srx.configReverseSoftLimitThreshold(limit, TIMEOUT_TO_CONFIGURE_MS);
        }
        else {
            srx.configForwardSoftLimitEnable(true, 10);
            srx.configForwardSoftLimitThreshold(limit, TIMEOUT_TO_CONFIGURE_MS);
        }
	}

	public void configHardLimitSwitch(Direction direction) {
        if (reverseDirections.contains(direction)) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TIMEOUT_TO_CONFIGURE_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
        }
        else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TIMEOUT_TO_CONFIGURE_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
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
		srx.setSelectedSensorPosition(absolutePosition, MAINTAIN_PID_LOOOP_IDX, K_TIMEOUT_MS);
    }

	public double readPosition() {
		return srx.getSelectedSensorPosition(PRIMARY_CLOSED_LOOP_SENSOR);
	}

	public void goToPosition(int position) {
		srx.set(ControlMode.Position, position);
	}

	public void setSpeedAndDirection(double speed, Direction direction) {
		srx.selectProfileSlot(1, 1);
		double speedParam = speed;
		if (reverseDirections.contains(direction)) {
			speedParam *= -1;
		}
		srx.set(ControlMode.Velocity, speedParam);
	}

	// stop arm from moving - this is active as we require pid to resist gravity
	public void halt() {
		srx.selectProfileSlot(0, 0);
		srx.set(ControlMode.Position, readPosition());
	}
}
