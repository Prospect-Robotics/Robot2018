package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move motor in given direction
 * Hold current position with PID when interrupted. 
 */
public final class MotorMoveInDirection extends MotorCommand {
	private final Direction direction;
	private final Rate rate;
	
	/**
	 * This command will hold for as long as it's running.
	 */
	public MotorMoveInDirection(Motor motor, Direction direction, Rate rate, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
    	this.direction = direction;
    	this.rate = rate;
		setName(toString());
		addArg("direction", direction);
		addArg("rate", rate);
		if(this.direction.isNeutral()) {
			throw new IllegalArgumentException("Use MotorHoldPosition or MotorDisable instead of passing a neutral direction to " + getClass().getSimpleName());
		}
    }
	public MotorMoveInDirection(Motor motor, Direction direction, Rate rate, RunningInstructions duration) {
		this(motor,direction,rate,duration,Lockout.Disabled);
	}
	public MotorMoveInDirection(Motor motor, Direction direction, Rate rate) {
		this(motor, direction, rate, RunningInstructions.RUN_NORMALLY);
	}
	public MotorMoveInDirection(Motor motor, Direction direction) {
		this(motor, direction, motor.getConfiguration().getDefaultRate());
	}

	@Override
	protected void ghscInitialize() {		
		if(subsystem.getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE 
				&& subsystem.getTargetState().getTargetDirection().equals(direction)
				&& subsystem.getTargetState().getTargetRate().equals(rate)
		)
		{
			traceFormatted("initialize","NOT setting %s to move %s, it's already doing that.", subsystem, direction);
		} else {
			actionFormatted("initialize","setting %s to move in the %s direction", subsystem, direction);
			subsystem.moveInDirectionAtRate(direction, rate);
		}
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected boolean ghscIsFinished() {
		return false;
	}
}
