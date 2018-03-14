package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

import logging.Logger;

/**
 * Disable arm. Subsystem will quiesce.
 */
public class ArmDisable extends GearheadsInstantCommand {
	public ArmDisable() {
		Logger.info("About to disable");
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");//was finer
		Robot.arm.disable();
	}
}
