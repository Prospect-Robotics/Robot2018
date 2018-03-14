package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Maintain arm position. Arm does this actively with
 * PID via the motor controller, so no further intervention is
 * required to maintain this.
 */
public class ArmHoldPosition extends GearheadsInstantCommand {
	public ArmHoldPosition() {
		logger.info("Active hold position");
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		Robot.arm.holdCurrentPosition();
	}
}
