package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.interfaces.Gyro;

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
    	driveTrain.getGyro().reset();
    }
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ")"; 
    }
}
