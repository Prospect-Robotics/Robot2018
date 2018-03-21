package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.GearheadsTimedCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractDriveTrainTimedCommand extends GearheadsTimedCommand {
	protected final DriveTrain driveTrain;
	public AbstractDriveTrainTimedCommand(DriveTrain subsystem, boolean requiresSubsystem, double timeout) {
		super(timeout);
		this.driveTrain = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + driveTrain + ", ...)";
	}
}
