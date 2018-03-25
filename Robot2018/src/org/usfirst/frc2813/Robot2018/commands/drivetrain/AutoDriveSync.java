package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Class to use PID control of autonomous drive. Note that all units are inches
 */
public class AutoDriveSync extends AbstractDriveTrainCommand {
	private final PIDSource m_source = new PIDSource() {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		/**
		 * This is the main pid reader routine. It provides the current angle relative to expected.
		 * This is a value in degrees, centered around 0. Theoretically this could be shifted up by
		 * 180 degrees so it could be taken modulo 360 and then shifted back down. This would give
		 * us an absolute angle +- 180 degrees. However, if the PID is working at all, we never
		 * expect large numbers.
		 */
		@Override
		public double pidGet() {
			double angleTravelled = gyro.getAngle() - startAngle;
			if (onCurve) {
				// We subtract the angle we're expecting to be facing from the current angle, so that what is left is just the error, signed value  
				angleTravelled -= calcAngle(distanceTravelled());
			}
			return angleTravelled;
		}
	};
	// divide Ki and multiply Kd by 0.05 to emulate the behavior of a normal PIDController which uses a fixed 0.05 second period.
	/*
	 * NB: 3/22/2018 MT - Changed PID to P only (0.08).  
	 * NOTES: 
	 * 
	 * 1.  The PID is controlling a % of maximum output power for rotation, so we have to generate a proportional value that is
	 * much smaller than 1.0 but proportional to direction and magnitude of the error in our current gyroscope angle relative to 
	 * the gyroscope angle when we started.
	 * 
	 * 2.  The goal is to get something that is "just enough" to compensate for the worst case performance anomaly -
	 * in other words - we are dragging on the left right now so the output has to be just strong enough to counter that and no
	 * stronger.  
	 * 
	 * 3.  Without knowing how throttle % and angle % are combined, the current loading and battery level, and the maximum
	 * speed under those conditions - we really can't begin to calculate this value.  Fortunately, we just have to derived it 
	 * empirically and it was pretty easy.  I just increased by .01 until it was tracking to within approximately 
	 * +/- 1 degree through the arc.  
	 * 
	 * 4.  IF the robot gets much worse than it is now, this can be increased but you could always fix the damage. 
	 */
	
	private final PIDController controller = new PIDController(0.07, 0.01, 0.15, m_source, this::usePIDOutput);

	/**
	 * These defaults give us values that won't slip. Ramps to speed up and slow down and a min speed which we can
	 * safely transition to/from a dead stop at.
	 */
	private static final double ACCELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double DECELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double MIN_SPEED = 0.3;  // speed to go if at a dead stop. The fastest we can go without spinning out
	private static final double MAX_SPEED = 0.5;

	private final Gyro gyro;
	private final Direction direction;
	private final double distance;
	private double startSpeed, endSpeed, maxSpeed;
	private double startPosition;
	private static double rampScaleFactor = 1.0; // scale ramps to be less agressive
	private double accelRamp, decelRamp;
	private double startAngle; // which may or may not be zero degrees.
	private double deltaAngle; // for the turn version
	private boolean onCurve;
	private double lastThrottle = 0; // Last throttle we sent to the drive train
	private boolean complete = false; // Have we completed the operation

	/**
	 * Use PID to drive in a straight line for a given distance.
	 * @param driveTrain - the drivetrain subsystem
	 * @param speed - the peak speed. We ramp up to this
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 */
	public AutoDriveSync(DriveTrain driveTrain, double speed, Direction direction, double distance) {
		super(driveTrain, true);
		this.gyro = driveTrain.getGyro();
		controller.setInputRange(-360, 360);
		controller.setContinuous();
		controller.setOutputRange(-1.0, 1.0);
		startSpeed = endSpeed = MIN_SPEED;
		maxSpeed=speed;
		this.direction = direction;
		this.distance = distance;
		accelRamp = ACCELERATION_RAMP * rampScaleFactor;  // here we assume max ramp - ie: start and end at dead stop
		decelRamp = DECELERATION_RAMP * rampScaleFactor;
		onCurve = false;
	}

