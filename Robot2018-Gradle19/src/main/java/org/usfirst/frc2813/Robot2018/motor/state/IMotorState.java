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
	 * @return The operation this state represents
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
	 * @param other The other MotorState to compare to  
	 * @return true if the two operations are the same, even if they use different units.
	 */
	boolean equals(MotorState other);
	/**
	 * Get the starting absolute position.
	 * @see getHasStartingAbsolutePosition
	 * @return The position of the robot when the state object was created, before any side effects. 
	 */
	Length getStartingAbsolutePosition();
	/**
	 * Get the current difference between target and actual position, returns 0 if not applicable.
	 * @return the signed difference in position from the goal state if applicable, 0 otherwise.
	 * @see getHasTargetPosition 
	 */
	Length getCurrentPositionError();
	/**
	 * Get the current difference between target and actual rate, returns 0 if not applicable.
	 * @return the signed difference in rate from the goal state if applicable, 0 otherwise.
	 * @see getHasTargetRate
	 */
	Rate getCurrentRateError();
	/**
	 * Are we at the target rate within marginOfError specified?
	 * @param marginOfError What is the maximum error (+/-) to consider "close enough"
	 * @return True if there's no target rate applicable, or the current rate is within +/- marginOfError from our expectation.
	 */
	boolean getCurrentRateErrorWithin(Rate marginOfError);
	/**
	 * Are we at the target rate within marginOfError specified?
	 * @see IMotorState#getHasTargetPosition()
	 * @param marginOfError What is the maximum error (+/-) to consider "close enough"
	 * @return True if there's no target position applicable, or the current position is within +/- marginOfError from our expectation.
	 */
	boolean getCurrentPositionErrorWithin(Length marginOfError);
	/**
	 * Is there a target distance?
	 * @return True if the operation uses a target relative distance parameter.
	 */
	boolean getHasTargetRelativeDistance();
	/**
	 * Is there a target direction?
	 * @return True if the operation uses a target direction parameter.
	 */
	boolean getHasTargetDirection();
	/**
	 * Is there a target rate?
	 * @return True if the operation uses a target rate parameter.
	 */
	boolean getHasTargetRate();
	/**
	 * Is there a target absolute position?
	 * @return True if the operation is a move, and we could determine an absolute position we hope to end at.
	 */
	boolean getHasTargetAbsolutePosition();
	/**
	 * Is there a target relative position?
	 * @return True if the operation uses a target relative position parameter.
	 */
	boolean getHasTargetRelativePosition();
	/**
	 * Is there a target position (absolute or relative)?
	 * @return True if the operation uses a target position parameter (either relative or absolute)
	 */
	boolean getHasTargetPosition();
	/**
	 * Is there a starting absolute position recorded?
	 * @return True if we recorded our position when we created the state, a "before" snapshot of the position.
	 */
	boolean getHasStartingAbsolutePosition();
	/**
	 * Convert units
	 * @param lengthUOM The unit to use for length (distance or angle)
	 * @param rateUOM The unit to use for rate (speed)
	 * @param invertDirection Do we want the Direction values and signs inverted [typically to account for sensor or motor inversion].
	 * @return The new object with units converted as specified.
	 */
	IMotorState convertTo(LengthUOM lengthUOM, RateUOM rateUOM, boolean invertDirection);
}
