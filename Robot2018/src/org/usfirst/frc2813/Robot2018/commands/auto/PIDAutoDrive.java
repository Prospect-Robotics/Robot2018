package org.usfirst.frc2813.Robot2018.commands.auto;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * Class to use PID control of autonomous drive. Note that all units are inches
 */
public class PIDAutoDrive extends GearheadsCommand {
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
	private final PIDController controller = new PIDController(.05, 0.00, 0, m_source, this::usePIDOutput);

	/**
	 * These defaults give us values that won't slip. Ramps to speed up and slow down and a min speed which we can
	 * safely transition to/from a dead stop at.
	 */
	private static final double ACCELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double DECELERATION_RAMP = 60;// 60 inches to ramp from min speed to max speed
	private static final double MIN_SPEED = 0.2;  // speed to go if at a dead stop. The fastest we can go without spinning out
	private static final double MAX_SPEED = 1.0;

	private final Direction direction;
	private final double distance;
	private double startSpeed, endSpeed, maxSpeed;
	private double startPosition;
	private double accelRamp, decelRamp;
	private double startAngle; // which may or may not be zero degrees.
	
	public PIDAutoDrive(double speed, Direction direction, double distance) {
		requires(Robot.driveTrain);
		controller.setInputRange(0, 256);
		controller.setContinuous();
		controller.setOutputRange(-1, 1);
		startSpeed = endSpeed = MIN_SPEED;
		maxSpeed=speed;
		this.direction = direction;
		this.distance = distance;
		accelRamp = ACCELERATION_RAMP;  // here we assume max ramp - ie: start and end at dead stop
		decelRamp = DECELERATION_RAMP;
	}
	
	/**
	 * Adjust our start/stop ramps and start power so that we effectively start and stop at
	 * the given ratio of the normal speed. So, if you want to end at 20% speed, set
	 * endingSpeedFactor to 0.2. Then your next command should set startSpeedFactor to 0.2
	 * @param speed - the peak speed. We ramp up to this
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 * @param startSpeedFactor - how fast are we going? 0..1
	 * @param endSpeedFactor - how fast should we be going when we're done? 0..1
	 */
	public PIDAutoDrive(double speed, Direction direction, double distance, double startSpeedFactor, double endSpeedFactor) {
		this(speed, direction, distance);
		startSpeed += MAX_SPEED * (1 - startSpeedFactor);
		accelRamp *= 1 - startSpeedFactor;

		endSpeed += MAX_SPEED * (1 - endSpeedFactor);
		decelRamp *= 1 - endSpeedFactor;
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
		startPosition = Robot.driveTrain.getDistance();
		startAngle = Robot.gyro.getAngle();
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

	private double distanceTraveled() {
		double rawDelta = Robot.driveTrain.getDistance() - startPosition;
		return direction.isPositive() ? rawDelta : -rawDelta;
	}
	
	/**
	 * Get the desired throttle based on where we are in our journey.
	 * Start by handling edge cases.
	 * Then interpolate in the last bit
	 * Then the first bit.
	 * All else is full speed ahead!
	 *
	 * @param distanceTravelled
	 * @return desired speed between MIN_SPEED and maxSpeed
	 */
	private double calcThrottle(double distanceTravelled) {
		double inchesRemaining = distance - distanceTravelled;
		double throttle = maxSpeed;

		if (inchesRemaining < 0) {
			return endSpeed;
		}
		if (inchesRemaining < decelRamp) {
			throttle = interpolate(distance - decelRamp, maxSpeed, distance, endSpeed, distanceTravelled);
		}
		if (distanceTravelled < 0) {
			return Math.min(throttle, startSpeed);
		}
		if (distanceTravelled < accelRamp) {
			return Math.min(throttle, interpolate(0, startSpeed, accelRamp, maxSpeed, distanceTravelled));
		}
		return maxSpeed;
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		//new PrintOutEncoderValues(60,Robot.driveTrain.encoderPort,Robot.driveTrain.encoderStarboard);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return distanceTraveled() >= distance;
	}

	// Called once after isFinished returns true
	protected void end() {
		Logger.info("Stopping...");
		controller.disable();
		controller.reset();
	}

	private void usePIDOutput(double output) {
		/* set their output to zero when disabled.
		 * The watchdog will call disable() every 0.1 seconds when
		 * the command isn't r
		 * PID controllers running.  Every time the output is written
		 * we cause the robot to move.  If the watchdog isn't alive,
		 * don't accept input.
		 */
		double newThrottle = calcThrottle(distanceTraveled());
		if (direction.isNegative()) {
			newThrottle *= -1;
		}
		Logger.printLabelled(LogType.INFO, "PID stepping",
				"distance travelled", distanceTraveled(),
				"New Throttle", newThrottle,
				"Output", output);

		Robot.driveTrain.arcadeDrive(newThrottle, -output);
	}
}
