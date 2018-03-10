package org.usfirst.frc2813.Robot2018;

/**
 * Create a class to represent the state of the Elevator state machine.
 * Glorified enumeration with the ability to describe itself.
 */
public class TalonState {
	// Zero velocity + brake mode, safe!
	public static final TalonState DISABLED = new TalonState("DISABLED");
	// Hold the current position, resist movement 
	public static final TalonState HOLDING_POSITION = new TalonState("HOLDING_POSITION");
	// Moving to a position, or holding a set position
	public static final TalonState SET_POSITION = new TalonState("SET_POSITION");
	// Moving at a velocity/speed/etc
	public static final TalonState MOVING = new TalonState("MOVING");
	
	private String label;
	private TalonState(String label) {
		this.label = label;
	}
	public String toString() {
		return label;
	}
	public boolean isDisabled() {
		return this == DISABLED;
	}
	public boolean isHoldingCurrentPosition() {
		return this == HOLDING_POSITION;
	}
	public boolean isMovingToPosition() {
		return this == SET_POSITION;
	}
	public boolean isMoving() {
		return this == MOVING;
	}
	public TalonState getState() {
		return this;
	}
};
