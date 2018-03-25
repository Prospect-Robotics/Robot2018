package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;

/**
 * Disable motor. Subsystem will quiesce.
 */
public final class MotorDisableForever extends AbstractMotorCommand {
	public MotorDisableForever(Motor motor) {
		super(motor, true);
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		Logger.printFormat(LogType.INFO,"%s setting as default command for %s.",this,motor);
		motor.setDefaultCommand(this);
		Logger.printFormat(LogType.INFO,"%s setting to not interruptable.",this);
		this.setInterruptible(false);
		Logger.printFormat(LogType.INFO,"%s setting %s to disable (neutral state) until canceled.",this,motor);
		motor.disable();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ")";
    }

    public boolean isFinished() {
    	return false;
    }
}
