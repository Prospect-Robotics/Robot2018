package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Arm.ArmHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Arm subsystem. Rotates up and down
 *
 * Arm can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in degrees.
 * speed is in inches per second.
 */
public class Arm extends SubsystemPositionDirectionSpeed {
	private static final double GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
    private static final double PULSES_PER_REVOLUTION = GEAR_RATIO * Talon.TALON_SRX_OUTPUTR_PULSES_PER_REVOLUTION;

    private static final double PULSES_PER_DEGREE = PULSES_PER_REVOLUTION / 360;
    private static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.


	private Talon motorController;

	public Arm() {
		MAX_POSITION = 24;
		MIN_POSITION = 0;
		PULSES_PER_UNIT_POSITION = PULSES_PER_REVOLUTION / 360;
		PULSES_PER_UNIT_POSITION_PER_TIME = PULSES_PER_DEGREE * VELOCITY_TIME_UNITS_PER_SEC;
	    DEFAULT_SPEED = 5;

		motorController = new Talon(RobotMap.srxArm, Logger.getLogger("ArmMC"));
		
		// Configure the limits for DOWN
		motorController.configureHardLimitSwitch(Direction.BACKWARD, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		motorController.disableSoftLimitSwitch(Direction.BACKWARD);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.BACKWARD, true);
		// Configure the limits for UP
		motorController.configureHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		motorController.configureSoftLimitSwitch(Direction.FORWARD, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION));
		// Configure the PID profiles
		motorController.setSlotIndexForHoldPosition(TalonProfileSlot.HoldingPosition);
		motorController.setSlotIndexForMove(TalonProfileSlot.Moving);
	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.1, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 2.0, 0.0, 0.0);
	}

	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	protected int readControllerPosition() {
		return motorController.readPosition();
	}

	protected void setControllerPosition(int positionParam) {
		motorController.setPosition(positionParam);
	}

	protected MotorControllerState readMotorControllerState() {
		return motorController.getState();
	}

	protected void setControllerDirectionAndSpeed(Direction direction, double speedParam) {
		motorController.move(direction, speedParam);
	}

	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes arm in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new ArmHoldPosition());
	}

	@Override
	public void periodic() {}
}
