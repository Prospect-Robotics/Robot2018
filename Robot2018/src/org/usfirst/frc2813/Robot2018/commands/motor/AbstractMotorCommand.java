package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractMotorCommand extends GearheadsCommand {
	protected final Motor motor;
	public AbstractMotorCommand(Motor subsystem, boolean requiresSubsystem) {
		this.motor = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + motor + ", ...)";
	}
}
