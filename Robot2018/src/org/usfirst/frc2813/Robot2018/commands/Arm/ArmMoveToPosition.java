package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move arm to position. Motor controller does this without
 * further intervention.  Length can be in any units you like.
 */
public class ArmMoveToPosition extends GearheadsInstantCommand {
	private final int position;

	public ArmMoveToPosition(int position) {
		this.position = position;
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.info("Move to position: " + position);
		Robot.arm.moveToPosition(position);
	}
}
