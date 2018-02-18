package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class MaintainElevatorPosition extends Command {

    //private PIDOutput debugPrintPIDOutput;
	private double targetPosition;
    public final PIDController controller = new PIDController(2.0, 0, 0, Robot.elevator.encoder, this::debugPrintPIDOutput);	// Kp, Ki, Kd
    public MaintainElevatorPosition() {
        requires(Robot.elevator);
    }
	
	public void debugPrintPIDOutput(double pidOutput) {
		System.out.println("Output updated to: "+pidOutput);
		Robot.elevator.speedController.pidWrite(pidOutput);
	}

    // Called just before this Command runs the first time
    protected void initialize() {
    	double pos = Robot.elevator.encoder.getDistance();
    	targetPosition = pos;		// TODO:  Used only for debug output - can remove when working
    	
    	controller.disable(); // If the source type changes, the encoder value will change suddenly, which will confuse the PID controller which will be BAD.
    	//PIDMoveElevator.controller.disable();
    	Robot.elevator.encoder.setPIDSourceType(PIDSourceType.kDisplacement);
    	controller.setSetpoint(pos);
    	controller.enable();
    	
		System.out.println("MaintainElevator:initialize:  pos ("+Robot.elevator.encoder.getPIDSourceType()+") to maintain is: "+pos+" (getDistance)");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	System.out.println("MaintainElevator:execute:  target position is: "+targetPosition+", getDistance is: "+Robot.elevator.encoder.getDistance());
    	// nothing to do here, just let the PID controller run.
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; // never return true here.  When a default command returns it is immediately restarted.  I don't want to disable and re-enable the PID controller every loop tick.
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("MaintainElevator:end [Should never get here!]:  getDistance is: "+Robot.elevator.encoder.getDistance());
    	controller.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	System.out.println("MaintainElevator:interrupted:  getDistance is: "+Robot.elevator.encoder.getDistance());
    	controller.disable();
    }
    
}
