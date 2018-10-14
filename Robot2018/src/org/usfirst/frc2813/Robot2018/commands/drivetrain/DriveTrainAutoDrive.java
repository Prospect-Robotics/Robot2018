package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.autonomous.AutonomousDriveState;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.util.GeometryHelpers;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class to use PID control of autonomous drive.
 * We use 2 soft PID controllers. One for angle and one for velocity.
 * Curved paths are supported by calculating our expected end angle, interpolating and using PID to correct angle.
 * we subtract expected angular offset for curves from current angle before sending to PID.
 * NOTE that we must over-correct with PID as the average error angle seen by PID must be close to 0 if we are to stay on a curve.
 */
public class DriveTrainAutoDrive extends SubsystemCommand<DriveTrain> {
	private final boolean disableSpeedPID = true;    // FIXME!! put here only until speed PID is fully tested
    private final PIDSource pidSourceSpeed = new PIDSource() {

        @Override
        public void setPIDSourceType(PIDSourceType pidSource) {
        }

        @Override
        public PIDSourceType getPIDSourceType() {
            return PIDSourceType.kDisplacement;
        }

        /**
         * This is the main speed PID reader routine. It returns the difference between actual and expected velocity.
         */
        @Override
        public double pidGet() {
        		return measureVelocityDrift();
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
        		return measureAngularDrift();
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
	 * This is the max allowable speed of our robot. It is expressed in units/units   FIXME!! which units?
	 * We use this to map speed onto [0..1]. This is essential so that we
	 *   a) do not needlessly limit our speed when we want to go fast, yet
	 *   b) can have meaningful speed ramps from and to stop, and
	 *   c) can meaningfully limit our speed when needed, such as when the elevator is raised.
	 */
	private static final double MAX_ACTUAL_SPEED = 40;  // FIXME! need to find out time rate from our timer call before we can even guess

	private final AutonomousDriveState state;
	private final Direction direction;
	private final double distance;
	private double startSpeed, endSpeed, maxSpeed;
	private double startPosition;
	private double startTime;
	private double accelRamp, decelRamp;
	private double startAngle;
	private double deltaAngle = 0; // for the turn version
	private double lastThrottle = 0; // Last throttle we sent to the drive train
	private boolean distanceReached = false; // Have we traveled far enough
	// stop when we have gone far enough AND our angular error is less than 0.5 degrees
	private static final double endAngleErrorThreshold = 0.5;
	private double lastPosition, lastTime; // calculate real speed between calls
	private double measuredSpeed = 0;
	private double angularDrift; // input to angle PID
	private double velocityDrift; // input to speed PID
	private int cyclesWithoutProgress = 0; // monitor position progress and give up if we stop moving
	/**
	 * Our interrupt latency is around 45ms. Set our stuck threshold to 5 inches per second at full speed.
	 */
	private static final double progressDistanceThreshold = 0.045*5;
