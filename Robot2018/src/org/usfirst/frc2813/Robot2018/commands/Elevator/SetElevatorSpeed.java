package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command to set speed of elevator.
 * FIXME! Does not take effect without moveElevator - should we change this?
 */
public class SetElevatorSpeed extends InstantCommand {
	private double newSpeedInchesPerSecond;
    private final Elevator elevator;

	/**
	 * Set the speed of the elevator
	 * @param speed in feet per second
	 */
	public SetElevatorSpeed(double newSpeedInchesPerSecond) {
		this.elevator = Robot.elevator;
		this.newSpeedInchesPerSecond=newSpeedInchesPerSecond;
	}

	// Called once when the command executes
	protected void initialize() {
		elevator.setSpeedInchesPerSecond(newSpeedInchesPerSecond); 
	}
}
