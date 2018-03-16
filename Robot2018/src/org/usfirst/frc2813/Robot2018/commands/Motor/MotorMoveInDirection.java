package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;

/**
 * Move elevator in given direction until interrupted.
 * Hold current position with PID when interrupted.
 */
public class MotorMoveInDirection extends MotorCommand {
	private final Direction direction;

	public MotorMoveInDirection(Motor motor, Direction direction) {
		super(motor, true);
		this.direction = direction;
	}

	@Override
	protected void initialize() {
		super.initialize();
		motor.moveInDirection(direction);
	}

	@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		super.interrupted();
		motor.holdCurrentPosition();
	}
}
