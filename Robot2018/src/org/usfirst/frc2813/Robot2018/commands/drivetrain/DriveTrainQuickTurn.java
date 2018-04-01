package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.units.Direction;

/**
 * Autonomous turn command. Use gyro and linear interpolation
 * to
 */
public final class DriveTrainQuickTurn extends SubsystemCommand<DriveTrain> {
	private final Direction direction;
	private final double relativeAngleInDegrees;
	private final double rate;

	// These are calculated when we start
	private double startingAngle;
	private double targetAngle;
	
	private static final double LERP_START=60;
	private static final double LERP_STOP=25;
	private static final double LERP_END=0.25;
	private static final double MIN_DEG=0.5;

	/*
	 * Create a quick turn command for turning in a direction for a specific number of degrees.
	 */
	public DriveTrainQuickTurn(DriveTrain driveTrain, Direction direction, double relativeAngleInDegrees, double rate) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.direction = direction;
		this.rate = rate;
		this.relativeAngleInDegrees = relativeAngleInDegrees;
		if(relativeAngleInDegrees < 0) {
			throw new IllegalArgumentException("Do not specify reverse directions with negative angles.  Use direction instead.");
		}
		if(rate < 0) {
			throw new IllegalArgumentException("Do not specify rate with negative values.  Use direction instead.");
		}
		if(rate > 1.0) {
			throw new IllegalArgumentException("Do not specify rate greater than 100%.");
		}
		addArg("direction",direction);
		addArg("relativeAngleInDegrees",relativeAngleInDegrees);
		addArg("rate",rate);
		setName(toString());
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
		startingAngle = subsystem.getGyro().getAngle();
		targetAngle   = startingAngle + (direction.getMultiplierAsDouble() * relativeAngleInDegrees);  
	}

	/**
	 * @return the number of degrees we have turned so far, relative to our target direction
	 * NOTE: If we are turning in the wrong way, this will become a larger magnitude in either positive or negative values until we do a (360-target) - i.e. turn in the wrong direction eventually gets the same result.
	 */
	private double getErrorInDegrees() {
		return targetAngle - subsystem.getGyro().getAngle();
	}

	/**
	 * @return offset in degrees from the target angle as an absolute value (how severe is the error)
	 */
	private double getErrorMagnitudeInDegrees() {
		return Math.abs(getErrorInDegrees());
	}

	/**
	 * Calculate throttle given degrees from target, based on the error magnitude,
	 * which logically cannot be less than 0 or greather than 360, or else things have gone sideways indeed.
	 * @param errorMagnitudeInDegrees The magnitude of the error in degrees from the goal
	 * @param throttle the rate 0.0-1.0 for percentage of maximum speed
	 * @return The correct throttle to use based on a ramp up/down interpolated from the calculations we made for the turn profile
	 */
	private static double calcThrottle(double errorMagnitudeInDegrees, double throttle) {
//		if (errorMagnitudeInDegrees < MIN_DEG) {//if at correct location, stop
//			return 0;
//		}
		if (errorMagnitudeInDegrees <= LERP_STOP) {//if through lerp period, min speed
			return LERP_END;
		}
		if (errorMagnitudeInDegrees >= LERP_START) {//if not at lerp period, given speed
			return throttle;
		}
//		return (errorMagnitudeInDegrees - LERP_STOP) * (throttle - LERP_END) / (LERP_START-LERP_STOP) + LERP_END;//deceleration/linear interpolation code
		return throttle;
	}

	// Called repeatedly when this Command is scheduled to run
	protected void ghscExecute() {
		int directionMultiplier = getErrorInDegrees() < 0 ? -1 : 1;
		subsystem.arcadeDrive(0, calcThrottle(getErrorMagnitudeInDegrees(), rate) * directionMultiplier);
	}

	// Make this return true when this Command no longer needs to run execute()
	@Override
	protected boolean ghscIsFinished() {
		return getErrorMagnitudeInDegrees() <= MIN_DEG;
	}
    
	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}
}
