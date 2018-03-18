package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

public class CalibratingSensorInDirection extends MotorState {
	public CalibratingSensorInDirection(IMotor motor, Direction targetDirection) {
		super(
				motor, 
				MotorOperation.CALIBRATING_SENSOR_IN_DIRECTION,
				targetDirection,
				null,
				null,
				null,
				motor.getCurrentPosition()
				);
	}
}
