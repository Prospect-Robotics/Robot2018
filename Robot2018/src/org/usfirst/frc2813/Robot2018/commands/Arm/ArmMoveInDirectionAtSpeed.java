package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move arm in given direction at given speed until interrupted.
 * Hold current position with PID when interrupted.
 * Rate can be in any units you like.
 */
public class ArmMoveInDirectionAtSpeed extends GearheadsCommand {
	private final Direction direction;
	private final Rate speed;

	public ArmMoveInDirectionAtSpeed(Direction direction, Rate speed) {
		this.direction = direction;
		this.speed = speed;
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.formatInfo("Move %s at speed %s", direction, speed);
		Logger.debug("in initialize");
			Robot.arm.moveInDirectionAtSpeed(direction, speed);
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
