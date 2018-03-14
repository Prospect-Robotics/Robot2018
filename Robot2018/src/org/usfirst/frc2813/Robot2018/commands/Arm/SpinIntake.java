// RobotBuilder Version: 2.0

// FIXME! This is a command from the intake subsystem, but is in the arm package. Move it!
package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;
import org.usfirst.frc2813.units.Direction;

/**
 * Spin or halt the Robot arm intake
 */
public class SpinIntake extends Command {
	/**
	 * Spin or stop the intake wheels
	 */
	private Direction direction;

	public SpinIntake() {
		direction = Direction.NEUTRAL;
	}

	public SpinIntake(Direction direction) {
		this.direction = direction;
	}

	// Called just before this Command runs the first time
	//@Override
	protected void initialize() {
		if (direction.isNeutral()) {
			Robot.intake.halt();
		}
		else {
			Robot.intake.setDirection(direction);
		}
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void execute() {}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		return direction.isNeutral();
	}

	// Called once after isFinished returns true
	@Override
	protected void end() {}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	@Override
	protected void interrupted() {}
}
