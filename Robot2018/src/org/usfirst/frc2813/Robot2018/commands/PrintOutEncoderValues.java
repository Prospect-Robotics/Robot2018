package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PrintOutEncoderValues extends Command {
	private Encoder myEncoder1;
	private Encoder myEncoder2;
	//private Encoder myEncoder3;
	private long last;
	//private int time;

    public PrintOutEncoderValues(int time, Encoder myEncoder1, Encoder myEncoder2) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	this.last = 0;
    	this.myEncoder1 = myEncoder1;
    	this.myEncoder2=myEncoder2;
    	//this.myEncoder3=myEncoder3;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if((System.currentTimeMillis() - last) >= 100) {
	    	System.out.println(
	    			myEncoder1.get() + " [" + myEncoder1.getRaw() + "] " +myEncoder1.getDistance()+" "+ myEncoder1.getDistance()*19.25 +" ["+myEncoder1.getDistancePerPulse()+"] "+myEncoder1.getDirection()+" ["+myEncoder1.getStopped()+"]\n" +
	    			myEncoder2.get() + " [" + myEncoder2.getRaw() + "]" +myEncoder2.getDistance()+" "+ myEncoder2.getDistance()*19.25+" ["+myEncoder2.getDistancePerPulse()+"] "+myEncoder1.getDirection()+" ["+myEncoder1.getStopped()+"]\n"
	    			);
	    	this.last = System.currentTimeMillis();
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