	/**
	 * Use PID to drive in a straight line for a given distance.
	 * Adjust our start/stop ramps and start power so that we effectively start and stop at
	 * the given ratio of the normal speed. So, if you want to end at 20% speed, set
	 * endingSpeedFactor to 0.2. Then your next command should set startSpeedFactor to 0.2
	 * @param driveTrain - the drivetrain subsystem
	 * @param speed - the peak speed. We ramp up to this
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 * @param startSpeedFactor - how fast are we going? 0..1
	 * @param endSpeedFactor - how fast should we be going when we're done? 0..1
	 */
	public AutoDriveSync(DriveTrain driveTrain, double speed, Direction direction, double distance, double startSpeedFactor, double endSpeedFactor) {
		this(driveTrain, speed, direction, distance);
		startSpeed += (MAX_SPEED - MIN_SPEED) * startSpeedFactor;
		accelRamp *= 1 - startSpeedFactor;

		endSpeed += (MAX_SPEED - MIN_SPEED) * endSpeedFactor;
		decelRamp *= 1 - endSpeedFactor;
	}

	/**
	 * Use PID to drive along a circular path for a given distance.
	 * @param driveTrain - the drivetrain subsystem
	 * @param speed - the peak speed. We ramp up to this
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 * @param startSpeedFactor - how fast are we going? 0..1
	 * @param endSpeedFactor - how fast should we be going when we're done? 0..1
	 * @param radius - the radius of the circle we are traveling
	 * @param clockwise - the direction of the circle
	 */
	public AutoDriveSync(DriveTrain driveTrain, double speed, Direction direction, double distance, double startSpeedFactor,
			double endSpeedFactor, double radius, boolean clockwise) {
		this(driveTrain, speed, direction, distance, startSpeedFactor, endSpeedFactor);
		onCurve = true;
		deltaAngle = (distance * 180.0) / (radius * Math.PI); // just an offset for now
		if (!clockwise) {
			deltaAngle *= -1.0;
		}
		if (direction.isNegative()) {
			deltaAngle *= -1.0;
		}
	}

	public static void scaleRamps(double scaleFactor) {
		if (scaleFactor > 0 && scaleFactor <= 1.0) {
			rampScaleFactor = scaleFactor;
		}
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		Logger.printLabelled(LogType.INFO, "PID AutoDrive state",
				"startSpeed", startSpeed,
				"accelRamp", accelRamp,
				"endSpeed", endSpeed,
				"decelRamp", decelRamp);
		// Encoder 2 spins the opposite direction of Encoder 1.  Encoder 1 has a positive sense, Encoder 2 will therefore have a negative sense.
		// In order to add the two values correctly, you should add Encoder 1 to the negative of Encoder 2, or "Encoder 1 - Encoder 2"
		// This will, counter intuitively, add the two values, NOT take the difference between the two values
		/*
		 * A note on Encoders and the sign of distance:
		 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
		 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
		 */
		startPosition = driveTrain.getDistance();
		startAngle = gyro.getAngle();
		Logger.printLabelled(LogType.INFO, "PID AutoDrive start", "position", startPosition, "angle", startAngle);
		controller.enable();
	}

	/**
	 * Interpolate: Given two points and the x value of third point, determines
	 * y value of that third point.
	 *
	 * x is distance and y is speed when used in calcThrottle
	 *
	 * @param x1 x value of first point
	 * @param y1 y value of first point
	 * @param x2 x value of second point
	 * @param y2 y value of second point
	 * @param x x value of third point
	 * @return y value of third point
	 */

	private static double interpolate(double x1, double y1, double x2, double y2, double x) {
		return y1 + (y2 - y1) / (x2 - x1) * (x - x1);
	}

	private double distanceTravelled() {
		double rawDelta = driveTrain.getDistance() - startPosition;
		return rawDelta * direction.getMultiplierAsDouble();
	}
	
