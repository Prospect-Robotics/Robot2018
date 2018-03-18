package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.values.Length;

public class HoldingPositionMotorState extends MotorState {

	public HoldingPositionMotorState(IMotor motor) {
		super(motor, MotorOperation.HOLDING_CURRENT_POSITION, null, null, null, null, motor.getCurrentPosition());
	}
}
