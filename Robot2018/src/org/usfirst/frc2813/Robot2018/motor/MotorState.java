package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public class MotorState {
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */
	
	private final MotorOperation operation; 
	private final Direction direction;
	private final Rate rate;
	private final Length position;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	private MotorState(MotorOperation state, Direction direction, Rate rate, Length position) {
		this.operation = state;
		this.direction = direction;
		this.rate = rate;
		this.position = position;
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Factories
	 * ---------------------------------------------------------------------------------------------- */
	
	private static final MotorState DISABLED = new MotorState(MotorOperation.DISABLED, null, null, null);
	private static final MotorState HOLDING_POSITION = new MotorState(MotorOperation.HOLDING_CURRENT_POSITION, null, null, null);
	
	public static MotorState createDisabled() {
		return DISABLED;
	}
	
	public static MotorState createHoldingPosition() {
		return HOLDING_POSITION;
	}
	
	public static MotorState createMovingToPosition(Length position) {
		return new MotorState(MotorOperation.MOVING_TO_POSITION, null, null, position);
	}
	
	public static MotorState createMoving(Direction direction, Rate rate) {
		return new MotorState(MotorOperation.MOVING, direction, rate, null);
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Implementation
	 * ---------------------------------------------------------------------------------------------- */

	public MotorOperation getOperation() {
		return operation;
	}

	public Direction getDirection() {
		return direction;
	}

	public Rate getRate() {
		return rate;
	}

	public Length getPosition() {
		return position;
	}
	
	public String getDescription() {
		return "Motor[State=" + operation + " Dir=" + direction + " Rate=" + rate + " Pos=" + position + "]";
	}
	
	public String toString() {
		return getDescription();
	}
	private boolean nullSafeEquals(Object a, Object b) {
		if(a == null && b == null) {
			return true;
		}
		if(a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
	public boolean equals(MotorState other) {
		if(null == other) {
			return false;
		}
		return nullSafeEquals(other.operation, this.operation)
				&& nullSafeEquals(other.direction, direction)
				&& nullSafeEquals(other.position, position)
				&& nullSafeEquals(other.rate, rate);
	}
}
