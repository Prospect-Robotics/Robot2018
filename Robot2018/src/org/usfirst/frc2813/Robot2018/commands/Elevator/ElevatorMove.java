package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Move elevator in direction until interrupted.
 */
public class ElevatorMove extends GearheadsCommand {
	private final Direction direction;

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public ElevatorMove(Direction direction) {
		this.direction = direction;
		logger.info("MoveElevator-" + direction);
		requires(Robot.elevator);
	}

	// Called once to begin the command
	// @Override
	protected void initialize() {
		logger.finer("in initialize");
			Robot.elevator.setDirection(direction);
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
