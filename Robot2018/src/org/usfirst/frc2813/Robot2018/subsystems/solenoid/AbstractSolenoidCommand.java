package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractSolenoidCommand extends GearheadsCommand {
	protected final Solenoid Solenoid;
	public AbstractSolenoidCommand(Solenoid subsystem, boolean requiresSubsystem) {
		this.Solenoid = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
		Logger.debug(this + " constructor.");
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + Solenoid + ", ...)";
	}
}
