package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonMotorInversion;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.TalonSensorPhase;
import org.usfirst.frc2813.util.unit.Direction;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Elevator subsystem. Moves up and down.
 *
 * Elevator can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in inches.
 * speed is in inches per second.
 */
public class Elevator extends SubsystemPositionDirectionSpeed {
	private final double INCHES_PER_REVOLUTION = Math.PI * 1.25;

	private Talon motorController;

	public Elevator() {
		MAX_POSITION = 24; //FIXME!!! This is from arm
		MIN_POSITION = 0;
		PULSES_PER_UNIT_POSITION = Talon.TALON_SRX_OUTPUTR_PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
	    DEFAULT_SPEED = 12;
		motorController = new Talon(RobotMap.srxElevator, Logger.getLogger("ElevatorMC"));
		// Configure the limits for DOWN
		motorController.configureHardLimitSwitch(Direction.DOWN, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		motorController.disableSoftLimitSwitch(Direction.DOWN);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, true);

// HW BUG WORKAROUND
motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.UP, true);
//		// Configure the limits for UP
//		motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
//		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, false);
// HW BUG WORKAROUND
		motorController.configureSoftLimitSwitch(Direction.UP, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION));
	
		// Configure the PID profiles
  	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.8, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 0.75, 0.01, 40.0);
	    initialize();
	}
	
	public TalonSensorPhase getSensorPhase() {
		return TalonSensorPhase.Normal;
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

	protected void setControllerDirectionAndSpeed(int speedParam) {
		motorController.move(direction, speedParam);
	}

	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new ElevatorHoldPosition());
	}

	@Override
	public void periodic() {}
	
	/// Dump debug output
	public void dumpState() {
		super.dumpState();
		motorController.dumpState();
	}
	
	public void encoderRelativePositionTestingMode() {
		disable();
		motorController.setEncoderPosition(0);
		disable();
		dumpState();
	}
}
