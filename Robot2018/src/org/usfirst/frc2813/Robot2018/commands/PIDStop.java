package org.usfirst.frc2813.Robot2018.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDStop extends Command {
	
	private final double Kp = 0.8, Ki = 0, Kd = 0;
	
	private DriveTrain driveTrain;

	private boolean initialized = false;
	private List<PIDController> encoders = new ArrayList<PIDController>(); 
	private List<PIDOutput>     speedControllers = new ArrayList<PIDOutput>(); 
	
	// Write to a list of PID controllers
	private PIDOutput driveAll = new PIDOutput() { public void pidWrite(double output) { for(PIDOutput pid : speedControllers) { pid.pidWrite(output); } } };
	
    public PIDStop(DriveTrain driveTrain) {
    	this.driveTrain = driveTrain;
        requires(driveTrain);
    }

    protected void initializeEncoders() {
    	if(!initialized) {
        	// Build a list of all the speed controllers, we can use when we need to drive them all 
        	speedControllers.add(driveTrain.getSpeedControllerPort());
        	speedControllers.add(driveTrain.getSpeedControllerStarboard());
        	// Initialize one PID for every working encoder.
    		if(driveTrain.encoderPortFunctional && driveTrain.encoderStarboardFunctional) {
    			encoders.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderPort(),      driveTrain.getSpeedControllerPort()));
    			encoders.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderStarboard(), driveTrain.getSpeedControllerStarboard()));
    		}
    		else if(driveTrain.encoderPortFunctional) {
    			encoders.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderPort(),      driveAll));
    		} else if(driveTrain.encoderStarboardFunctional) {
    			encoders.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderStarboard(), driveAll));
    		} else {
    			DriverStation.reportWarning("Can't PIDStop, BOTH ENCODERS OFFLINE", true);
    		}
    		// Configure the speed PID controller common values
    		for(PIDController pid : encoders) {
    			pid.setAbsoluteTolerance(20); // 20 encoder ticks +/-, close enough for whatever encoder is used
    			pid.setOutputRange(-1.0,  1.0);
    		}
        	initialized = true;
    	}
    }
    
    protected void disableEncoders() {
    	initializeEncoders();
		for(PIDController pid : encoders) {
			pid.disable();
		}
    }
    protected void enableEncoders() {
    	initializeEncoders();
		for(PIDController pid : encoders) {
			pid.enable();
		}
    }
    public boolean allEncodersOnTarget() {
    	initializeEncoders();
		for(PIDController pid : encoders) 
			if(!pid.onTarget()) 
				return false;
		return true;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    	// Enable all encoders
    	enableEncoders();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
		return allEncodersOnTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	// Disable all encoders
    	disableEncoders();
    }
}
