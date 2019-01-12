package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public final class MotorMoveToAbsolutePosition extends MotorCommand {
	private final Length position;
	private final Length allowableError;
	
    public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.position = position;
		this.allowableError = allowableError;
		setName(toString());
		addArg("position", position);
		addArg("allowableError", allowableError);
    }
	public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError, RunningInstructions duration) {
		this(motor, position, allowableError, duration, Lockout.Disabled);
	}
	public MotorMoveToAbsolutePosition(Motor motor, Length position, Length allowableError) {
		this(motor, position, allowableError, RunningInstructions.RUN_NORMALLY);
	}
	public MotorMoveToAbsolutePosition(Motor motor, Length position) {
		this(motor, position, null);
	}

	@Override
	protected void ghscInitialize() {		
		if(subsystem.getTargetState().getOperation() == MotorOperation.MOVING_TO_ABSOLUTE_POSITION && subsystem.getTargetState().getTargetAbsolutePosition() == position) {
			traceFormatted("initialize","NOT telling %s to move to %s, it's already doing that.", subsystem, position);
		} else {
			actionFormatted("initialize","telling %s to move to %s.", subsystem, position);
			subsystem.moveToAbsolutePosition(position);
		}
	}

	@Override
	protected boolean ghscIsFinished() {
		if(allowableError == null) return true;
    	// Handle completion for sync mode
    	if(subsystem.getCurrentPositionErrorWithin(allowableError)) {
    		actionFormatted("isFinished", "success waiting for %s to move to %s.",subsystem,position);
    		return true;
    	}    	
     	traceFormatted("isFinished","still waiting for %s to move to %s.",subsystem,position);
    	return false;
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}
}
