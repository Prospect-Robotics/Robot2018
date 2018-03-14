package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

import logging.Logger;

/**
 * Move elevator in given direction at given speed until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ElevatorMoveInDirectionAtSpeed extends GearheadsCommand {
	private final Direction direction;
	private final double speed;

	public ElevatorMoveInDirectionAtSpeed(Direction direction, double speed) {
		this.direction = direction;
		this.speed = speed;
		Logger.formatInfo("Move %s at speed: %s", direction, speed);
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");//was finer
			Robot.elevator.moveAtSpeedAndDirection(speed, direction);
	}

	//@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		Logger.debug("in interrupted");//was finer
		Robot.elevator.holdCurrentPosition();
	}
}
