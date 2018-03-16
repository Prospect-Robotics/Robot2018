package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move elevator in given direction at given speed until interrupted.
 * Hold current position with PID when interrupted.
 */
public class MotorMoveInDirectionAtSpeed extends MotorCommand {
	private final Direction direction;
	private final Rate speed;

	public MotorMoveInDirectionAtSpeed(Motor motor, Direction direction, Rate speed) {
		super(motor, true);
		this.direction = direction;
		this.speed = speed;
		Logger.printFormat(LogType.INFO, "Move %s at speed: %s", direction, speed);
	}

	@Override
	protected void initialize() {
		super.initialize();
		motor.moveInDirectionAtSpeed(direction, speed);
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
