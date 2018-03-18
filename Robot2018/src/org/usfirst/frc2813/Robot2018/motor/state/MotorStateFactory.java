package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public final class MotorStateFactory {
	
	/* ----------------------------------------------------------------------------------------------
	 * Factories
	 * ---------------------------------------------------------------------------------------------- */
	
	public static MotorState createDisabled(IMotor motor) {
		return new DisabledMotorState(motor);
	}

	public static MotorState createHoldingPosition(IMotor motor) {
		return new HoldingPositionMotorState(motor);
	}
	
	public static MotorState createMovingToAbsolutePosition(IMotor motor, Length targetAbsolutePosition) {
		return new MovingToAbsolutePositionMotorState(motor, targetAbsolutePosition);
	}

	public static MotorState createMovingInDirectionAtRate(IMotor motor, Direction targetDirection, Rate targetRate) {
		return new MovingInDirectionAtRateMotorState(motor, targetDirection, targetRate);
	}
	
	public static IMotorState createMovingToRelativePosition(IMotor motor, Direction targetDirection, Length targetRelativeDistance) {
		return new MovingToRelativePosition(motor, targetDirection, targetRelativeDistance);
	}

	public static IMotorState createCalibrateSensorInDirection(IMotor motor, Direction targetDirection) {
		return new CalibratingSensorInDirection(motor, targetDirection);
	}
}
