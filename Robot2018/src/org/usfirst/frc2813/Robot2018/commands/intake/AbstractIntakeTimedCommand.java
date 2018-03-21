package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.GearheadsTimedCommand;
import org.usfirst.frc2813.Robot2018.subsystems.Intake;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractIntakeTimedCommand extends GearheadsTimedCommand {
	protected final Intake intake;
	public AbstractIntakeTimedCommand(Intake subsystem, boolean requiresSubsystem, double timeout) {
		super(timeout);
		this.intake = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + intake + ", ...)";
	}
}
