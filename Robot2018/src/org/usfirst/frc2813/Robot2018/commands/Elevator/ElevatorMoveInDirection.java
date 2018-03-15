package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.units.Direction;

/**
 * Move elevator in given direction until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ElevatorMoveInDirection extends GearheadsCommand {
	private final Direction direction;

	public ElevatorMoveInDirection(Direction direction) {
		this.direction = direction;
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		Robot.elevator.moveInDirection(direction);
	}

	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		Robot.elevator.holdCurrentPosition();
	}
}
