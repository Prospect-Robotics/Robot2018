package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public class ElevatorTesting extends GearheadsCommand {
	public ElevatorTesting() {
		requires(Robot.elevator);
	}
	
	protected void execute() {
	}
	// @Override
	protected void initialize() {
		Robot.elevator.encoderRelativePositionTestingMode();
	}
	
	protected boolean isFinished() {
		return false;
	}
}
