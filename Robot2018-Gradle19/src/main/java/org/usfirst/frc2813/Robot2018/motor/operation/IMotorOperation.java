package org.usfirst.frc2813.Robot2018.motor.operation;

public interface IMotorOperation {

	boolean isDisabled();

	boolean isHoldingCurrentPosition();

	boolean isIdle();

	boolean isMoving();

	boolean isMovingToPosition();

	boolean isMovingToAbsolutePosition();

	boolean isMovingToRelativePosition();

	boolean isMovingInDirectionAtRate();

	boolean isCalibratingSensorInDirection();
}