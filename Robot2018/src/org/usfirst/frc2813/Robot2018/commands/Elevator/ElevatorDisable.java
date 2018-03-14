package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

import logging.Logger;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public class ElevatorDisable extends GearheadsInstantCommand {
	public ElevatorDisable() {
		Logger.info("About to disable");
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in execute");//was finer
		Robot.elevator.disable();
	}
}
