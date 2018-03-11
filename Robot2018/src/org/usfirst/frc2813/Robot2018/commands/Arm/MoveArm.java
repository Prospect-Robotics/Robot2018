package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;


/**
 * Move or maintain arm position.
 * No argument to halt.
 * Direction to move in a direction.
 * Position to move to that position in degrees from bottom.
 */
public class MoveArm extends Command {
	private boolean halted;
	private Direction direction;
	private double position;
	private boolean positionMode;  // true if we are moving to a position
	// false if we move by direction and speed
	public MoveArm() {
		halted = true;
		requires(Robot.arm);
	}

	public MoveArm(Direction direction) {
		this.direction = direction;
		positionMode = false;
		halted = false;
	}

	public MoveArm(double position) {
		this.position = position;
		positionMode = true;
		halted = false;
	}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		if (halted) {
			Arm.haltArm();
		}
		else {
			if (positionMode) {
				direction = Arm.readArmPosition() > position ? Direction.DOWN : Direction.UP;
				Arm.moveArm(position);
			}
			else {
				Arm.moveArm(direction);
			}
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		double currentPosition;
		double targetPosition;
		boolean done;

		if (halted) return true;

		currentPosition = Arm.readArmPosition();

		/*
		 * NB: When holding "up" or "down" buttons we are not
		 * in positionMode, and targetPosition is set to 0 or DEGREES
		 * and limit switches will stop motion.
		 * One area of concern is that any offset in encoder values
		 * or inaccuracy will result in never reaching done state
		 * even if the soft limit is hit or the limit switch triggers.
		 * "done" should also be checking the controller to see if
		 * either soft or hard limit is it.  Then we need to adjust
		 * if we hit a physical limit without hitting the absolute value, 
		 * HEIGHT or 0.
		 */
		if (positionMode) {
			targetPosition = position;
		}
		else if (direction == Direction.DOWN) {
			targetPosition = 0;
		}
		else {
			targetPosition = Arm.DEGREES;
		}

		if (direction == Direction.DOWN) {
			done = currentPosition <= targetPosition;
		}
		else {
			done = currentPosition >= targetPosition;
		}

		if (done) {
			System.out.println("MoveArm_FINISHED");
			halted = true;
			Arm.haltArm();
			return true;
		}
		return false;
	}

	@Override
	protected void end() {}

	@Override
	protected void interrupted() {}
}