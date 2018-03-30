package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * Class to use PID control of autonomous drive.
 * We use 2 soft PID controllers. One for angle and one for velocity.
 * Curved paths are supported by calculating our expected end angle, interpolating
 * our angle and subtracting expected angular offset from current angle before sending to PID.
 */
public class DriveTrainAutoDrive extends SubsystemCommand<DriveTrain> {
	private final PIDSource pidSourceSpeed = new PIDSource() {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		/**
		 * This is the main speed PID reader routine. It provides the current speed relative to expected.
		 * This is a value shifted to be centered around 0.
		 */
		@Override
		public double pidGet() {
			return 3;
		}
	};
	private final PIDSource pidSourceAngle = new PIDSource() {

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		/**
		 * This is the main angle PID reader routine. It provides the current angle relative to expected.
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

	private PIDController pidAngleController = null;
	private PIDController pidSpeedController = null;

	/**
	 * These defaults give us values that won't slip. Ramps to speed up and slow down and a min speed which we can
	 * safely transition to/from a dead stop at.
	 */
	private static final double ACCELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double DECELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double MIN_THROTTLE = 0.3;
	private static final double MAX_THROTTLE = 1.0;
	private static final double brakeFactor = 0.8; // scale throttle down 20% if going too fast

	private final Gyro gyro;
	private final Direction direction;
	private final double distance;
	private double startSpeed, endSpeed, maxSpeed;
	private double startPosition;
	private double startTime;
	private static double rampScaleFactor = 1.0; // scale ramps to be less aggressive
	private double accelRamp, decelRamp;
	private double startAngle; // which may or may not be zero degrees.
	private double deltaAngle; // for the turn version
	private boolean onCurve;
	private double lastThrottle = 0; // Last throttle we sent to the drive train
	private boolean complete = false; // Have we completed the operation
	private double lastDistance, lastTime; // calculate real speed between calls

	/**
	 * Use PID to drive in a straight line for a given distance.
	 * @param driveTrain - the drivetrain subsystem
	 * @param speed - the peak speed. We ramp up to this
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 */
	public DriveTrainAutoDrive(DriveTrain driveTrain, double speed, Direction direction, double distance) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.gyro = driveTrain.getGyro();
		maxSpeed=speed;
		this.direction = direction;
		this.distance = distance;
		accelRamp = ACCELERATION_RAMP * rampScaleFactor;  // here we assume max ramp - ie: start and end at dead stop
		decelRamp = DECELERATION_RAMP * rampScaleFactor;
		onCurve = false;
		addArg("speed",speed);
		addArg("direction",direction);
		addArg("distance",distance);
		setName(toString());
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
	public DriveTrainAutoDrive(DriveTrain driveTrain, double speed, Direction direction, double distance, double startSpeedFactor, double endSpeedFactor) {
		this(driveTrain, speed, direction, distance);
		startSpeed = maxSpeed * startSpeedFactor;
		accelRamp *= 1 - startSpeedFactor;

		endSpeed = maxSpeed * endSpeedFactor;
		decelRamp *= 1 - endSpeedFactor;
		addArg("speed",speed);
		addArg("direction",direction);
		addArg("distance",distance);
		addArg("startSpeedFactor",startSpeedFactor);
		addArg("endSpeedFactor",endSpeedFactor);
		setName(toString());
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
	public DriveTrainAutoDrive(DriveTrain driveTrain, double speed, Direction direction, double distance, double startSpeedFactor, double endSpeedFactor, double radius, boolean clockwise) {
		this(driveTrain, speed, direction, distance, startSpeedFactor, endSpeedFactor);
		onCurve = true;
		deltaAngle = (distance * 180.0) / (radius * Math.PI); // just an offset for now
		if (!clockwise) {
			deltaAngle *= -1.0;
		}
		if (direction.isNegative()) {
			deltaAngle *= -1.0;
		}
		addArg("speed",speed);
		addArg("direction",direction);
		addArg("distance",distance);
		addArg("startSpeedFactor",startSpeedFactor);
		addArg("endSpeedFactor",endSpeedFactor);
		addArg("radius",radius);
		addArg("clockwise",clockwise);
		setName(toString());
	}

	public static void scaleRamps(double scaleFactor) {
		if (scaleFactor > 0 && scaleFactor <= 1.0) {
			rampScaleFactor = scaleFactor;
		}
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
		// Encoder 2 spins the opposite direction of Encoder 1.  Encoder 1 has a positive sense, Encoder 2 will therefore have a negative sense.
		// In order to add the two values correctly, you should add Encoder 1 to the negative of Encoder 2, or "Encoder 1 - Encoder 2"
		// This will, counter intuitively, add the two values, NOT take the difference between the two values
		/*
		 * A note on Encoders and the sign of distance:
		 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
		 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
		 */
		startPosition = lastDistance = subsystem.getDistance();
		startAngle = gyro.getAngle();
		startTime = lastTime = System.currentTimeMillis();
		Logger.printLabelled(LogType.INFO, "PID AutoDrive initialize",
				"startTime", startTime,
				"startPosition", startPosition,
				"startSpeed", startSpeed,
				"startAngle", startAngle,
				"accelRamp", accelRamp,
				"endSpeed", endSpeed,
				"decelRamp", decelRamp);

        pidAngleController = new PIDController(0.07, 0.01, 0.15, pidSourceAngle, this::pidAngleOutputFunc);
		pidAngleController.setInputRange(-360, 360);
		pidAngleController.setContinuous();
		pidAngleController.setOutputRange(-1.0, 1.0);
		pidAngleController.enable();
        pidSpeedController = new PIDController(0.07, 0.01, 0.15, pidSourceSpeed, this::pidSpeedOutputFunc);
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
		double rawDelta = subsystem.getDistance() - startPosition;
		return rawDelta * direction.getMultiplierAsDouble();
	}

	/** Convert speed (0..1) to positive throttle [MIN_THROTTLE,MAX_THROTTLE] */
	private double calcThrottle(double speed, double distanceTravelled) {
		double desiredThrottle = MIN_THROTTLE + (MAX_THROTTLE - MIN_THROTTLE) * speed;
// FIXME! Need to convert inches/millisecond to our speed in 0..1 and need to choose and define brakeFactor
//		double newDistance = distanceTravelled - lastDistance;
//		double newTime = System.currentTimeMillis() - lastTime;
//		double newSpeed = (newDistance / newTime);  // FIXME! convert to standard space: 0..1
//		if (newSpeed > speed) {
//			return desiredThrottle * brakeFactor;
//		}
		return desiredThrottle;
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
	private double calcSpeed(double distanceTravelled) {
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

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean ghscIsFinished() {
		/*
		 * Keep track of whether we have finished, and ignore any PID outputs on a best effort basis.
		 * It's highly probable that assigning to boolean complete will be atomic, though it's not
		 * guaranteed.  I didn't think it was worth adding a mutex here and in the PID callback.
		 */
		complete = (distanceTravelled() >= distance);
		return complete;
	}

	/**
	 * CRITICAL: Interrupted calls end, but end doesn't call interrupted!
	 * If you want to turn stuff off... do it in end.
	 * Called once after isFinished returns true or interrupted.
	 */
	@Override
	protected void ghscEnd() {
		// NB: reset() calls disable().  reset() does pidwrite to zero.
		if(pidAngleController != null) {
			pidAngleController.reset();
			pidAngleController.free();
			pidAngleController = null;
		}
		if(pidSpeedController != null) {
			pidSpeedController.reset();
			pidSpeedController.free();
			pidSpeedController = null;
		}
		/*
		 * When we end, we are going to push the same throttle value that we calculated for the "end speed"
		 * and ramped down to, but we're going to disable the turn.  The last PID output we sent to the
		 * pidAngleController before we reached our goal for curves will almost certainly have a non-zero rotation rate
		 * and would have one if we were correcting for an error when driving straight.
		 */
		Logger.info("Stopping... leaving lastThrottle=" + lastThrottle);
		subsystem.arcadeDrive(lastThrottle, 0.0);
	}

	/**
	 * This is the Angle controlling PID callback. It provides angle adjustment.
	 * @param pidSpeedOutput This is the output of the PID computation based on the error in the relative speed of the robot
	 */
	private void pidSpeedOutputFunc(double pidSpeedOutput) {
	}

	/**
	 * This is the Angle controlling PID callback. It provides angle adjustment.
	 * @param pidAngleOutput This is the output of the PID computation based on the error in the relative angle of the robot.
	 */
	private void pidAngleOutputFunc(double pidAngleOutput) {
		// If the command is complete, ignore any extra PID output callbacks while we are shutting PID down.
		if (complete) return;

		double distanceTravelled = distanceTravelled();
		double desiredSpeed = calcSpeed(distanceTravelled);
		double newThrottle = calcThrottle(desiredSpeed, distanceTravelled) * direction.getMultiplier();

		Logger.printLabelled(LogType.DEBUG, "PID linear stepping",
				"TargetDistance", distance,
				"distance travelled", distanceTravelled,
// uncomment next line to see our angular drift to see how PID is doing at keeping us on path
//				"AngleError[now]", gyro.getAngle() - startAngle - calcAngle(distanceTravelled()),
				"Desired speed", desiredSpeed,
				"Throttle", newThrottle,
				"PID Angle Output", pidAngleOutput);
		subsystem.arcadeDrive(newThrottle, pidAngleOutput);
		lastThrottle = newThrottle; // NB: When we stop, we'll go back to this throttle + no angle
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}
}
