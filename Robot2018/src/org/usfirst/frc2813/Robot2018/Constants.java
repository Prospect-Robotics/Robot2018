package org.usfirst.frc2813.Robot2018;

public class Constants {
	
	/**
	 * Constants used universally throughout the 2018 Robot code
	 * 
	 */

	/*
	 * Which PID slot to pull gains from. Starting 2018, you can choose from
	 * 0,1,2 or 3. Only the first two (0,1) are visible in web-based
	 * configuration.
	 */
	public static final int kSlotIdx = 0;

	/*
	 * Talon SRX/ Victor SPX will supported multiple (cascaded) PID loops. For
	 * now we just want the primary one.
	 */
	public static final int maintainPIDLoopIdx = 0;
	public static final int movePIDLoopIdx = 1;

	/*
	 * set to zero to skip waiting for confirmation, set to nonzero to wait and
	 * report to DS if action fails.
	 */
	public static final int kTimeoutMs = 10;
	

	/* choose to ensure sensor is positive when output is positive */
	public static boolean kSensorPhase = true;

	/* choose based on what direction you want to be positive,
		this does not affect motor invert. */
	public static boolean kMotorInvert = false;
	
	
}
