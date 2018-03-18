package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public class MotorMoveToAbsolutePosition extends MotorInstantCommand {
	private final Length position;

	public MotorMoveToAbsolutePosition(Motor motor, Length position) {
		super(motor, true);
		this.position = position;
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && motor.getTargetPosition() == position) {
			Logger.info(this + " NOT telling " + motor + " to move to " + position + ", it's already doing that.");
		} else {
			Logger.info(this + " telling " + motor + " to move to " + position + ".");
			motor.moveToAbsolutePosition(position);
		}
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + position + ")";
    }
}
