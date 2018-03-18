package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
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
			Logger.printFormat(LogType.INFO,"%s NOT telling %s to move to %s, it's already doing that.",this,motor,position);
		} else {
			Logger.printFormat(LogType.INFO,"%s telling %s to move to %s.",this,motor,position);
			motor.moveToAbsolutePosition(position);
		}
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + position + ")";
    }
}
