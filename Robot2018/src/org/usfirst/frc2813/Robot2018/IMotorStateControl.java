package org.usfirst.frc2813.Robot2018;

public interface IMotorStateControl {
	/*
	 * [ACTION] Stop output of the motor
	 */
	public void disable();
	/*
	 * [ACTION] Set to an absolute encoder position
	 */
	public void setPosition(double inches);
	/*
	 * [ACTION] Hold the current position, resist movement
	 */
	public void holdCurrentPosition();
	/*
	 * [ACTION] Set to a speed and direction. Direction will determine whether speed is
	 * logical forward (positive) or reverse (negative) (not necessarily the same as
	 * the motor direction. Remember it can be inverted by configuration, to hide
	 * the difference)
	 */
	public void move(Direction newDirection, double newSpeedInchesPerSecond);
}
