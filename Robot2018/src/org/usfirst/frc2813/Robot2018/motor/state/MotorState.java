package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public class MotorState implements IMotorState {
	
	/* ----------------------------------------------------------------------------------------------
	 * Configuration
	 * ---------------------------------------------------------------------------------------------- */
	
	private final IMotor motor;
	private final MotorOperation operation; 
	private final Direction targetDirection;
	private final Rate targetRate;
	private final Length targetAbsolutePosition;
	private final Length targetRelativeDistance;
	private final Length startingAbsolutePosition;

	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	protected MotorState(
			IMotor motor,
			MotorOperation state, 
			Direction direction, 
			Rate rate, 
			Length absolutePosition, 
			Length relativeDistance,
			Length startingAbsolutePosition
			) 
	{
		this.motor = motor;
		this.operation = state;
		this.targetDirection = direction;
		this.targetRate = rate;
		this.targetAbsolutePosition = absolutePosition;
		this.targetRelativeDistance = relativeDistance;
		this.startingAbsolutePosition = startingAbsolutePosition;
	}

	/* ----------------------------------------------------------------------------------------------
	 * Implementation
	 * ---------------------------------------------------------------------------------------------- */

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getOperation()
	 */
	@Override
	public MotorOperation getOperation() {
		return operation;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getDirection()
	 */
	@Override
	public Direction getTargetDirection() {
		return targetDirection;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getRate()
	 */
	@Override
	public Rate getTargetRate() {
		return targetRate;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getAbsolutePosition()
	 */
	@Override
	public Length getTargetAbsolutePosition() {
		if(operation == MotorOperation.MOVING_TO_RELATIVE_POSITION) {
			return startingAbsolutePosition.add(targetRelativeDistance.multiply(targetDirection.getMultiplierAsDouble()));
		} else {
			return targetAbsolutePosition;
		}
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getRelativeDistance()
	 */
	@Override
	public Length getTargetRelativeDistance() {
		return targetRelativeDistance;
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#getDescription()
	 */
	@Override
	public String getDescription() {
		return "MotorState[Op=" + operation 
				+ (targetDirection != null ? " Dir=" + targetDirection : "")
				+ (targetRate != null ? " Rate=" + targetRate : "")
				+ (targetAbsolutePosition != null ? " AbsPos=" + targetAbsolutePosition : "")
				+ (targetRelativeDistance != null ? " RelDist=" + targetRelativeDistance : "")
				+ "]";
	}
	
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#toString()
	 */
	@Override
	public String toString() {
		return getDescription();
	}
	private boolean nullSafeEquals(Object a, Object b) {
		if(a == b) {
			return true;
		}
		if(a == null || b == null) {
			return false;
		}
		return a.equals(b);
	}
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorState#equals(org.usfirst.frc2813.Robot2018.motor.MotorState)
	 */
	@Override
	public boolean equals(MotorState other) {
		if(null == other) {
			return false;
		}
		return nullSafeEquals(other.operation, operation)
				&& nullSafeEquals(other.targetDirection, targetDirection)
				&& nullSafeEquals(other.targetAbsolutePosition, targetAbsolutePosition)
				&& nullSafeEquals(other.targetRelativeDistance, targetRelativeDistance)
				&& nullSafeEquals(other.targetRate, targetRate);
	}

	/**
	 * Shortcut to getOperation().isDisabled()
	 */
	@Override
	public boolean isDisabled() {
		return getOperation().isDisabled();
	}

	/**
	 * Shortcut to getOperation().isHoldingCurrentPosition()
	 */
	@Override
	public boolean isHoldingCurrentPosition() {
		return getOperation().isHoldingCurrentPosition();
	}

	/**
	 * Shortcut to getOperation().isIdle()
	 */
	@Override
	public boolean isIdle() {
		return getOperation().isIdle();
	}

	/**
	 * Shortcut to getOperation().isMoving()
	 */
	@Override
	public boolean isMoving() {
		return getOperation().isMoving();
	}

	/**
	 * Shortcut to getOperation().isMovingToPosition()
	 */
	@Override
	public boolean isMovingToPosition() {
		return getOperation().isMovingToPosition();
	}

	/**
	 * Shortcut to getOperation().isMovingToAbsolutePosition()
	 */
	@Override
	public boolean isMovingToAbsolutePosition() {
		return getOperation().isMovingToAbsolutePosition();
	}

	/**
	 * Shortcut to getOperation().isMovingToRelativePosition()
	 */
	@Override
	public boolean isMovingToRelativePosition() {
		return getOperation().isMovingToRelativePosition();
	}

	@Override
	public boolean isCalibratingSensorInDirection() {
		return getOperation().isCalibratingSensorInDirection();
	}

	/**
	 * Shortcut to getOperation().isMovingInDirectionAtRate()
	 */
	@Override
	public boolean isMovingInDirectionAtRate() {
		return getOperation().isMovingInDirectionAtRate();
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		return Math.abs(getCurrentRateError().getValue()) <= Math.abs(marginOfError.getValue());
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		return Math.abs(getCurrentPositionError().getValue()) <= Math.abs(marginOfError.getValue());
	}

	@Override
	public boolean getHasTargetRelativeDistance() {
		return targetRelativeDistance != null;
	}

	@Override
	public boolean getHasTargetDirection() {
		return targetDirection != null;
	}

	@Override
	public boolean getHasTargetRate() {
		return targetRate != null;
	}

	@Override
	public boolean getHasTargetAbsolutePosition() {
		return targetAbsolutePosition != null;
	}

	@Override
	public boolean getHasTargetRelativePosition() {
		return targetRelativeDistance != null;
	}

	@Override
	public boolean getHasTargetPosition() {
		return getHasTargetRelativePosition() || getHasTargetAbsolutePosition();
	}

	@Override
	public Length getCurrentPositionError() {
		LengthUOM uom = getHasTargetPosition() ? getTargetAbsolutePosition().getUOM() : motor.getConfiguration().getNativeDisplayLengthUOM();  
		if(!getHasTargetPosition()) { 
			return uom.create(0);
		}
		Length convertedCurrentPosition = motor.getCurrentPosition().convertTo(uom);
		Length currentTargetAbsolutePosition = getTargetAbsolutePosition().convertTo(uom); 
		return uom.create(convertedCurrentPosition.getValue() - currentTargetAbsolutePosition.getValue());
	}

	@Override
	public Rate getCurrentRateError() {
		RateUOM uom = getHasTargetRate() ? targetRate.getUOM() : motor.getConfiguration().getNativeDisplayRateUOM();  
		if(!getHasTargetRate()) { 
			return uom.create(0);
		}
		Rate convertedCurrentRate = motor.getCurrentRate().convertTo(uom);
		Rate convertedTargetRate  = targetRate.convertTo(uom); 
		return uom.create(convertedCurrentRate.getValue() - convertedTargetRate.getValue());
	}

	@Override
	public IMotorState convertTo(LengthUOM lengthUOM, RateUOM rateUOM, boolean invertDirection) {
		return new MotorState(
				motor,
				operation,
				invertDirection ? targetDirection.getInverse() : targetDirection,
				targetRate == null ? null : targetRate.convertTo(rateUOM),
				targetAbsolutePosition == null ? null : targetAbsolutePosition.convertTo(lengthUOM),
				targetRelativeDistance == null ? null : targetRelativeDistance.convertTo(lengthUOM),
				startingAbsolutePosition == null ? startingAbsolutePosition :startingAbsolutePosition.convertTo(lengthUOM) 
				);
	}

	@Override
	public Length getStartingAbsolutePosition() {
		return startingAbsolutePosition;
	}

	@Override
	public boolean getHasStartingAbsolutePosition() {
		return true; // NB: Could do this only for relative moves, but why not
	}
}
