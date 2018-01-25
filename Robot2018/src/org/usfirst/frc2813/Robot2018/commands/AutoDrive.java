package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoDrive extends Command {
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
    public AutoDrive(double forwardSpeed, double distance) {
    	requires(Robot.driveTrain);
    	m_forwardSpeed = forwardSpeed;
    	m_distance = distance;
    	maxSpeed = Math.abs(forwardSpeed);
    	encoder1 = Robot.driveTrain.quadratureEncoder1;
    	encoder2 = Robot.driveTrain.quadratureEncoder2;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	encoder1.reset();
    	encoder2.reset();
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
    	if (newThrottle != Math.abs(m_forwardSpeed)) {
    		m_forwardSpeed=-1*newThrottle;
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return distanceTraveled() >=m_distance;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
