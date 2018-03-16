package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public abstract class MotorInstantCommand extends GearheadsInstantCommand {
	protected final Motor motor;
	public MotorInstantCommand(Motor motor, boolean requiresMotor) {
		this.motor = motor;
		if(requiresMotor) {
			requires(motor);
		}
		Logger.debug("constructed");
	}

	@Override
	protected void execute() {
		Logger.debug("execute");
	}
	
	@Override
	protected void initialize() {
		Logger.debug("initialize");
	}
	
	@Override
	protected void interrupted() {
		Logger.debug("interrupted");
	}
}
