package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.Robot2018.subsystems.motor.MotorState;

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
		if(!motor.getMotorControllerState().equals(MotorState.DISABLED)) {
			Logger.info("NOT setting " + motor.getName() + " to disable (neutral state), it's already disabled.");
		} else {
			Logger.info("Setting " + motor.getName() + " to disable (neutral state).");
		}
		motor.disable();
	}
}
