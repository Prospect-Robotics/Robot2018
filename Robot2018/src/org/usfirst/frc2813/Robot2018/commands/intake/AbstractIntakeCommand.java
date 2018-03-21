package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractIntakeCommand extends GearheadsCommand {
	protected final Intake intake;
	public AbstractIntakeCommand(Intake subsystem, boolean requiresSubsystem) {
		this.intake = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + intake + ", ...)";
	}
}
