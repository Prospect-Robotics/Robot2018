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
	public MoveElevator() {
		halted = true;
		requires(Robot.elevator);
	}

	public MoveElevator(Direction direction) {
		this.direction = direction;
        positionMode = false;
		halted = false;
	}

	public MoveElevator(double position) {
		this.position = position;
        positionMode = true;
		halted = false;
	}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		if (halted) {
			Elevator.halt();
		}
		else {
            if (positionMode) {
                direction = Elevator.readPosition() > position ? Direction.DOWN : Direction.UP;
            }
			Elevator.move();
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
        double currentPosition;
        double targetPosition;
		boolean done;

		if (halted) return true;

		currentPosition = Elevator.readPosition();

        if (positionMode) {
            targetPosition = position;
        }
        else if (direction == Direction.DOWN) {
            targetPosition = 0;
        }
        else {
            targetPosition = Elevator.HEIGHT;
        }

		if (direction == Direction.DOWN) {
            done = currentPosition <= targetPosition;
		}
        else {
            done = currentPosition >= targetPosition;
        }
		if (done) {
			halted = true;
			Elevator.halt();
			return true;
		}
		return false;
	}

	@Override
	protected void end() {}

	@Override
	protected void interrupted() {}
}
