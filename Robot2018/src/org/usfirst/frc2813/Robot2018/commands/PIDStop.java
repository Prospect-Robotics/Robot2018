package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDStop extends Command {
	
	public static final double Kp = 0, Ki = 0, Kd = 0; // TODO placeholder values
	
	static PIDController pid1, pid2;

    public PIDStop() {
        requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	if(pid1 == null) {
    		if(Robot.driveTrain.encoderPortFunctional && Robot.driveTrain.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderPort(), Robot.driveTrain.getSpeedControllerPort());
    			pid2 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderStarboard(), Robot.driveTrain.getSpeedControllerStarboard());
    		}
    		else if(Robot.driveTrain.encoderPortFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderPort(), PIDStop::driveBothWheels);
    		} else if(Robot.driveTrain.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, Robot.driveTrain.getEncoderStarboard(), PIDStop::driveBothWheels);
    		} else {
    			DriverStation.reportWarning("Can't PIDStop, BOTH ENCODERS OFFLINE", true);
    			return;
    		}
    	}
    	
    	pid1.enable();
    	if(pid2 != null) pid2.enable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Thread.yield(); // give the PIDControllers a chance to run (Not sure if this is necessary, or preferable to the alternative)
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if(pid1 == null)
    		// can't do anything with only one controller
    		return true;
        return pid1.onTarget() && pid2 == null ? true : pid2.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	if(pid1 != null) pid1.disable();
    	if(pid2 != null) pid2.disable();
    }
    
    private static void driveBothWheels(double speed) {
    	Robot.driveTrain.arcadeDrive(speed, 0);
    	
    }
}
