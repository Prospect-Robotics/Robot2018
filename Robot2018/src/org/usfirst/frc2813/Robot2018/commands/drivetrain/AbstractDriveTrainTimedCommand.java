package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.commands.GearheadsTimedCommand;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.interfaces.Gyro;

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
