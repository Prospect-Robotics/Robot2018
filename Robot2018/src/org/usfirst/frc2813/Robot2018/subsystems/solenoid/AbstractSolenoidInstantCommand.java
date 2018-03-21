package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;

/**
 * Abstract Solenoid instant command
 */
public abstract class AbstractSolenoidInstantCommand extends GearheadsInstantCommand {
	protected final Solenoid solenoid;

	public AbstractSolenoidInstantCommand(Solenoid subsystem, boolean requiresSubsystem) {
		this.solenoid = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + solenoid + ", ...)";
	}
}
