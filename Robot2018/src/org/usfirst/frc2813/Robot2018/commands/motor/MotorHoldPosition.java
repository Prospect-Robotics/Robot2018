package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Maintain motor position with closed loop (PID) via the motor 
 * controller, so no further intervention is required to 
 * maintain this. 
 */
public final class MotorHoldPosition extends MotorCommand {

    public MotorHoldPosition(Motor motor, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		setName(toString());
    }
    public MotorHoldPosition(Motor motor, RunningInstructions duration) {
    	this(motor, duration, Lockout.Disabled);
    }
	public MotorHoldPosition(Motor motor) {
		this(motor, RunningInstructions.RUN_NORMALLY);
	}
 
	@Override
	protected void ghscInitialize() {
		if(!subsystem.getTargetState().getOperation().equals(MotorOperation.HOLDING_CURRENT_POSITION)) {
			actionFormatted("initialize","setting %s to hold position", subsystem);
			subsystem.holdCurrentPosition();
		} else {
			traceFormatted("initialize","not setting %s to hold position, it's already doing that", subsystem);
		}
	}

	/**
	 * Requires the subsystem
	 */
	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	/**
	 * Return false forever, run until interrupted, timer expires, etc..
	 */
	@Override
	protected boolean ghscIsFinished() {
		return false;
	}
}
