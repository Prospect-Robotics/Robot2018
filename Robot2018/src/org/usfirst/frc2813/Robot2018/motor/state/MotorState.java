package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.logging.Logger;
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
		if(targetDirection != null) {
			return targetDirection;
		}
		if(getHasTargetAbsolutePosition() && getHasStartingAbsolutePosition()) {
			return (getTargetAbsolutePosition().getCanonicalValue() < getStartingAbsolutePosition().getCanonicalValue()) ? Direction.REVERSE : Direction.FORWARD;
		}
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
		String params = ((targetDirection != null ? " TgtDir=" + targetDirection : "")
				+ (targetRate != null ? " TgtRate=" + targetRate : "")
				+ (targetAbsolutePosition != null ? " TgtAbsPos=" + targetAbsolutePosition : "")
				+ (targetRelativeDistance != null ? " TgtRelDist=" + targetRelativeDistance : "")
				+ (targetRate != null ? " TgtRateErr=" + getCurrentRateError() : "")
				+ (operation.isMovingToPosition() ? " TgtPosErr=" + getCurrentPositionError() : "")
				).trim();
		return operation + (params.length() > 0 ? "[" + params + "]" : "");				 
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
		return Math.abs(getCurrentRateError().getCanonicalValue()) <= Math.abs(marginOfError.getCanonicalValue());
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		return Math.abs(getCurrentPositionError().getCanonicalValue()) <= Math.abs(marginOfError.getCanonicalValue());
	}

	@Override
	public boolean getHasTargetRelativeDistance() {
		return targetRelativeDistance != null;
	}

	@Override
	public boolean getHasTargetDirection() {
		return getTargetDirection() != null;
	}

	@Override
	public boolean getHasTargetRate() {
		return targetRate != null;
	}

	@Override
	public boolean getHasTargetAbsolutePosition() {
		return getTargetAbsolutePosition() != null;
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
		if(!getHasTargetRate()) { 
			return motor.getConfiguration().createSensorRate(0);
		}
		//RateUOM uom = targetRate.getUOM();
		RateUOM uom = motor.getConfiguration().getNativeSensorRateUOM();
		Rate rate = motor.getCurrentRate();
		Rate convertedCurrentRate = rate.convertTo(uom);
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
	

	@Override
	public boolean checkStatus(IStatusCheckCallback cb) {
		boolean done = false;

		// Capture the encoder value
		int currentEncoderValue = motor.getConfiguration().toSensorUnits(motor.getCurrentPosition()).getValueAsInt();
		long currentTimeMillis = System.currentTimeMillis();
		
		// Calibration complete?
		if(!done) {
			if(isCalibratingSensorInDirection() && motor.getCurrentHardLimitSwitchStatus(getTargetDirection())) {
				cb.completed("We think we are calibrating and hit the correct hard limit switch for " + getTargetDirection());
				done = true;
			} else if(isDisabled()) {
				cb.disabled("We are disabled.  This may be due to a failure, or not.");
				done = true;
			}
		}
		
		// Check hard limits reached while moving
		if(!done) {
			if(getTargetDirection() != null) {
				if(motor.getCurrentHardLimitSwitchStatus(getTargetDirection())) {
					cb.interrupted("We were moving and hit a hard limit switch.");
					done = true;
				}
				if(motor.getCurrentSoftLimitSwitchStatus(getTargetDirection())) {
					cb.interrupted("We were moving and hit a soft limit switch.");
					done = true;
				}
			}
		}
		
		// If holding position
		if(isHoldingCurrentPosition()) {
			cb.completed("We're just holding the current position.");
			done = true;
		}

		// See if we're at the target position
		if(!done) {
			Length margin = motor.getConfiguration().createDisplayLength(0.001);
			if(isMovingToPosition() && motor.getCurrentPositionErrorWithin(margin)) {
				cb.completed("We think we are within " + margin + " of target.  Actual error is " + motor.getCurrentPositionError() + " position is " + motor.getCurrentPosition());
				done = true;
			}
		}

		// Check the limits and stalled - PID died?
		if(!done) {
			// PID running
			if(isMovingToPosition()) {
				boolean hitHardLimit = motor.getCurrentHardLimitSwitchStatus(Direction.FORWARD) || motor.getCurrentHardLimitSwitchStatus(Direction.REVERSE);
				boolean hitSoftLimit = motor.getCurrentSoftLimitSwitchStatus(Direction.FORWARD) || motor.getCurrentSoftLimitSwitchStatus(Direction.REVERSE);
				// If we're not being run for the first time, see if we've stopped moving...
				// We're running PID but we stopped moving and a limit is active... tell tale sign
				if(cb.getLastEncoderValue() != 0 && (hitHardLimit || hitSoftLimit) && (currentEncoderValue == cb.getLastEncoderValue())) { // TODO: Add a time component to 'not changed in xx seconds' and add a 'margin for pulses' this only works for simulator
					if(hitHardLimit) {
						cb.interrupted("PID hit hard limit switch and stopped.");
						done = true;
					}
					if(hitSoftLimit) {
						cb.interrupted("PID hit soft limit switch and stopped.");
						done = true;
					}
				}
			}
		}
		cb.setLastCheckedTimeMillis(currentTimeMillis);
		cb.setLastEncoderValue(currentEncoderValue);
		return done;
	}
}
