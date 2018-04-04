package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.util.Formatter;

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

	/**
	 * distance to safely accelerate from 0 to top speed
	 */
	public double accelRamp = DEFAULT_ACCELERATION_RAMP;
	/**
	 * distance to safely decelerate from top speed to 0
	 */
	public double decelRamp = DEFAULT_DECELERATION_RAMP;
	/**
	 * min throttle required to start moving
	 */
	public double minThrottle = DEFAULT_MIN_THROTTLE;
	/**
	 * max throttle allowable. This is normally 1 unless we need to restrict motor current.
	 */
	public double maxThrottle = DEFAULT_MAX_THROTTLE;
	
	/**
	 * nested class for state which is shared between autonomous actions. Read and write only
	 * in constructors! This is required since all operations are constructed in one pass and
	 * then run later.
	 */
	public class Shared {
		// FIXME! switch to Rate type
		/**
		 * current speed [0,1]
		 */
		public double speed = 0;
		/**
		 * top allowable speed [0,1]
		 */
		public double maxSpeed = 1;
		/**
		 * current angle in field relative degrees (ie: 0 is starting angle)
		 */
		public double angle = 0;
	}
	public Shared shared;
	
	public AutonomousDriveState() {
		shared = new Shared();
	}
	
	/**
	 * Scale the peak ramps. This is the distance over which we ramp up from 0..1 and down from 1..0
	 * @param scaleFactor
	 */
	public void scaleRamps(double scaleFactor) {
		if (scaleFactor < 0 || scaleFactor > 1.0) {
			throw new IllegalArgumentException("ramp scale factor must be in [0..1]");
		}
		accelRamp = scaleFactor * DEFAULT_ACCELERATION_RAMP;
		decelRamp = scaleFactor * DEFAULT_DECELERATION_RAMP;
	}
	public String toString() {
		return Formatter.concat("AutoState: [", "speed: ", shared.speed, "maxSpeed: ", shared.maxSpeed, "angle: ", shared.angle, "throttle: (", minThrottle, ", ", maxThrottle, ")]");
	}
}
