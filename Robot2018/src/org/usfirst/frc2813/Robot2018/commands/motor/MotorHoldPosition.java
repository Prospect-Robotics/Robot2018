package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
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
		if(motor.getState().getOperation() != MotorOperation.HOLDING_CURRENT_POSITION) {
			Logger.info("Setting " + motor.getName() + " to hold position.");
			motor.holdCurrentPosition();
		} else {
			Logger.info("Not setting " + motor.getName() + " to hold position, it's already doing that.");
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
