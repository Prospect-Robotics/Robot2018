package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
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
public final class MotorMoveInDirectionAtRate extends AbstractMotorCommand {
	private final Direction direction;
	private final Rate rate;

	public MotorMoveInDirectionAtRate(IMotor motor, Direction direction, Rate speed) {
		super(motor, true);
		this.direction = direction;
		this.rate = speed;
		if(this.direction.isNeutral()) {
			throw new IllegalArgumentException("Use MotorHoldPosition or MotorDisable instead of passing a neutral direction to " + getClass().getSimpleName());
		}
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE && motor.getTargetState().getTargetDirection().equals(direction) && motor.getTargetState().getTargetRate() == rate) {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT setting %s to move in the %s direction at %s, it's already doing that.",this,motor,direction,rate);
			}
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
        return getClass().getSimpleName() + "(" + motor + ", direction=" + direction + ", rate=" + rate + ")";
    }
}
