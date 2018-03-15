package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move elevator in given direction at given speed until interrupted.
 * Hold current position with PID when interrupted.
 */
public class ElevatorMoveInDirectionAtSpeed extends GearheadsCommand {
	private final Direction direction;
	private final Rate speed;

	public ElevatorMoveInDirectionAtSpeed(Direction direction, Rate speed) {
		this.direction = direction;
		this.speed = speed;
		logger.info(String.format("Move %s at speed: %s", direction, speed));
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
			Robot.elevator.moveInDirectionAtSpeed(direction, speed);
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
