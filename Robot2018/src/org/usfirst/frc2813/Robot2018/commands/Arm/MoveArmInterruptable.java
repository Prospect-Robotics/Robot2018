package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;

public class MoveArmInterruptable extends MoveArm {
	public MoveArmInterruptable(Direction direction) {super(direction);}
	public MoveArmInterruptable(Double position) {super(position);}
	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		Robot.arm.holdCurrentPosition();
	}
}
