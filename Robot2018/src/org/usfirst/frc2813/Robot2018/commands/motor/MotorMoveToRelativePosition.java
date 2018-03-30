package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to a relative position. Motor controller does this without
 * further intervention.
 * Supports synchronous, asynchronous with timeout support.
 */
public final class MotorMoveToRelativePosition extends MotorCommand {
	private final Direction direction;
	private final Length relativeDistance;
	private final Length allowableError;
	
    public MotorMoveToRelativePosition(Motor motor, Direction direction, Length relativeDistance, Length allowableError, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.direction = direction;
		this.relativeDistance = relativeDistance;
		this.allowableError = allowableError;
		setName(toString());
		addArg("direction", direction);
		addArg("relativeDistance", relativeDistance);
		addArg("allowableError", allowableError);
		/*
		if(duration.isAsynchronous() && allowableError != null) {
			throw new IllegalArgumentException(this + " must not specify an allowable error, if you aren't waiting for completion.");
		}
		if(!duration.isAsynchronous() && allowableError == null) {
			throw new IllegalArgumentException(this + " must specify an allowable error for synchronous operation.");
		}
		*/
    }
	public MotorMoveToRelativePosition(Motor motor, Direction direction, Length relativeDistance, Length allowableError, RunningInstructions duration) {
		this(motor, direction, relativeDistance, allowableError, duration, Lockout.Disabled);
	}
	public MotorMoveToRelativePosition(Motor motor, Direction direction, Length relativeDistance, Length allowableError) {
		this(motor, direction, relativeDistance, allowableError, RunningInstructions.RUN_NORMALLY);
	}

	/**
	 * Start the move
	 */
	@Override
	protected void ghscInitialize() {
		traceFormatted("initialize", "telling %s to move %s by %s.", subsystem, direction, relativeDistance);
		subsystem.moveToRelativePosition(direction, relativeDistance);
	}

	@Override
	public boolean ghscIsFinished() {
		if(allowableError == null) return true;
     	// Handle completion for sync mode
    	if(subsystem.getCurrentPositionErrorWithin(allowableError)) {
    		traceFormatted("isFinished", "success waiting for %s to move %s by %s.", subsystem, direction, relativeDistance);
    		return true;
    	}    	
    	traceFormatted("isFinished", "still waiting for %s to move %s by %s.", subsystem, direction, relativeDistance);
    	return false;
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

}
