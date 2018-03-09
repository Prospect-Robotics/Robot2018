// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Elevator;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

/**
 * Move or maintain elevator position.
 */
public class MoveElevator extends Command {
	private boolean halted;
	private Direction direction;

	public MoveElevator() {
		halted = true;
		requires(Robot.elevator);
	}
	public MoveElevator(Direction direction) {
		Elevator.setDirection(direction);
		this.direction = direction;
		halted = false;
	}

	// Called just before this Command runs the first time
	//@Override
	protected void initialize() {
		Elevator.halt();
	}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		if (halted) {
			Elevator.halt();
		}
		else {
			Elevator.move();
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		double current_position, end_position, tmp;
		if (halted) {
			return true;
		}
		current_position = Elevator.readPosition();
		end_position = Elevator.readEndPosition();

		// swap so we can do one compare
		if (direction == Direction.DOWN) {
			tmp = current_position;
			current_position = end_position;
			end_position = tmp;
		}
		if (current_position >= end_position) {
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
