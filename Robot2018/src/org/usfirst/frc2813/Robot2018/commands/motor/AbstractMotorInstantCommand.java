package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract motor instant command
 */
public abstract class AbstractMotorInstantCommand extends GearheadsInstantCommand {
	protected final Motor motor;
	public AbstractMotorInstantCommand(Motor subsystem, boolean requiresSubsystem) {
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
