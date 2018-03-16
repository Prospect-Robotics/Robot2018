package org.usfirst.frc2813.Robot2018.motor;

/**
 * Class to represent the state of the Elevator state machine.
 * Glorified enumeration with the ability to describe itself.
 */
public enum MotorControllerState {
	// Zero velocity + brake mode, safe!
	DISABLED,
	// Hold the current position, resist movement
	HOLDING_POSITION,
	// Moving to a position, or holding a set position
	SET_POSITION,
	// Moving at a velocity/speed/etc
	MOVING;
	public boolean isDisabled() {
		return this == DISABLED;
	}

	public boolean isHoldingCurrentPosition() {
		return this == HOLDING_POSITION;
	}
	
	public boolean isIdle() {
		return this.isDisabled() || this.isHoldingCurrentPosition();
	}

	public boolean isMovingToPosition() {
		return this == SET_POSITION;
	}

	public boolean isMoving() {
		return this == MOVING;
	}
}