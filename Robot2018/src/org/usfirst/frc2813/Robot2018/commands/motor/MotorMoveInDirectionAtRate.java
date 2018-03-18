package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move motor  in given direction at given speed until 
 * interrupted. Hold current position with PID when interrupted.
 */
public class MotorMoveInDirectionAtRate extends MotorCommand {
	private final Direction direction;
	private final Rate rate;

	public MotorMoveInDirectionAtRate(Motor motor, Direction direction, Rate speed) {
		super(motor, true);
		this.direction = direction;
		this.rate = speed;
		if(this.direction.isNeutral()) {
			throw new IllegalArgumentException("Use MotorHoldPosition or MotorDisable instead of passing a neutral direction to " + getClass().getSimpleName());
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE && motor.getTargetDirection() == direction && motor.getTargetSpeed() == rate) {
			Logger.printFormat(LogType.INFO,"%s NOT setting %s to move in the %s direction at %s, it's already doing that.",this,motor,direction,rate);
		} else {
			Logger.printFormat(LogType.INFO,"%s setting %s to move in the %s direction at %s.",this,motor,direction,rate);
			motor.moveInDirectionAtRate(direction, rate);
		}
	}

	@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		super.interrupted();
        /*
        NOTE: Most motor subsystems will have a default command of holdCurrentPosition 
        so this is kind of redundant but still the logical thing to put ehre just in case. 
        */
		motor.holdCurrentPosition();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + direction + ", " + rate + ")";
    }
}
