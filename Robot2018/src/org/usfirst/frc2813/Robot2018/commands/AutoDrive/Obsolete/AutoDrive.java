package org.usfirst.frc2813.Robot2018.commands.AutoDrive.Obsolete;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Command;

/**
 * ***Replaced By PIDAutoDrive***
 * OBSOLETE
 */
public class AutoDrive extends Command {
	@SuppressWarnings("unused")
	private double m_forwardSpeed;
	private double m_distance;
	private Encoder encoder1;
	private Encoder encoder2;
	private double maxSpeed;
	private double minDist;
	private double distanceLeft;
	private double lerpStart;
	private double lerpEnd;
	private double lerpStop;
	private double accelStartDistance;
	private double accelStopDistance;
	private double accelStartValue;
	private double accelStopValue;
	private double minSpeed;
    public AutoDrive(double forwardSpeed, double minSpeed, double distance) {
    	requires(Robot.driveTrain);
    	m_forwardSpeed = forwardSpeed;
    	m_distance = distance;
    	maxSpeed = Math.abs(forwardSpeed);
    	minSpeed = Math.abs(minSpeed);
    	encoder1 = Robot.driveTrain.encoderStarboard;
    	encoder2 = Robot.driveTrain.encoderPort;
    	lerpStart = m_distance/4;
    	lerpEnd=.2;
    	lerpStop = .02;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//encoder1.reset();
    	//encoder2.reset();
    	distanceLeft=m_distance;
    }
    private double distanceTraveled() {
    	return (Math.abs(encoder1.getDistance()*RobotMap.WHEEL_CIRCUMFERENCE)+Math.abs(encoder2.getDistance()*RobotMap.WHEEL_CIRCUMFERENCE))/2;
    }
    private double calcThrottleSteadyState() {
    	return maxSpeed;
    }
    private double calcThrottleDecelerate(double d) {//set throttle given distance from target
    	if (d<minDist) {
    		return 0;
    	}
    	if (d<=lerpStop) {
    		return lerpEnd;
    	}
    	if (d>=lerpStart) {
    		return maxSpeed;
    	}
    	return (d-lerpStop) * (maxSpeed - lerpEnd) / (lerpStart - lerpStop)+lerpEnd;
    }
    private double calcThrottleAccelerate(double d) {
    	if (distanceTraveled() < accelStopDistance) {
    		return distanceTraveled()*((accelStopValue-accelStartValue)/(accelStopDistance-accelStartDistance))+accelStartValue;
    	}
    	return maxSpeed;
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	distanceLeft=m_distance - distanceTraveled();
    	double potentialThrottle=Math.min(calcThrottleSteadyState(), calcThrottleAccelerate(distanceLeft));
    	double newThrottle=Math.min(potentialThrottle,calcThrottleDecelerate(distanceLeft));
    	System.out.println("EXECUTE"+newThrottle);
    	/*if (newThrottle != Math.abs(m_forwardSpeed)) {
    		m_forwardSpeed=-1*newThrottle;
    	}
    	System.out.println("EXECUTE"+newThrottle);
    	double newThrottle1=newThrottle;
    	if (Robot.gyro.getAngle() > 0 && distanceLeft<10) {
    		newThrottle1=newThrottle+.1;
    	}
    	else if (Robot.gyro.getAngle() < 0 && distanceLeft<10) {
    		newThrottle1=newThrottle-.1;
    	}*/
    	//Robot.driveTrain.tankAutoDrive(newThrottle1,newThrottle);
    	if (distanceLeft<=.25*m_distance) {
    		newThrottle=minSpeed;
    	}
    	Robot.driveTrain.tankAutoDrive(newThrottle,newThrottle);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return distanceTraveled() >=m_distance-36;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("END");
    	if (Robot.gyro.getAngle()>0) {
    		System.out.println("GETANGLE>0");
    		while(true) {
    			System.out.println(Robot.gyro.getAngle());
    			if (Robot.gyro.getAngle()<=0) {
    				System.out.println("GETANGLE>=0BREAK");
    				break;
    			}
    			Robot.driveTrain.speedControllerStarboard.set(.15);
    			//Robot.driveTrain.speedController1.set(.15);
    		}
    		Robot.driveTrain.speedControllerStarboard.set(0);
    		Robot.driveTrain.speedControllerPort.set(0);
    	}
    	else if (Robot.gyro.getAngle()<0) {
    		System.out.println("GETANGLE<0");
    		while(true) {
    			System.out.println(Robot.gyro.getAngle());
    			if (Robot.gyro.getAngle()>=0) {
    				System.out.println(Robot.gyro.getAngle());
    				System.out.println("GETANGLE>=0BREAK");
    				break;
    			}
    			Robot.driveTrain.speedControllerPort.set(-.15);
    			//Robot.driveTrain.speedController2.set(-.15);
    		}
    		Robot.driveTrain.speedControllerPort.set(0);
    		Robot.driveTrain.speedControllerStarboard.set(0);
    	}
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
