// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Elevator;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

/**
 * Move or maintain elevator position.
 * No argument to halt.
 * Direction to move in a direction.
 * Position to move to that position in inches above floor.
 */
public class MoveElevator extends Command {
	private boolean halted;
	private Direction direction;
	private double position;
    private boolean positionMode;  // true if we are moving to a position
                                   // false if we move by direction and speed
    private final Elevator elevator;
	public MoveElevator() {
		this.elevator = Robot.elevator;
		halted = true;
		requires(Robot.elevator);
	}

	public MoveElevator(Direction direction) {
		this.elevator = Robot.elevator;
		this.direction = direction;
        positionMode = false;
		halted = false;
	}

	public MoveElevator(double position) {
		this.elevator = Robot.elevator;
		this.position = position;
        positionMode = true;
		halted = false;
	}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		if (halted) {
			Robot.elevator.holdCurrentPosition();
		}
		else {
            if (positionMode) {
                direction = Robot.elevator.readPosition() > position ? Direction.DOWN : Direction.UP;
            }
            Robot.elevator.move(direction, position);
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
        double currentPosition;
        double targetPosition;
		boolean done;

		if (halted) return true;

		currentPosition = elevator.readPosition();

		/*
		 * NB: When holding "up" or "down" buttons we are not
		 * in positionMode, and targetPosition is set to 0 or HEIGHT
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
            targetPosition = elevator.HEIGHT;
        }

		if (direction == Direction.DOWN) {
            done = currentPosition <= targetPosition;
		}
        else {
            done = currentPosition >= targetPosition;
        }
		System.out.println("MoveElevator [PositionMode=" + positionMode + ", Direction=" + direction + ", Target=" + targetPosition + ", Position=" + currentPosition + "]");
		if (done) {
			System.out.println("MoveElevator_FINISHED");
			halted = true;
			elevator.holdCurrentPosition();
			return true;
		}
		return false;
	}

	@Override
	protected void end() {}

	@Override
	protected void interrupted() {}
}
