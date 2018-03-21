package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;

/**
 * Abstract DriveTrain instant command
 */
public abstract class AbstractIntakeInstantCommand extends GearheadsInstantCommand {
	protected final Intake intake;

	public AbstractIntakeInstantCommand(Intake subsystem, boolean requiresSubsystem) {
		this.intake = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + intake + ", ...)";
	}
}
