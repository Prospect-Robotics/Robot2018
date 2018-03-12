package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Move elevator in direction until interrupted.
 */
public class ElevatorMove extends GearheadsCommand {
	private final Direction direction;
	private final double speed;

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public ElevatorMove(Direction direction) {
		this.direction = direction;
		speed = Robot.elevator.DEFAULT_SPEED;
		logger.info(String.format("MoveElevator - %s at default speed %s", direction, speed));
		requires(Robot.elevator);
	}

	public ElevatorMove(Direction direction, double speed) {
		this.direction = direction;
		this.speed = speed;
		logger.info(String.format("MoveElevator - %s at speed %s", direction, speed));
		requires(Robot.elevator);
	}

	// Called once to begin the command
	// @Override
	protected void initialize() {
		logger.finer("in initialize");
			Robot.elevator.moveAtSpeedAndDirection(speed, direction);
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted
	}

	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		Robot.elevator.holdCurrentPosition();
	}
}
