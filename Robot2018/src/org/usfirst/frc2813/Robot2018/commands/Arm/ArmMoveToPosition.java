package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Move arm to position. Motor controller does this without
 * further intervention.
 */
public class ArmMoveToPosition extends GearheadsInstantCommand {
	private final double positionInInches;

	public ArmMoveToPosition(double positionInInches) {
		logger.info("Move to position: " + positionInInches);
		this.positionInInches = positionInInches;
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		Robot.arm.moveToPosition(positionInInches);
	}
}
