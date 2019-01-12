package org.usfirst.frc2813.Robot2018.motor.state;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;

public class MovingToRelativePosition extends MotorState {

	public MovingToRelativePosition(IMotor motor, Direction targetDirection, Length targetRelativeDistance) {
		super(
				motor,
				MotorOperation.MOVING_TO_RELATIVE_POSITION,
				targetDirection,
				null,
				null,
				targetRelativeDistance,
				motor.getCurrentPosition()
				);
	}
}
