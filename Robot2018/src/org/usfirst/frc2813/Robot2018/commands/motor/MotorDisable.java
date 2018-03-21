package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;

/**
 * Disable motor. Subsystem will quiesce.
 */
public class MotorDisable extends AbstractMotorInstantCommand {
	public MotorDisable(Motor motor) {
		super(motor, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.DISABLED) {
			Logger.printFormat(LogType.INFO,"%s NOT SETTING %s to disable (neutral state), it's already disabled.", this, motor);
		} else {
			Logger.printFormat(LogType.INFO,"%s setting %s to disable (neutral state).",this,motor);
		}
		motor.disable();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ")";
    }

}
