package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Disable arm. Subsystem will quiesce.
 */
public class ArmDisable extends GearheadsInstantCommand {
	public ArmDisable() {
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");
		Robot.arm.disable();
	}
}
