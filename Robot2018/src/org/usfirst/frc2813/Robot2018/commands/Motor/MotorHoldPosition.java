package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Maintain elevator position. Elevator does this actively with
 * PID via the motor controller, so no further intervention is
 * required to maintain this.
 */
public class MotorHoldPosition extends MotorCommand {
	public MotorHoldPosition(Motor motor) {
		super(motor, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(!motor.getMotorControllerState().equals(MotorControllerState.HOLDING_POSITION)) {
			motor.holdCurrentPosition();
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
