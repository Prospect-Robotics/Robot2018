package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;

/**
 * Move motor in given direction until interrupted.
 * Hold current position with PID when interrupted. 
 */
public class MotorMoveInDirection extends MotorCommand {
	private final Direction direction;

	public MotorMoveInDirection(Motor motor, Direction direction) {
		super(motor, true);
		this.direction = direction;
		if(this.direction.isNeutral()) {
			throw new IllegalArgumentException("Use MotorHoldPosition or MotorDisable instead of passing a neutral direction to " + getClass().getSimpleName());
		}
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE && motor.getTargetDirection() == direction) {
			Logger.info(this + " NOT setting " + motor + " to move " + direction + ", it's already doing that.");
		} else {
			Logger.info(this + " setting " + motor + " to move in the " + direction + " direction.");
			motor.moveInDirectionAtDefaultRate(direction);
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
        * NOTE: Typically this is also the default command for motor subsystems, so it's kind of redundant but logical. 
        */
		motor.holdCurrentPosition();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + direction + ")";
    }
}
