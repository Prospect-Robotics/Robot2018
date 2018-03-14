package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

import logging.Logger;

/**
 * Move arm in given direction at given speed until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ArmMoveInDirectionAtSpeed extends GearheadsCommand {
	private final Direction direction;
	private final double speed;

	public ArmMoveInDirectionAtSpeed(Direction direction, double speed) {
		this.direction = direction;
		this.speed = speed;
		Logger.info(String.format("Move %s at speed %s", direction, speed));
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");//was finer
			Robot.arm.moveAtSpeedAndDirection(speed, direction);
	}

	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		Logger.debug("in interrupted");//was finer
		Robot.arm.holdCurrentPosition();
	}
}
