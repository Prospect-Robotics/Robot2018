package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public class MovingInDirectionAtRateMotorState extends MotorState {
	public MovingInDirectionAtRateMotorState(IMotor motor, Direction targetDirection, Rate targetRate) {
		super(
				motor, 
				MotorOperation.MOVING_IN_DIRECTION_AT_RATE,
				targetDirection,
				targetRate,
				null,
				null,
				motor.getCurrentPosition()
				);
	}
}
