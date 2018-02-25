// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2813.Robot2018.commands.DriveTrain.Obsolete;
import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.Robot;

/**
 *
 */
public class ShiftGears extends InstantCommand {
	/**
	 * Shift from high to low gear on the DriveTrain
	 */
    public ShiftGears() {
    	super();
    }

    // Called just before this Command runs the first time
    //@Override
    protected void initialize() {
    	Robot.driveTrain.gearShift.set(!Robot.driveTrain.gearShift.get());
    	
    }
}