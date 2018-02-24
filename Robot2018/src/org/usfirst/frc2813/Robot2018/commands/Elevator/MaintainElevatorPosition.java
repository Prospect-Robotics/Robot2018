package org.usfirst.frc2813.Robot2018.commands.Elevator;
import org.usfirst.frc2813.Robot2018.Constants;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

import com.ctre.phoenix.motorcontrol.ControlMode;


/**
 *
 */
public class MaintainElevatorPosition extends Command {

	
	
    //private PIDOutput debugPrintPIDOutput;
	private double targetPosition;
    public MaintainElevatorPosition() {
        requires(Robot.SRXElevator);
    }
	
	public void debugPrintPIDOutput(double pidOutput) {
		System.out.println("Output updated to: "+pidOutput);
		//Robot.elevator.speedController.pidWrite(pidOutput);TODO? REMOVE
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.srxElevator.selectProfileSlot(Constants.maintainPIDLoopIdx, Constants.maintainPIDLoopIdx);
    	/*double pos = Robot.SRXElevator.encoder.getDistance();
    	targetPosition = pos;		// TODO:  Used only for debug output - can remove when working
    	
    	controller.disable(); // If the source type changes, the encoder value will change suddenly, which will confuse the PID controller which will be BAD.
    	//PIDMoveElevator.controller.disable();
    	Robot.SRXElevator.encoder.setPIDSourceType(PIDSourceType.kDisplacement);
    	controller.setSetpoint(pos);
    	controller.enable();*/
    	
    	/*
    	 * When we release the elevator button the Maintain Elevator will take over
    	 * Talon SRX takes a primary posiiton
    	 * TODO:  Clean up Talon stuff once we are sure it works
    	 */
    	targetPosition = RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);		// TODO:  temp variable only for debug output - can remove when working
    	RobotMap.srxElevator.set(ControlMode.Position, targetPosition);

    	
    	
    	System.out.println("MaintainElevator:initialize:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
//		System.out.println("MaintainElevator:initialize:  pos ("+Robot.elevator.encoder.getPIDSourceType()+") to maintain is: "+pos+" (getDistance)");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	System.out.println("MaintainElevator:execute:  target position is: "+targetPosition+", getDistance is: "+RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
//    	System.out.println("MaintainElevator:execute:  target position is: "+targetPosition+", getDistance is: "+Robot.elevator.encoder.getDistance());
    	// nothing to do here, just let the PID controller run.
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; // never return true here.  When a default command returns it is immediately restarted.  I don't want to disable and re-enable the PID controller every loop tick.
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("MaintainElevator:end [Should never get here!]:  getDistance is: "+RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	System.out.println("MaintainElevator:interrupted:  getDistance is: "+RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR));
    }
    
}
