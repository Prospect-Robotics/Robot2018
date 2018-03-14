package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move elevator to position. Motor controller does this without
 * further intervention.
 */
public class ElevatorMoveToPosition extends GearheadsInstantCommand {
	private final int position;

	public ElevatorMoveToPosition(int position) {
		this.position = position;
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		Robot.elevator.moveToPosition(position);
	}
}
