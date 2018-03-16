package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public abstract class MotorCommand extends GearheadsCommand {
	protected final Motor motor;
	public MotorCommand(Motor motor, boolean requiresMotor) {
		this.motor = motor;
		if(requiresMotor) {
			requires(motor);
		}
		Logger.debug("constructed");
	}

	// @Override
	protected void execute() {
		Logger.debug("in execute");
	}
	
	// @Override
	protected void initialize() {
		Logger.debug("in initialize");
		motor.disable();
	}
	
	// @Override
	protected void interrupted() {
		Logger.debug("interrupted");
	}
}
