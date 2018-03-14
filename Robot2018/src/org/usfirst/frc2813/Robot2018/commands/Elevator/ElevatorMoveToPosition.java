package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

import logging.Logger;

/**
 * Move elevator to position. Motor controller does this without
 * further intervention.
 */
public class ElevatorMoveToPosition extends GearheadsInstantCommand {
	private final double positionInInches;

	public ElevatorMoveToPosition(double positionInInches) {
		Logger.info("Move to position: " + positionInInches);
		this.positionInInches = positionInInches;
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in initialize");//was finer
		Robot.elevator.moveToPosition(positionInInches);
	}
}
