package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public final class MotorMoveToAbsolutePosition extends MotorCommand {
	private final Length position;
	private final Length allowableError;
	
    public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError, CommandDuration duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.position = position;
		this.allowableError = null;
		setName(toString());
		addArg("position", position);
		addArg("allowableError", allowableError);
		if(duration.isAsynchronous() && allowableError != null) {
			Logger.warning(this + " does not need to specify an allowable error, if you aren't waiting for completion.");
		}
    }
	public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError, CommandDuration duration) {
		this(motor, position, allowableError, duration, Lockout.Disabled);
	}
	public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError) {
		this(motor, position, allowableError, CommandDuration.DISABLED);
	}
	public MotorMoveToAbsolutePosition(Motor motor, Length position) {
		this(motor, position, null);
	}

	@Override
	protected void subsystemInitializeImpl() {		
		if(subsystem.getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && subsystem.getTargetState().getTargetAbsolutePosition() == position) {
			traceFormatted("initialize","NOT telling %s to move to %s, it's already doing that.", subsystem, position);
		} else {
			traceFormatted("initialize","telling %s to move to %s.", subsystem, position);
			subsystem.moveToAbsolutePosition(position);
		}
	}

	@Override
	protected boolean subsystemIsFinishedImpl() {
    	// Handle completion for sync mode
    	if(subsystem.getCurrentPositionErrorWithin(allowableError)) {
    		traceFormatted("isFinished", "success waiting for %s to move to %s.",subsystem,position);
    		return true;
    	}    	
     	traceFormatted("isFinished","still waiting for %s to move to %s.",subsystem,position);
    	return false;
    }

	@Override
	public boolean isSubsystemRequired() {
		return true;
	}
}
