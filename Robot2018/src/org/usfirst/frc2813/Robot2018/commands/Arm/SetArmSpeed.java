package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.subsystems.Arm;
import org.usfirst.frc2813.Robot2018.Robot;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Command to set speed of arm.
 * FIXME! Does not take effect without moveArm - should we change this?
 */
public class SetArmSpeed extends InstantCommand {
	private double newSpeedInchesPerSecond;
    private final Arm arm;

	/**
	 * Set the speed of the arm
	 * @param speed in feet per second
	 */
	public SetArmSpeed(double newSpeedInchesPerSecond) {
		this.arm = Robot.arm;
		this.newSpeedInchesPerSecond = newSpeedInchesPerSecond;

	}

	// Called once when the command executes
	protected void initialize() {
		arm.setSpeedInchesPerSecond(newSpeedInchesPerSecond); 
	}
}