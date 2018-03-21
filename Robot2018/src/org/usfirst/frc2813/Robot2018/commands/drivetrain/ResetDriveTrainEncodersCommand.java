package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class ResetDriveTrainEncodersCommand extends InstantCommand {
	/**
	 * Reset the DriveTrain encoders
	 */
    public ResetDriveTrainEncodersCommand() {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.driveTrain);
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.driveTrain.encoderStarboard.reset();
    	Robot.driveTrain.encoderPort.reset();
    }

}