//	private static final double progressCycleThreshold = 6;
	private static final double progressCycleThreshold = 60;		// TODO:  Review:  allow to stall longer in case we get stuck on carpet, but do stop eventually in case we are hitting the switch
	private double lastDistanceTravelled;
	
	/**
	 * We combine 2 separate PIDs into one routine to control drive - velocity and angle.
	 * Velocity is less sensitive, so we save it's PID output and use both on the angle PID callback.
	 */
	private double pidSpeedAdjust;

	/**
	 * Use PID to drive in a straight line for a given distance.
	 * Adjust our start/stop ramps and start power so that we effectively start and stop at
	 * the given ratio of the normal speed. So, if you want to end at 20% speed, set
	 * endingSpeedFactor to 0.2. Then your next command should set startSpeedFactor to 0.2
	 * @param driveTrain - the subsystem
	 * @param direction - forward or backward?
	 * @param distance - how far to travel
	 * @param endSpeed - speed we should be at by the end of this operation
	 */
	public DriveTrainAutoDrive(DriveTrain driveTrain, AutonomousDriveState state, Direction direction, double distance, double endSpeed) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.state = state;
		this.maxSpeed = state.shared.maxSpeed;
		this.direction = direction;
		this.distance = distance;
		this.startSpeed = state.shared.speed;
		this.endSpeed = endSpeed;
		this.accelRamp = state.accelRamp * (1 - startSpeed);
		this.decelRamp = state.decelRamp * (1 - endSpeed);
		this.startAngle = state.shared.angle;
		state.shared.speed = endSpeed; // update state to what it will be

		addArg("state",state);
		addArg("direction",direction);
		addArg("distance",distance);
		addArg("endSpeed",endSpeed);
		setName(toString());
	}

	/**
	 * Use PID to drive on a circular path given a radius and number of degrees.
	 * Adjust our start/stop ramps and start power so that we effectively start and stop at
	 * the given ratio of the normal speed. So, if you want to end at 20% speed, set
	 * endingSpeedFactor to 0.2. Then your next command should set startSpeedFactor to 0.2
	 * @param driveTrain - the subsystem
	 * @param direction - forward or backward?
	 * @param angle - degrees of arc segment to traverse
	 * @param radius - radius of arc in inches
	 * @param rotation - clockwise or counter clockwise
	 * @param endSpeed - speed we should be at by the end of this operation
	 */
	public DriveTrainAutoDrive(DriveTrain driveTrain, AutonomousDriveState state, Direction direction, double angle, double radius, Direction rotation, double endSpeed) {
		this(driveTrain, state, direction, GeometryHelpers.computeArcLength(angle, radius), endSpeed);
		deltaAngle = GeometryHelpers.computeRelativeAngle(angle, direction, rotation);
		state.shared.angle += deltaAngle;

		addArg("angle",angle);
		addArg("radius",radius);
		addArg("rotation",rotation);
		setName(toString());
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
		startPosition = lastPosition = readPosition();
		startTime = lastTime = readTime();
		actionFormatted("initialize", "PID AutoDrive initialize",
				"End state", state,
				"startTime", startTime,
				"startPosition", startPosition,
				"Direction", direction,
				"startSpeed", startSpeed,
				"startAngle", startAngle,
				"deltaAngle", deltaAngle,
				"accelRamp", accelRamp,
				"decelRamp", decelRamp,
				"endSpeed", endSpeed);
		pidAngleController = new PIDController(0.07, 0.01, 0.15, pidSourceAngle, this::pidAngleOutputFunc);
		pidAngleController.setInputRange(-360, 360);
		pidAngleController.setContinuous();
		pidAngleController.setOutputRange(-1.0, 1.0);
		pidAngleController.enable();

		pidSpeedController = new PIDController(0.07, 0.01, 0.15, pidSourceSpeed, this::pidSpeedOutputFunc);
		pidSpeedController.setInputRange(-1.0, 1.0);
		pidSpeedController.setContinuous();
		pidSpeedController.setOutputRange(-1.0, 1.0);
		pidSpeedController.enable();
	}

	/** measure our error from the curve we are walking. This is the angle PID callback */
    public double measureAngularDrift() {
        double angleRightNow = subsystem.getGyro().getAngle();
        if (deltaAngle == 0) {
        		angularDrift = angleRightNow - startAngle;
        }
        else {
        		angularDrift = angleRightNow - calcAngle(distanceTravelled());
        }
        return angularDrift;
    }

	/** read system time stamp */
	private double readTime() {
		return Timer.getFPGATimestamp();
	}

	/** read drive system position value */
	private double readPosition() {
		return subsystem.getDistance();
	}

	/** Calculate delta time and update time stamp. */
	private double getDeltaTime() {
		double now = readTime();
		double deltaTime = now - lastTime;
		lastTime = now;
		return deltaTime;
	}
	/** Calculate delta position and update position value. */
	private double getDeltaPosition() {
		double here = readPosition();
		double deltaPosition = (here - lastPosition) * direction.getMultiplierAsDouble();
		lastPosition = here;
		return deltaPosition;
	}

	/** Measure the speed since it was last read. */
	private double measureActualSpeed() {
		double deltaTime = getDeltaTime();
		double deltaPosition = getDeltaPosition();
		Logger.printFormat(LogType.DEBUG, "XYZZYA>  time: %s, position: %s, timeMillis %s", deltaTime, deltaPosition, System.currentTimeMillis());
		if (deltaTime == 0) return measuredSpeed; // guard against divide by zero.
		return (deltaPosition / deltaTime) / MAX_ACTUAL_SPEED;
	}

	/** Measure our error from the desired speed. This is the speed PID callback. */
	private double measureVelocityDrift() {
		velocityDrift = calcSpeed(distanceTravelled()) - measureActualSpeed();
		return velocityDrift;
	}

	private double distanceTravelled() {
		return (subsystem.getDistance() - startPosition) * direction.getMultiplierAsDouble();
	}

	/** Convert speed (0..1) to signed throttle */
	private double speedToThrottle(double speed) {
		return (state.minThrottle + (state.maxThrottle - state.minThrottle) * speed) * direction.getMultiplier();
	}

	/**
	 * Get the desired speed based on where we are in our journey. Do not exceed ramps.
	 * @param distanceTravelled How far has the robot moved in inches.
	 * @return speed in [0, 1] range that we want to be going at this point in our curve.
	 */
	private double calcSpeed(double distanceTravelled) {
		double distanceRemaining = distance - distanceTravelled;
		double startLimit, endLimit;

		if (distanceTravelled < 0) {
			return startSpeed;
		}
		
		if (distanceTravelled < accelRamp) {
			startLimit = GeometryHelpers.interpolate(0, startSpeed, accelRamp, maxSpeed, distanceTravelled);
		}
		else {
			startLimit = maxSpeed;
		}

		if (distanceRemaining < 0) {
			endLimit = endSpeed;
		}
		else if (distanceRemaining < decelRamp) {
			endLimit = GeometryHelpers.interpolate(distance - decelRamp, maxSpeed, distance, endSpeed, distanceTravelled);
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
		Logger.printLabelled(LogType.DEBUG, "XYZZY calcAngle", "distSoFar", distanceTravelled, "ttlDist", distance, "startAngle", startAngle, "deltaAngle", deltaAngle, "interp", GeometryHelpers.interpolate(0, startAngle, distance, deltaAngle, distanceTravelled));
		if (distanceTravelled < 0) {
			return 0;
		}
		if (distanceTravelled > distance) {
			return deltaAngle;
		}
		return GeometryHelpers.interpolate(0, startAngle, distance, deltaAngle, distanceTravelled);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean ghscIsFinished() {
		/*
		 * Keep track of whether we have finished, and ignore any PID outputs on a best effort basis.
		 * It's highly probable that assigning to boolean distanceReached will be atomic, though it's not
		 * guaranteed.  I didn't think it was worth adding a mutex here and in the PID callback.
		 */
		//Logger.info(distanceTravelled() + "/" + distance);
		if (!distanceReached) {
			distanceReached = (distanceTravelled() >= distance);
		}
		if (distanceReached) {
			return true;// (XXX need to look further into this) !needAngleLock();
		}
		return false;
	}
	/**
	 * CRITICAL: Interrupted calls end, but end doesn't call interrupted!
	 * If you want to turn stuff off... do it in end.
	 * Called once after isFinished returns true or interrupted.
	 */
	@Override
	protected void ghscEnd() {
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
		Logger.info("Stopping... leaving lastThrottle=", lastThrottle);
		if (endSpeed == 0) {
			subsystem.arcadeDrive(0.0, 0.0);
		} else {
			subsystem.arcadeDrive(lastThrottle, 0.0);
		}
//		subsystem.arcadeDrive(lastThrottle, 0.0);
	}

    /**
     * This is the Speed controlling PID callback. It provides speed adjustment which we map into throttle adjustment.
     * For now we just save the value.
     * @param pidSpeedOutput This is the output of the PID computation based on the error in the relative speed of the robot
     */
    private void pidSpeedOutputFunc(double pidSpeedOutput) {
    		pidSpeedAdjust = disableSpeedPID ? 0 : pidSpeedOutput;
    }
    
    private boolean needAngleLock() {
    	if (endSpeed == 0 && subsystem.getGyro().getAngle() - deltaAngle > endAngleErrorThreshold) {
    		Logger.info("needAngleLock: "+ endSpeed + " " + subsystem.getGyro().getAngle() + " " + deltaAngle);
    		lastThrottle = 0;
    		return true;
    	}
    	return false;
	}

	/**
	 * This is the Angle controlling PID callback. It provides angle adjustment.
	 * @param pidAngleAdjust This is the output of the PID computation based on the error in the relative angle of the robot (the pid source)
	 */
	private void pidAngleOutputFunc(double pidAngleAdjust) {
		// If the command is distanceReached, ignore any extra PID output call backs while we are shutting PID down.
		if (distanceReached) {
			if (needAngleLock()) {
				subsystem.arcadeDrive(lastThrottle, pidAngleAdjust);
			}
			return;
		}

		Logger.printFormat(LogType.DEBUG, "XYZZYB>  timeMillis %s", System.currentTimeMillis());
		double distanceTravelled = distanceTravelled();
		double desiredSpeed = calcSpeed(distanceTravelled);
		
		/*
		 * Compare the distance since the last call to a threshold scaled by expected speed to ensure
		 * we aren't stuck. This happens if we drifted and are actually pushing against something. In
		 * this case we mark ourselves distanceReached. This effectively resets our position as there is no
		 * record of our leaving early and autonomous tracks as if we traveled the full distance.
		 */
		if ((distanceTravelled - lastDistanceTravelled) < progressDistanceThreshold * desiredSpeed) {
			if (++cyclesWithoutProgress > progressCycleThreshold) {
				Logger.error("STALL DETECTED\n");
//				distanceReached = true;		// TODO:  FLAG:  Is this right?  I think this was taken out because the bot gets stuck on carpet
											// I think we still need it for when we hit the switch.  Putting back in, but upping progressCycleThreshold
				distanceReached = true;
			}
		}
		else {
			cyclesWithoutProgress = 0;
		}
		lastDistanceTravelled = distanceTravelled;
		double newThrottle = speedToThrottle(desiredSpeed + pidSpeedAdjust);

		// WARNING! This will flood logs!
		Logger.printLabelled(LogType.DEBUG, "PID linear stepping",
				"TargetDistance", distance,
				"distance traveled", distanceTravelled,
				"Angular drift", angularDrift,
				"Velocity drift", velocityDrift,
				"Desired speed", desiredSpeed,
				"Throttle", newThrottle,
				"PID Angle Adjust", pidAngleAdjust,
				"PID Speed Adjust", pidSpeedAdjust);

		subsystem.arcadeDrive(newThrottle, pidAngleAdjust);
		lastThrottle = newThrottle; // NB: When we stop, we'll go back to this throttle + no angle
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}
}
