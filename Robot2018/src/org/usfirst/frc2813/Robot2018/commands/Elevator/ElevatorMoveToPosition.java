package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Move elevator to position. Motor controller does this without
 * further intervention.
 */
public class ElevatorMoveToPosition extends GearheadsInstantCommand {
	private final double position;

	public ElevatorMoveToPosition(double position) {
		this.position = position;
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		Robot.elevator.moveToPosition(position);
	}
}
