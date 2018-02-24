package org.usfirst.frc2813.Robot2018.commands.Arm;
import org.usfirst.frc2813.Robot2018.Constants;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

import com.ctre.phoenix.motorcontrol.ControlMode;


/**
 *
 */
public class MaintainArmPosition extends Command {
	private double targetPosition;
    public MaintainArmPosition() {
        requires(Robot.arm);
    }
	
	public void debugPrintPIDOutput(double pidOutput) {
		System.out.println("Output updated to: "+pidOutput);
		//Robot.elevator.speedController.pidWrite(pidOutput);TODO? REMOVE
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.srxArm.selectProfileSlot(0, 0);
    	/*
    	 * When we release the elevator button the Maintain Elevator will take over
    	 * Talon SRX takes a primary posiiton
    	 * TODO:  Clean up Talon stuff once we are sure it works
    	 */
    	targetPosition = RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);		// TODO:  temp variable only for debug output - can remove when working
    	RobotMap.srxArm.set(ControlMode.Position, targetPosition);
    	System.out.println("MaintainArm:initialize:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Let PID Controller Run
    	System.out.println("MaintainArm:execute:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
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
