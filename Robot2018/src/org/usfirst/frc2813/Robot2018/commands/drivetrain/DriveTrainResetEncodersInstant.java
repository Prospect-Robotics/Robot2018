package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Reset the encoders
 */
public class DriveTrainResetEncodersInstant extends AbstractDriveTrainInstantCommand {

	/**
	 * Reset the DriveTrain encoders
	 * Whatever is already running may be using the encoders... so require the subsystem.
	 */
    public DriveTrainResetEncodersInstant(DriveTrain driveTrain) {
        super(driveTrain, true /* require subsystem */);
    }

    // Called once when the command executes
    protected void initialize() {
    	super.initialize();
    	driveTrain.getEncoderStarboard().reset();
    	driveTrain.getEncoderPort().reset();
    }
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ")"; 
    }

}
