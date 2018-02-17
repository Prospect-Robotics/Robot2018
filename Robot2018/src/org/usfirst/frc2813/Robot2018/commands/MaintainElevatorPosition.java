package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class MaintainElevatorPosition extends Command {

    public MaintainElevatorPosition() {
        requires(Robot.elevator);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	double pos = Robot.elevator.encoder.getDistance();
    	Robot.elevator.controller.disable(); // If the source type changes, the encoder value will change suddenly, which will confuse the PID controller which will be BAD.
    	Robot.elevator.encoder.setPIDSourceType(PIDSourceType.kDisplacement);
    	Robot.elevator.controller.setSetpoint(pos);
    	Robot.elevator.controller.enable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	// nothing to do here, just let the PID controller run.
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false; // never return true here.  When a default command returns it is immediately restarted.  I don't want to disable and re-enable the PID controller every loop tick.
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.elevator.controller.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.elevator.controller.disable();
    }
    
}
