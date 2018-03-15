package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.units.Direction;

/**
 * Move arm in given direction until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ArmMoveInDirection extends GearheadsCommand {
	private final Direction direction;

	public ArmMoveInDirection(Direction direction) {
		this.direction = direction;
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");
			Robot.arm.moveInDirection(direction);
	}

	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		Logger.debug("in interrupted");
		Robot.arm.holdCurrentPosition();
	}
}
