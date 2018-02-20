package org.usfirst.frc2813.Robot2018.commands;
import org.usfirst.frc2813.Robot2018.Constants;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;


/**
 *
 */
public class MaintainArmPosition extends Command {

	
	
	private double targetPosition;
	
    public MaintainArmPosition() {
        requires(Robot.arm);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	    	
    	Robot.elevator.encoder.setPIDSourceType(PIDSourceType.kDisplacement);

    	/*
    	 * When we release the elevator button the Maintain Elevator will take over
    	 * Talon SRX takes a primary posiiton
    	 * TODO:  Clean up Talon stuff once we are sure it works
    	 */
    	targetPosition = RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);		// TODO:  temp variable only for debug output - can remove when working
    	RobotMap.srxArm.set(ControlMode.Position, targetPosition);

    	
    	
    	System.out.println("MaintainElevator:initialize:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	System.out.println("MaintainElevator:execute:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
    	// nothing to do here, just let the PID controller run.
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; // never return true here.  When a default command returns it is immediately restarted.  I don't want to disable and re-enable the PID controller every loop tick.
    }

    // Called once after isFinished returns true
    protected void end() {
    	}

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	}
    
}
