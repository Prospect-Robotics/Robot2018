package org.usfirst.frc2813.Robot2018;

/**
 * Common functions to tell what we are doing with a motor 
 */
public interface IMotorState {
	public boolean isDisabled();
	public boolean isHoldingCurrentPosition();
	public boolean isMovingToPosition();
	public boolean isMoving();
	public IMotorState getState();
}
