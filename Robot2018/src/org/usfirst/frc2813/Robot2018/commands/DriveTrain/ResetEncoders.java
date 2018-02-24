package org.usfirst.frc2813.Robot2018.commands.DriveTrain;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class ResetEncoders extends InstantCommand {

    public ResetEncoders() {
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
