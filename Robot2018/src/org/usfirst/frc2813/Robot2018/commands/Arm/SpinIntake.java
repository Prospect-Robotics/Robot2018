// RobotBuilder Version: 2.0

// FIXME! This is a command from the intake subsystem, but is in the arm package. Move it!
package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;

/**
 *
 */
public class SpinIntake extends Command {
	private Direction spinDirection;
	private boolean stopAtEnd;
	private SpeedController speedController1;
	/**
	 * Spin the intake wheels
	 * 
	 * @param spinDirectionIn - true is spin direction in; false if out
	 * @param stopAtEnd - set motors to 0 when button is released
	 */
	public SpinIntake(Direction spinDirectionIn, boolean stopAtEnd) {
		this.spinDirection=spinDirectionIn;
		this.stopAtEnd=stopAtEnd;
		requires(Robot.intake);
	}

	// Called just before this Command runs the first time
	//@Override
	protected void initialize() {
		speedController1 = Robot.intake.speedController1;
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		if (stopAtEnd) {
			speedController1.set(0);
		}
		else { 
			Robot.intake.spin(spinDirection);
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {
	}
}
