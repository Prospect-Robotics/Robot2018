package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractMotorCommand extends GearheadsCommand {
	protected final Motor motor;
	public AbstractMotorCommand(Motor subsystem, boolean requiresSubsystem) {
		this.motor = subsystem;
		// NB: This evil is required to get a compile when Motor is built for standalone mode
		if(requiresSubsystem && Subsystem.class.isInstance(subsystem)) {
			requires((Subsystem)(Object)subsystem);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + motor + ", ...)";
	}
}