	/**
	 * Get the desired throttle based on where we are in our journey.
	 * Start by handling edge cases.
	 * Then interpolate in the last bit
	 * Then the first bit.
	 * All else is full speed ahead!
	 * @param distanceTravelled How far has the robot moved.  driveTrain.getDistance() suggests this is in feet.
	 * @return desired speed between MIN_SPEED and maxSpeed
	 */
	private double calcThrottle(double distanceTravelled) {
		double distanceRemaining = distance - distanceTravelled;
		double startLimit, endLimit;

		// shouldn't happen. Were we pushed? Did we roll?
		if (distanceTravelled < 0) {
			return startSpeed;
		}
		
		// never exceed the start ramp
		if (distanceTravelled < accelRamp) {
			startLimit = interpolate(0, startSpeed, accelRamp, maxSpeed, distanceTravelled);
		}
		else {
			startLimit = maxSpeed;
		}

		// and never exceed the stop ramp
		if (distanceRemaining < 0) {
			endLimit = endSpeed;
		}
		else if (distanceRemaining < decelRamp) {
			endLimit = interpolate(distance - decelRamp, maxSpeed, distance, endSpeed, distanceTravelled);
		}
		else {
			endLimit = maxSpeed;
		}

		return Math.min(startLimit, endLimit);
	}

	/**
	 * interpolate our desired angle offset by interpolating 0..deltaAngle
	 * @param distanceTravelled How far has the robot moved.  driveTrain.getDistance() suggests this is in feet.
	 * @return desired offset from startAngle
	 */
	private double calcAngle(double distanceTravelled) {
		if (distanceTravelled < 0) {
			return 0;
		}
		if (distanceTravelled > distance) {
			return deltaAngle;
		}
		return interpolate(0, 0, distance, deltaAngle, distanceTravelled);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		//new PrintOutEncoderValues(60,driveTrain.encoderPort,driveTrain.encoderStarboard);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		/* 
		 * Keep track of whether we have finished, and ignore any PID outputs on a best effort basis.
		 * It's highly probable that assigning to boolean complete will be atomic, though it's not 
		 * guaranteed.  I didn't think it was worth adding a mutex here and in the PID callback. 
		 */
		return complete = (distanceTravelled() >= distance);
	}

	// Called once after isFinished returns true
	protected void end() {
		controller.disable();
		controller.reset();
		/*
		 * When we end, we are going to push the same throttle value that we calculated for the "end speed"
		 * and ramped down to, but we're going to disable the turn.  The last PID output we sent to the 
		 * controller before we reached our goal for curves will almost certainly have a non-zero rotation rate
		 * and would have one if we were correcting for an error when driving straight.
		 */
		Logger.info("Stopping... leaving lastThrottle=" + lastThrottle);
		driveTrain.arcadeDrive(lastThrottle, 0.0);
	}

	/**
	 * This is the pid callback. It provides out angle adjustment.
	 * @param pidOutput This is the output of the PID computation based on the error in the relative angle of the robot (the pid source) 
	 */
	private void usePIDOutput(double pidOutput) {
		// If the command is complete, ignore any extra PID output callbacks while we are shutting PID down.
		if(complete) {
			return;
		}
		double distanceTravelled = distanceTravelled();
		double newThrottle = calcThrottle(distanceTravelled) * direction.getMultiplier();

		/*
		 * 3/22/2018 MT - 
		 * Added reporting of the error in the angle, so you can see how well PID is controlling angular 
		 * drift from the angle at the start of the command.  Within +/- 1 degree seems to be adequate.
		 */
		Logger.printLabelled(LogType.INFO, "PID linear stepping",
				"TargetDistance", distance * direction.getMultiplier(),
				"distance travelled", distanceTravelled,
				"AngleError[now]", gyro.getAngle() - startAngle,
				"Throttle", newThrottle,
				"PID Output", pidOutput);
		driveTrain.arcadeDrive(newThrottle, pidOutput);
		lastThrottle = newThrottle; // NB: When we stop, we'll go back to this throttle + no angle
	}
}
