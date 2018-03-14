package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

import logging.Logger;

/**
 * Move arm to position. Motor controller does this without
 * further intervention.
 */
public class ArmMoveToPosition extends GearheadsInstantCommand {
	private final double positionInInches;

	public ArmMoveToPosition(double positionInInches) {
		Logger.info("Move to position: " + positionInInches);
		this.positionInInches = positionInInches;
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");//was finer
		Robot.arm.moveToPosition(positionInInches);
	}
}
