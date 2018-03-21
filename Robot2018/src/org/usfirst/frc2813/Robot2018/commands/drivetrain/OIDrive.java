// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.drivetrain;
import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Drives the robot in arcade drive with Robot.oi.joystick1.
 */
public class OIDrive extends Command {
	public OIDrive() {
		requires(Robot.driveTrain);
	}

	// Called just before this Command runs the first time
	@Override
	protected void initialize() {}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		Robot.driveTrain.arcadeDrive(Robot.oi.joystick1, Robot.oi.joystick2);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}
}
