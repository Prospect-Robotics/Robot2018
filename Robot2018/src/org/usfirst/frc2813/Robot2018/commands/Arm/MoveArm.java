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
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 *
 */
public class MoveArm extends Command {
	private TalonSRX speedController1;
	private boolean direction;
	
	private static final double ONE_DEGREE_PER_SECOND = RobotMap.ARM_ONE_DEGREE_PER_SECOND; // 36 = 360 (number of degrees in a circle) / 10 (set(ControlMode.Velocity) expects velocity in units of ticks per 100ms; a second is 1000ms).
	
	/**
	 * Move the arm up or down
	 * @param upDown the direction, true for up, false for down
	 */
    public MoveArm(boolean upDown) {//if moving up, true; down, false; stop is for release of button
    	direction=upDown;
        requires(Robot.arm);
    }

    // Called just before this Command runs the first time
    //@Override
    protected void initialize() {
    	RobotMap.srxArm.selectProfileSlot(1, 1);
    	
    	speedController1 = Robot.arm.srxController;
    }

    // Called repeatedly when this Command is scheduled to run
    //@Override
    protected void execute() {
    	if(direction == true) {
    		speedController1.set(ControlMode.Velocity,-5*ONE_DEGREE_PER_SECOND);//TODO not sure if 1 or -1 is up
    	}
    	else if (direction == false) {
    		speedController1.set(ControlMode.Velocity,5*ONE_DEGREE_PER_SECOND);//TODO not sure if -1 or 1 is down
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    //@Override
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
