package org.usfirst.frc2813.Robot2018.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDStop extends Command {
	private boolean initializedEncoders = false;
	private final double Kp = 0.8, Ki = 0, Kd = 0;
	
	private PIDController pid1, pid2;
	private List<PIDController> encoders = new ArrayList<PIDController>(); 

    public PIDStop() {
        requires(Robot.driveTrain);
    }

    protected void initializeEncoders() {
    	if(!initializedEncoders) {
    		if(Robot.driveTrain.encoderPortFunctional && Robot.driveTrain.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderPort(), Robot.driveTrain.getSpeedControllerPort());
    			encoders.add(pid1);
    			pid2 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderStarboard(), Robot.driveTrain.getSpeedControllerStarboard());
    			encoders.add(pid2);
    		}
    		else if(Robot.driveTrain.encoderPortFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderPort(), PIDStop::driveBothWheels);
    			encoders.add(pid1);
    		} else if(Robot.driveTrain.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderStarboard(), PIDStop::driveBothWheels);
    			encoders.add(pid1);
    		} else {
    			DriverStation.reportWarning("Can't PIDStop, BOTH ENCODERS OFFLINE", true);
    		}
    		for(PIDController pid : encoders) {
    			pid.setAbsoluteTolerance(20); // 20 encoder ticks +/-, close enough for whatever encoder is used
    		}
    	}
    	initializedEncoders = true;
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
   
    private static void driveBothWheels(double speed) {
    	Robot.driveTrain.arcadeDrive(speed, 0);
    }
}
