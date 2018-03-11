package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Command to set speed of elevator.
 * FIXME! Does not take effect immediately if not already moving
 */
public class ElevatorSetSpeed extends GearheadsInstantCommand {
	private double speed;

	/**
	 * Set the speed of the elevator
	 * @param speed in feet per second
	 */
	public ElevatorSetSpeed(double speed) {
		this.speed=speed;
	}

	// Called once when the command executes
	protected void initialize() {
		Robot.elevator.setSpeed(speed);
	}
}