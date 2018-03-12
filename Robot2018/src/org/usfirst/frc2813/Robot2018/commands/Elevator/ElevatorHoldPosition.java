package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Maintain elevator position. Elevator does this actively with
 * PID via the motor controller, so no further intervention is
 * required to maintain this.
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
