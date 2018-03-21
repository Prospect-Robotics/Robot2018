package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.logging.Logger;

/**
 * Reset the Gyro counter.
 */
public final class DriveTrainResetGyroInstant extends AbstractDriveTrainInstantCommand {

	/*
	 * Reset the Gyro
	 * Whatever is already running may be using the gyro... so require the subsystem.
	 */
    public DriveTrainResetGyroInstant(DriveTrain driveTrain) {
    	super(driveTrain, true /* require subsystem */);
    	setName(toString());
    }

    /**
     * Reset the gyro
     */
    protected void initialize() {
    	super.initialize();
    	Logger.info(this + " resetting drive train gyro.");
    	driveTrain.getGyro().reset();
    }
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ")"; 
    }
}
