package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;

/**
 * Disable motor. Subsystem will quiesce.
 */
public final class MotorDisableInstant extends AbstractMotorInstantCommand {
	public MotorDisableInstant(Motor motor) {
		super(motor, true);
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.DISABLED) {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT SETTING %s to disable (neutral state), it's already disabled.", this, motor);
			}
		} else {
			Logger.printFormat(LogType.INFO,"%s setting %s to disable (neutral state).",this,motor);
		}
		motor.disable();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ")";
    }

}
