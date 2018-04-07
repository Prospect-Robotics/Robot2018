package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.values.Length;

public class MovingToAbsolutePositionMotorState extends MotorState {
	public MovingToAbsolutePositionMotorState(IMotor motor, Length targetAbsolutePosition) {
		super(
				motor,
				MotorOperation.MOVING_TO_ABSOLUTE_POSITION,
				null,
				null, 
				targetAbsolutePosition,
				null,
				motor.getCurrentPosition(),
				null
				);
	}
}
