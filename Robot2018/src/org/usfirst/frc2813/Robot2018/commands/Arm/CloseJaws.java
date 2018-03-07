// RobotBuilder Version: 2.0

// FIXME! This is a command from the intake subsystem, but is in the arm package. Move it!
package org.usfirst.frc2813.Robot2018.commands.Arm;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Robot;

/**
 * Close the Robot arm jaws
 */
public class CloseJaws extends Command {
	public CloseJaws() {
		requires(Robot.arm);
	}

	//@Override
	protected void initialize() {}

	@Override
	protected void execute() {
		Robot.arm.closeJaws();
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		return true;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}
}
