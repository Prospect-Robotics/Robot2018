package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.util.unit.Direction;
import org.usfirst.frc2813.util.unit.values.Rate;

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
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.info(String.format("Move %s at speed %s", direction, speed));
		logger.finer("in initialize");
			Robot.arm.moveInDirectionAtSpeed(direction, speed);
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
