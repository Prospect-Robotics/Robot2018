package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.logging.Logger;

/**
 * NB: This class is here temporarily and will be replaced by DriveTrainAutoDrive (currently PIDAutoDrive)
 * @see org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoDrive
 */
public final class DriveTrainAutoCurveSync extends AbstractDriveTrainCommand {
	private final double degrees;
	private final double rate;
	private final double turnRadius;

	private double startingOrientation;
	
    public DriveTrainAutoCurveSync(DriveTrain driveTrain, double rate, double degrees, double turnRadius) {
    	super(driveTrain, true /* require driveTrain */);
    	this.turnRadius=turnRadius;
    	this.degrees=degrees;
    	this.rate   =rate;
		setName(toString());
    }
    
    protected void initialize() {
    	super.initialize();
    	this.startingOrientation = Robot.gyro.getAngle();
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
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	driveTrain.curvatureDrive(calcThrottle(), turnRadius);
    }
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	Logger.info(this + " Degrees=%.3f GyroAngle=%.3f startingOrientation=%.3f\n", degrees, driveTrain.getGyro().getAngle(), startingOrientation);
        return degrees > 0 ? degreesRotated() >= degrees : degreesRotated() <= degrees;
    }
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ", rate=" + rate + ", degrees=" + degrees + ", turnRadius=" + turnRadius + ")"; 
    }
}
