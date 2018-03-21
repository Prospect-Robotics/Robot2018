package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.Logger;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractDriveTrainCommand extends GearheadsCommand {
	protected final DriveTrain driveTrain;
	public AbstractDriveTrainCommand(DriveTrain subsystem, boolean requiresSubsystem) {
		this.driveTrain = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
		Logger.debug(this + " constructor.");
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + driveTrain + ", ...)";
	}
}
