package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

/**
 * Maintain motor position with closed loop (PID) via the motor 
 * controller, so no further intervention is required to 
 * maintain this. 
 */
public class MotorHoldPosition extends MotorCommand {
	public MotorHoldPosition(Motor motor) {
		super(motor, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() != MotorOperation.HOLDING_CURRENT_POSITION) {
			Logger.info(this + " setting " + motor + " to hold position.");
			motor.holdCurrentPosition();
		} else {
			Logger.info(this + " not setting " + motor + " to hold position, it's already doing that.");
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ")";
    }

}
