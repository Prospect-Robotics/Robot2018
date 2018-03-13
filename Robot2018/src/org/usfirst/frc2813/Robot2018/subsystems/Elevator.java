package org.usfirst.frc2813.Robot2018.subsystems;


import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorHoldPosition;
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
		PULSES_PER_UNIT_POSITION = Talon.PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
	    DEFAULT_SPEED = 12;

		motorController = new Talon(RobotMap.srxElevator, logger);
		// Configure the limits for DOWN
		motorController.configureHardLimitSwitch(Direction.DOWN, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		motorController.disableSoftLimitSwitch(Direction.DOWN);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, true);
		// Configure the limits for UP
		motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, false);
		motorController.configureSoftLimitSwitch(Direction.UP, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION));
		// Configure the PID profiles
  	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.8, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 0.75, 0.01, 40);
	    initialize();
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

	protected void setControllerSpeedAndDirection(int speedParam) {
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
}
