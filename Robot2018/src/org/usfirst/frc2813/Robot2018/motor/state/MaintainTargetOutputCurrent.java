package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

public class MaintainTargetOutputCurrent extends MotorState {
	public MaintainTargetOutputCurrent(IMotor motor, Direction targetDirection, Double targetOutputCurrent) {
		super(
				motor, 
				MotorOperation.MAINTAINING_TARGET_OUTPUT_CURRENT,
				targetDirection,
				null,
				null,
				null,
				null,
				targetOutputCurrent
				);
	}
}
