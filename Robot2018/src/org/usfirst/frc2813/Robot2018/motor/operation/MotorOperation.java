package org.usfirst.frc2813.Robot2018.motor.operation;

/**
 * Class to represent the state of the motor state machine.
 * Glorified enumeration with the ability to describe itself.
 */
public enum MotorOperation implements IMotorOperation {
	// Zero velocity + brake mode, safe!
	DISABLED,
	// Hold the current position, resist movement
	HOLDING_CURRENT_POSITION,
	// Moving to a position, or holding a set position
	MOVING_TO_ABSOLUTE_POSITION,
	// Moving at a velocity/speed/etc
	MOVING_IN_DIRECTION_AT_RATE,
	// Moving a relative distance in a direction
	MOVING_TO_RELATIVE_POSITION,
	// Calibrating sensor in a direction
	CALIBRATING_SENSOR_IN_DIRECTION
	;
	@Override
	public boolean isDisabled() {
		return this == DISABLED;
	}

	@Override
	public boolean isHoldingCurrentPosition() {
		return this == HOLDING_CURRENT_POSITION;
	}
	
	@Override
	public boolean isIdle() {
		return this.isDisabled() || this.isHoldingCurrentPosition();
	}
	
	@Override
	public boolean isMoving() {
		return this.isMovingToAbsolutePosition() 
				|| this.isMovingToRelativePosition()
				|| this.isMovingInDirectionAtRate()
				;
	}
	
	@Override
	public boolean isMovingToPosition() {
		return this.isMovingToAbsolutePosition() 
				|| this.isMovingToRelativePosition()
				;
	}

	@Override
	public boolean isMovingToAbsolutePosition() {
		return this == MOVING_TO_ABSOLUTE_POSITION;
	}

	@Override
	public boolean isMovingToRelativePosition() {
		return this == MOVING_TO_RELATIVE_POSITION;
	}

	@Override
	public boolean isMovingInDirectionAtRate() {
		return this == MOVING_IN_DIRECTION_AT_RATE;
	}

	@Override
	public boolean isCalibratingSensorInDirection() {
		return this == CALIBRATING_SENSOR_IN_DIRECTION;
	}
}