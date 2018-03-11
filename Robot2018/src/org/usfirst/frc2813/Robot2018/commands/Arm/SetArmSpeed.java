package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command to set speed of arm.
 * FIXME! Does not take effect immediately if not already moving
 */
public class SetArmSpeed extends InstantCommand {
	private double speed;

	/**
	 * Set the speed of the elevator
	 * @param speed in feet per second
	 */
	public SetArmSpeed(double speed) {
		this.speed=speed;
	}

	// Called once when the command executes
	protected void initialize() {
		Robot.arm.setSpeed(speed);
	}
}
