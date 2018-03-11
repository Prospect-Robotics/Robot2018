package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Maintain elevator position.
 */
public class ElevatorHoldPosition extends GearheadsInstantCommand {
	public ElevatorHoldPosition() {
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		logger.finer("in execute");
		Robot.elevator.holdCurrentPosition();
	}
}
