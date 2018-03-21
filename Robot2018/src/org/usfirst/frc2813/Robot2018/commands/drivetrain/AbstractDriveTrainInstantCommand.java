package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Abstract DriveTrain instant command
 */
public abstract class AbstractDriveTrainInstantCommand extends GearheadsInstantCommand {
	protected final DriveTrain driveTrain;

	public AbstractDriveTrainInstantCommand(DriveTrain subsystem, boolean requiresSubsystem) {
		this.driveTrain = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
	}
	
	public String toString() {
		return getClass().getSimpleName() + "(" + driveTrain + ", ...)";
	}
}