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
	MOVING_TO_RELATIVE_POSITION
	;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isDisabled()
	 */
	@Override
	public boolean isDisabled() {
		return this == DISABLED;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isHoldingCurrentPosition()
	 */
	@Override
	public boolean isHoldingCurrentPosition() {
		return this == HOLDING_CURRENT_POSITION;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isIdle()
	 */
	@Override
	public boolean isIdle() {
		return this.isDisabled() || this.isHoldingCurrentPosition();
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isMoving()
	 */
	@Override
	public boolean isMoving() {
		return this.isMovingToAbsolutePosition() 
				|| this.isMovingToRelativePosition()
				|| this.isMovingInDirectionAtRate()
				;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isMovingToPosition()
	 */
	@Override
	public boolean isMovingToPosition() {
		return this.isMovingToAbsolutePosition() 
				|| this.isMovingToRelativePosition()
				;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isMovingToAbsolutePosition()
	 */
	@Override
	public boolean isMovingToAbsolutePosition() {
		return this == MOVING_TO_ABSOLUTE_POSITION;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isMovingToRelativePosition()
	 */
	@Override
	public boolean isMovingToRelativePosition() {
		return this == MOVING_TO_RELATIVE_POSITION;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation#isMovingInDirectionAtRate()
	 */
	@Override
	public boolean isMovingInDirectionAtRate() {
		return this == MOVING_IN_DIRECTION_AT_RATE;
	}
}