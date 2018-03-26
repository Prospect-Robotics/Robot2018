package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Maintain motor position with closed loop (PID) via the motor 
 * controller, so no further intervention is required to 
 * maintain this. 
 */
public final class MotorHoldPosition extends MotorCommand {

    public MotorHoldPosition(Motor motor, CommandDuration duration, Lockout lockout) {
    	super(motor, duration, lockout);
		setName(toString());
    }
    public MotorHoldPosition(Motor motor, CommandDuration duration) {
    	this(motor, duration, Lockout.Disabled);
    }
	public MotorHoldPosition(Motor motor) {
		this(motor, CommandDuration.DISABLED);
	}
 
	@Override
	protected void subsystemInitializeImpl() {
		if(!subsystem.getTargetState().getOperation().equals(MotorOperation.HOLDING_CURRENT_POSITION)) {
			traceFormatted("initialize","setting %s to hold position", subsystem);
			subsystem.holdCurrentPosition();
		} else {
			traceFormatted("initialize","not setting %s to hold position, it's already doing that", subsystem);
		}
	}

	/**
	 * Requires the subsystem
	 */
	@Override
	public boolean isSubsystemRequired() {
		return true;
	}

	/**
	 * Return false forever, run until interrupted, timer expires, etc..
	 */
	@Override
	protected boolean subsystemIsFinishedImpl() {
		return false;
	}
}
