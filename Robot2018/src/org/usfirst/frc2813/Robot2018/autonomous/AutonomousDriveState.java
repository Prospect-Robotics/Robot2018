package org.usfirst.frc2813.Robot2018.autonomous;

/**
 * This is a state object for Autonomous drive commands.
 * It contains information about how the drive system is to be controlled, 
 * such as min and max throttle and acceleration ramps.
 * It also contains information about the speed, position and orientation of the robot.
 */
public class AutonomousDriveState {
	/*
	 * These defaults give us values that won't slip. Ramps to speed up and slow down and a min speed which we can
	 * safely transition to/from a dead stop at.
	 */
	private static final double DEFAULT_ACCELERATION_RAMP = 60; // inches to ramp from min speed to max speed
	private static final double DEFAULT_DECELERATION_RAMP = 60; // inches to ramp from min speed to max speed
	private static final double DEFAULT_MIN_THROTTLE = 0.2;
	private static final double DEFAULT_MAX_THROTTLE = 1.0;

	// FIXME! switch to Rate
	public double speed = 0;
	public double maxSpeed;

	public double angle = 0; // in field relative degrees
	public double accelRamp = DEFAULT_ACCELERATION_RAMP;
	public double decelRamp = DEFAULT_DECELERATION_RAMP;
	public double minThrottle = DEFAULT_MIN_THROTTLE;
	public double maxThrottle = DEFAULT_MAX_THROTTLE;
	
	/**
	 * construct a state object out of defaults. No accessors as everything is public for now. MaxSpeed is
	 * required as we do not have a reasonable default for that.
	 */
	public AutonomousDriveState(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
}
