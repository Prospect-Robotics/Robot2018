package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
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
		motor.disable();
	}
}
