// RobotBuilder Version: 2.0

// FIXME! This is a command from the intake subsystem, but is in the arm package. Move it!
package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;

/**
 * Spin or halt the Robot arm intake
 */
public class SpinIntake extends Command {
	/**
	 * Spin the intake wheels
	 * 
	 * @param spinDirectionIn - true is spin direction in; false if out
	 * @param stopAtEnd - set motors to 0 when button is released
	 */
	private boolean halted;
	private Direction direction;

	public SpinIntake() {
		halted = true;
	}
	public SpinIntake(Direction direction) {
		Arm.setIntakeDirection(direction);
		halted = false;
	}

	// Called just before this Command runs the first time
	//@Override
	protected void initialize() {}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {
		if (halted) {
			Arm.haltIntake();
		}
		else { 
			Arm.spinIntake();
		}
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
