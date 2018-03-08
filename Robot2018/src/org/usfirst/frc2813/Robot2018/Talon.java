package org.usfirst.frc2813.Robot2018;

import java.util.HashSet;
import java.util.Set;

import org.usfirst.frc2813.Robot2018.Constants;
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

	public static final int SRX_MAG_PULSES_PER_REVOLUTION = 4096; // pulses per revolution on a Talon SRX branded magnetic encoder, commonly known as an "SRX mag".
	public static final double SRX_VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.  One second equals 1000 millseconds.

	private static final int TALON_TIMEOUT_TO_CONFIGURE_MS = 10;  // timeout for config function

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
            srx.configReverseSoftLimitThreshold(limit, TALON_TIMEOUT_TO_CONFIGURE_MS);
        }
        else {
            srx.configForwardSoftLimitEnable(true, 10);
            srx.configForwardSoftLimitThreshold(limit, TALON_TIMEOUT_TO_CONFIGURE_MS);
        }
	}

	public void configHardLimitSwitch(Direction direction) {
        if (reverseDirections.contains(direction)) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_TO_CONFIGURE_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
        }
        else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_TO_CONFIGURE_MS);
			srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
        }
	}

	public double readPosition() {
		return srx.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);
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
