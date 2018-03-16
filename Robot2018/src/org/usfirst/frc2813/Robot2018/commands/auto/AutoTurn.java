package org.usfirst.frc2813.Robot2018.commands.auto;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Autonomous turn command. Use gyro and linear interpolation
 * to
 */
public class AutoTurn extends GearheadsCommand {
	private final double degrees, rate;
	private double startingAngle;
	private boolean negativeAngle;
	private static final double LERP_START=60;
	private static final double LERP_STOP=40;
	private static final double LERP_END=0.2;
	private static final double MIN_DEG=0.01;

	public AutoTurn(double rate, double degrees) {
		requires(Robot.driveTrain);
		this.rate = rate;
		if (degrees < 0) {
			negativeAngle = true;
			this.degrees = -degrees;
		}
		else {
			negativeAngle = false;
			this.degrees = degrees;
		}
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		startingAngle = Robot.gyro.getAngle();
	}

	// returns number of degrees we have turned so far
	private double getAnglesProgressed() {
		if (negativeAngle)
			return startingAngle - Robot.gyro.getAngle();
		return Robot.gyro.getAngle() - startingAngle;
	}

	/**
	 * Calculate throttle given degrees from target
	 * @param deg
	 * @param throttle
	 * @return
	 */
	private static double calcThrottle(double deg, double throttle) {
		if (deg < MIN_DEG) {//if at correct location, stop
			return 0;
		}
		if (deg <= LERP_STOP) {//if through lerp period, min speed
			return LERP_END;
		}
		if (deg >= LERP_START) {//if not at lerp period, given speed
			return throttle;
		}
		return (deg - LERP_STOP) * (throttle - LERP_END) / (LERP_START-LERP_STOP) + LERP_END;//deceleration/linear interpolation code
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		Robot.driveTrain.arcadeDrive(0, calcThrottle(getAnglesProgressed(), rate));
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return getAnglesProgressed() >= degrees;
	}
}
