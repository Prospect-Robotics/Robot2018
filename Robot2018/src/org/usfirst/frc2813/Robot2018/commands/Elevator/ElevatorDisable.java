package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public class ElevatorDisable extends GearheadsInstantCommand {
	public ElevatorDisable() {
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in execute");
		Robot.elevator.disable();
	}
}
