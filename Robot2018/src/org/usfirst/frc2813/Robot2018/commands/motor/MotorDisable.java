package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Disable elevator. Subsystem will quiesce.
 */
public class MotorDisable extends MotorInstantCommand {
	public MotorDisable(Motor motor) {
		super(motor, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getState().getOperation() == MotorOperation.DISABLED) {
			Logger.info(this + " NOT setting " + motor + " to disable (neutral state), it's already disabled.");
		} else {
			Logger.info(this + " setting " + motor + " to disable (neutral state).");
		}
		motor.disable();
	}

    public String toString() {
        return "MotorDisable(" + motor + ")";
    }

}
