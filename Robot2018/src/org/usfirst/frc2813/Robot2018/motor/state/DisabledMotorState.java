package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;

public class DisabledMotorState extends MotorState {

	public DisabledMotorState(IMotor motor) {
		super(motor, MotorOperation.DISABLED, null, null, null, null, motor.getCurrentPosition(), null);
	}
}
