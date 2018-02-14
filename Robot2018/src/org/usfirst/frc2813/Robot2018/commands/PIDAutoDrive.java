package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.RandomPIDController2;
import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDAutoDrive extends Command {
	
	
	private final PIDSource m_source = new PIDSource() {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			return Robot.gyro.getAngle() - startAngle;
		}
	};
	// divide Ki and multiply Kd by 0.05 to emulate the behavior of a normal PIDController which uses a fixed 0.05 second period.
	private final RandomPIDController2 controller = new RandomPIDController2(.01, 0.00, 0, m_source, this::usePIDOutput);
	//private final PIDController controller = new PIDController(0.15, 0, 0.2);
	//private final WatchdogPIDInterface watchdog = new WatchdogPIDInterface(controller);
	private final double forwardSpeed;
	private final double distance;
	private double stopAt;
	private final double maxSpeed;
	private double startPosition;
	private double startAngle; // which may or may not be zero degrees.
	
	private static final double ACCELERATION = 10;//inches
	private static final double DECELERATION = 40;//inches
	
    public PIDAutoDrive(double forwardSpeed, double distance) {	// What are the units of distance?
        requires(Robot.driveTrain);
        controller.setInputRange(0, 360);
        controller.setContinuous();
        controller.setOutputRange(-1, 1);
   //     watchdog.setSafetyEnabled(true);
   //     watchdog.setExpiration(0.1);
        this.forwardSpeed=forwardSpeed;
        this.distance = distance;
        maxSpeed = Math.abs(forwardSpeed);
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
    	// Encoder 2 spins the opposite direction of Encoder 1.  Encoder 1 has a postive sense, Encoder 2 will therefore have a negative sense.
    	// In order to add the two values correctly, you should add Encoder 1 to the negative of Encoder 2, or "Encoder 1 - Encoder 2"
    	// This will, counter intuitively, add the two values, NOT take the difference between the two values
    	/*
    	 * A note on Encoders and the sign of distance:
    	 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
    	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
    	 */
    	startPosition = Robot.driveTrain.getDistance();
    	startAngle = Robot.gyro.getAngle();
    	controller.enable();
    	System.out.println("PID AutoDrive initilize: Started  stopAt:"+stopAt+" distance:"+distance);
    }
    private double distanceTraveled() {
    	return Robot.driveTrain.getDistance() - startPosition;
    }
    private double calcThrottleSteadyState() {
    	return maxSpeed;
    }
    private double calcThrottleDecelerate(double inchesTraveled) {
    	if (inchesTraveled > distance) {
    		return 0.1;
    	}
    	//Robot.driveTrain.setBrakeCoast(inchesTraveled < distance - DECELERATION) 
    	return 1 + (inchesTraveled - (distance - DECELERATION)) * (maxSpeed-1) / DECELERATION;
    	
    	
    }
    private double calcThrottleAccelerate(double inchesTraveled) {
    	//return inchesTraveled/ACCELERATION;
    	if (inchesTraveled < 0) {
    		return 0.2;
    	}
    	return .2 + (maxSpeed) * (ACCELERATION - inchesTraveled) / (ACCELERATION);
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
//    	watchdog.feed();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	/*
    	 * isFinished() for PIDAutoDrive
    	 * Want to stop when we have reached the desired Encoder position
    	 * The encoder position "stopAt" is recoreded 
    	 */
    	//  stopAt is an encoder position; it is signed 
    	// Encoder 2 spins the opposite direction of Encoder 1.  Encoder 1 has a postive sense, Encoder 2 will therefore have a negative sense.
    	// In order to add the two values correctly, you should add Encoder 1 to the negative of Encoder 2, or "Encoder 1 - Encoder 2"
    	// This will, counter intuitively, add the two values, NOT take the difference between the two values
    	//        double distanceRemaining = stopAt - ((Math.abs(Robot.driveTrain.quadratureEncoder1.getDistance())+Math.abs(Robot.driveTrain.quadratureEncoder2.getDistance()))/2);
        //double distanceRemaining = stopAt - Robot.driveTrain.getDistance();
        //if(distance < 0)
        //	distanceRemaining *= -1;
        //System.out.println("Distance remaining: "+distanceRemaining+",  stopAt:"+stopAt+", Encoder1:"+ Robot.driveTrain.quadratureEncoder1.getDistance());
        return distanceTraveled() >= distance;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("Stopping...");
    	controller.disable();
    	controller.reset();
    }
    
    private void usePIDOutput(double output) {
    	/*te their output to zero when disabled.
    	 * The watchdog will call disable() every 0.1 seconds when
    	 * the command isn't r
    	 * PID controllers wriunning.  Every time the output is written
    	 * we cause the robot to move.  If the watchdog isn't alive,
    	 * don't accept input.
    	 */
//    	if(!watchdog.isAlive()) return;
    	//System.out.println("Output updated to: "+output);//+", Time since last run: "+controller.getTimeDelta());
    	double rawDistance = Math.abs(distanceTraveled());
    	double steadyState=calcThrottleSteadyState();
    	double accel = calcThrottleAccelerate(rawDistance);
    	accel = 1;
    	double decel = calcThrottleDecelerate(rawDistance);
    	double potentialThrottle = Math.min(steadyState, decel);
    	double newThrottle = Math.min(potentialThrottle, accel);
    	//if (newThrottle > 0) {
    		//newThrottle=0;
    	//}
    	if (this.forwardSpeed <0) {
    		newThrottle *= -1;
    	}
    	System.out.printf("PID: t %.3f; a %.3f; ss %.3f; d %.3f;\nnT %.3f; fS %.3f; dist %.3f\n", distanceTraveled(), accel, steadyState, decel, newThrottle, this.forwardSpeed, distance);
    	//System.out.printf("PID: traveled %.3f; throttle %.3f\n", distanceTraveled(), newThrottle);
    	
    	
    	Robot.driveTrain.arcadeDrive(newThrottle, -output);
    }
}
