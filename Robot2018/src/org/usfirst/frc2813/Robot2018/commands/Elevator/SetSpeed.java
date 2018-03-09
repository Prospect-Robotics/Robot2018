package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command to set speed of elevator.
 * FIXME! Does not take effect without moveElevator - should we change this?
 */
public class SetSpeed extends InstantCommand {
	double speed;

	/**
	 * Set the speed of the elevator
	 * @param speed in feet per second
	 */
	public SetSpeed(double speed) {
		this.speed=speed;
	}

	// Called once when the command executes
	protected void initialize() {
		Elevator.setSpeedFeetPerSecond(speed);
	}
}
