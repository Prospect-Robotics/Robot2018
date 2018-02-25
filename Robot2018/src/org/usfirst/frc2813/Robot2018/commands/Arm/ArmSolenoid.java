// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.RobotMap;

/**
 *
 */
public class ArmSolenoid extends InstantCommand {
	/**
	 * Switch the position of the intake pistons
	 */
    public ArmSolenoid() {
    	super();
    }

    // Called just before this Command runs the first time
    //@Override
    protected void initialize() {
    	RobotMap.armSingleSolenoid.set(!RobotMap.armSingleSolenoid.get());//.get is true if open, false if closed
    }
}
