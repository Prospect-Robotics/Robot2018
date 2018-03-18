package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.operation.IMotorOperation;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public interface IMotorState extends IMotorOperation {
	/**
	 * Get the operation 
	 */
	MotorOperation getOperation();
	/**
	 * Get the direction
	 * @return the value, or null if it's not relevant to the operation
	 */
	Direction getTargetDirection();
	/**
	 * Get the rate
	 * @return the value, or null if it's not relevant to the operation
	 */
	Rate getTargetRate();
	/**
	 * Get the absolute position of the operation
	 * @return the value, or null if it's not relevant to the operation
	 */
	Length getTargetAbsolutePosition();
	/**
	 * Get the relative distance of the operation
	 * @return the value, or null if it's not relevant to the operation
	 */
	Length getTargetRelativeDistance();
	/**
	 * Get the description the operation
	 * @return the value, or null if it's not relevant to the operation
	 */
	String getDescription();
	/**
	 * Get the description the operation
	 * @return the value, or null if it's not relevant to the operation
	 */
	String toString();
	/**
	 * Compare two operations to see if they are the same operation.
	 * NOTE: Does NOT know if two operations would have the same result.  
	 * i.e. If your position is 10, "move to 20" and "move 10 forward" would give
	 * the same answer, but we aren't predicting the future here!  
	 * @return true if the two operations are the same, even if they use different units.
	 */
	boolean equals(MotorState other);
	/**
	 * Get the starting absolute position.
	 * @see hasStartingAbsolutePosition()
	 */
	Length getStartingAbsolutePosition();
	/**
	 * Get the current difference between target and actual position, returns 0 if not applicable.
	 * @see hasTargetPosition()
	 */
	Length getCurrentPositionError();
	/**
	 * Get the current difference between target and actual rate, returns 0 if not applicable.
	 * @see hasTargetRate()
	 */
	Rate getCurrentRateError();
	/**
	 * Are we at the target rate within marginOfError specified?
	 * NOTE: Returns true if there's no target rate applicable to the current state.
	 */
	boolean getCurrentRateErrorWithin(Rate marginOfError);
	/**
	 * Are we at the target rate within marginOfError specified?
	 * NOTE: Returns true if there's no target length applicable to the current state.
	 * @see hasTargetPosition
	 */
	boolean getCurrentPositionErrorWithin(Length marginOfError);
	/**
	 * Is there a target distance?
	 */
	boolean getHasTargetRelativeDistance();
	/**
	 * Is there a target direction?
	 */
	boolean getHasTargetDirection();
	/**
	 * Is there a target distance?
	 */
	boolean getHasTargetRate();
	/**
	 * Is there a target absolute position?
	 */
	boolean getHasTargetAbsolutePosition();
	/**
	 * Is there a target relative position?
	 */
	boolean getHasTargetRelativePosition();
	/**
	 * Is there a target position (absolute or relative)?
	 */
	boolean getHasTargetPosition();
	/*
	 * Is there a starting absolute position recorded?
	 */
	boolean getHasStartingAbsolutePosition();
	/**
	 * Convert units
	 */
	IMotorState convertTo(LengthUOM lengthUOM, RateUOM rateUOM, boolean invertDirection);
}
