package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDMoveElevator extends Command {
	private final double fps;

	public PIDMoveElevator(double feetPerSecond) {
		requires(Robot.elevator);
		fps = feetPerSecond;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		if (Robot.elevator.encoder.getPIDSourceType() != PIDSourceType.kRate) {
			Robot.elevator.controller.disable();
			Robot.elevator.encoder.setPIDSourceType(PIDSourceType.kRate);
		}
		Robot.elevator.controller.setSetpoint(fps);
		Robot.elevator.controller.enable();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true or another command which requires
	// one or more of the same subsystems is scheduled to run
	protected void end() {
		// Should I disable the PID controller here?
	}
}
