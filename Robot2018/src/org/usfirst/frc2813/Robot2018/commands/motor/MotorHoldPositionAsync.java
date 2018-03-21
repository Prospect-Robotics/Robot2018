package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;

/**
 * Maintain motor position with closed loop (PID) via the motor 
 * controller, so no further intervention is required to 
 * maintain this. 
 */
public final class MotorHoldPositionAsync extends AbstractMotorCommand {
	public MotorHoldPositionAsync(Motor motor) {
		super(motor, true);
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(!motor.getTargetState().getOperation().equals(MotorOperation.HOLDING_CURRENT_POSITION)) {
			Logger.printFormat(LogType.INFO,"%s setting %s to hold position",this,motor);
			motor.holdCurrentPosition();
		} else {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s not setting %s to hold position, it's already doing that",this,motor);
			}
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
