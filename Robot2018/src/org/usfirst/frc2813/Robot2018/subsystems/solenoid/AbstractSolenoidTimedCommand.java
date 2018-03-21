package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsTimedCommand;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractSolenoidTimedCommand extends GearheadsTimedCommand {
	protected final Solenoid solenoid;
	public AbstractSolenoidTimedCommand(Solenoid subsystem, boolean requiresSubsystem, double timeout) {
		super(timeout);
		this.solenoid = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + solenoid + ", ...)";
	}
}
