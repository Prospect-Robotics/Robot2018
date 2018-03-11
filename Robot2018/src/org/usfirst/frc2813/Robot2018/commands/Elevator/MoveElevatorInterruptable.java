package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;

public class MoveElevatorInterruptable extends MoveElevator {
	public MoveElevatorInterruptable(Direction direction) {super(direction);}
	public MoveElevatorInterruptable(Double position) {super(position);}
	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		Robot.elevator.holdCurrentPosition();
	}
}
