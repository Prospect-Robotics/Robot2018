package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.interfaces.Gyro;

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
		Logger.debug(this + " constructed");
	}

	// @Override
	protected void execute() {
		Logger.debug(this + " in execute");
	}
	
	// @Override
	protected void initialize() {
		Logger.debug(this + " in initialize");
	}
	
	// @Override
	protected void interrupted() {
		Logger.debug(this + " interrupted");
	}
	
    // @Override
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ", ...)";
    }
}
