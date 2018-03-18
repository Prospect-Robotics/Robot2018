package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Wait for a motor system to arrive at position.
 */
public class MotorWaitForPosition extends MotorCommand {
	public MotorWaitForPosition(Motor motor) {
		super(motor, true);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() != MotorOperation.MOVING_TO_POSITION) {
			Logger.info(this + " waiting for " + motor + " to reach position.");
			motor.holdCurrentPosition();
		} else {
			Logger.info(this + " waiting pointlessly for " + motor + " to reach position, it's already one that.");
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		if (motor.getTargetState().getOperation() != MotorOperation.MOVING_TO_POSITION) {
			return true;
		}
		if (motor.getTargetState().getDirection() == Direction.UP) {
			return motor.getCurrentPosition().getValue() > motor.getTargetPosition().getValue();
		}
		return motor.getCurrentPosition().getValue() < motor.getTargetPosition().getValue();
	}

    public String toString() {
        return "MotorWaitForPosition(" + motor + ")";
    }

}
