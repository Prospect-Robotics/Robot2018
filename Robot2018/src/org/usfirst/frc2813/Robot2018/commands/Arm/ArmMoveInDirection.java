package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Move arm in given direction until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ArmMoveInDirection extends GearheadsCommand {
	private final Direction direction;

	public ArmMoveInDirection(Direction direction) {
		this.direction = direction;
		logger.info("Move in " + direction);
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
			Robot.arm.moveInDirection(direction);
	}

	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		Robot.arm.holdCurrentPosition();
	}
}
