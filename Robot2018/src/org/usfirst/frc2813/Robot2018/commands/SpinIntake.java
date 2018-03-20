package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Spin the intake IN or OUT. This command is Not instant! When disabled, the intake stops.
 */
public class SpinIntake extends Command {
	private final Direction direction;
	public SpinIntake(Direction direction) {
		this.direction = direction;
		requires(Robot.intake);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		Logger.info("SpinIntake Set to Move " + direction);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.intake.spin(direction);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		//Set the speed to 0 when button is released.
		Logger.info("SpinIntake end.");
		Robot.intake.stop();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		Logger.info("SpinIntake interrupted.");
		Robot.intake.stop();
	}

	public String toString() {
		return "SpinIntake: " + direction;
	}
}
