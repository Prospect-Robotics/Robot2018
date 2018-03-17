package org.usfirst.frc2813.Robot2018.commands.auto;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 *
 */
public class AutoCurveDrive extends GearheadsCommand {
	private final double degrees, rate;
	private double startingOrientation;
	@SuppressWarnings("unused")
	private static final double LERP_START=60;
	@SuppressWarnings("unused")
	private static final double LERP_STOP=40;
	@SuppressWarnings("unused")
	private static final double LERP_END=0.2;
	@SuppressWarnings("unused")
	private static final double MIN_DEG=0.01;
	private final double turnRadius;
    public AutoCurveDrive(double rate, double degrees, double turnRadius) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	this.turnRadius=turnRadius;
    	this.degrees=degrees;
    	this.rate   =rate;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Robot.gyro.reset();
    	startingOrientation = Robot.gyro.getAngle();
    	//finalOrientation = Robot.gyro.getAngle() + degrees;
    }
    private double degreesRotated() {
    	return (Robot.gyro.getAngle() - startingOrientation);
    }
    private double calcThrottle() {//set throttle given degrees from target
    	//return -.3;
    	if (degrees < 0) {
    		return rate;
    	}
    	else {
    		return -rate;
    	}
    	/*if (deg<MIN_DEG) {//if at correct location, stop
    		return 0;
    	}
    	if (deg<=LERP_STOP) {//if through lerp period, min speed
    		return LERP_END;
    	}
    	if (deg>=LERP_START) {//if not at lerp period, given speed
    		return throttle;
    	}
    	return (deg-LERP_STOP)*(throttle-LERP_END)/(LERP_START-LERP_STOP)+LERP_END;//deceleration/linear interpolation code
  */  }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//double deg=degrees-degreesRotated();
    	//double newThrottleLeft=calcThrottle(deg, throttleLeft);
    	//double newThrottleRight = calcThrottle(deg, throttleRight);
    	
    	Robot.driveTrain.curvatureDrive(calcThrottle(), turnRadius);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	System.out.println("ISFINISHED");
    	System.out.printf("degrees %.3f ;gyro %.3f ;startingOrientation %.3f\n", degrees, Robot.gyro.getAngle(), startingOrientation);
        //return (degrees > 0 ? Robot.gyro.getAngle() >= startingOrientation + degrees : Robot.gyro.getAngle() <= startingOrientation - degrees);
        
        //boolean positiveIsFinished = Robot.gyro.getAngle() >= startingOrientation + degrees;
        
//        /boolean negativeIsFinished = Robot.gyro.getAngle() <= startingOrientation + degrees;
        
        return degrees > 0 ? degreesRotated() >= degrees : degreesRotated() <= degrees;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
