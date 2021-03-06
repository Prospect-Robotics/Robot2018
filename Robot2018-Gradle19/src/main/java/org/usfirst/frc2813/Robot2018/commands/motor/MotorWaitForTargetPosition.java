package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.values.Length;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 * Do not require the subsystem, in case someone is driving this in parallel.
 */
public final class MotorWaitForTargetPosition extends SubsystemCommand<Motor> {
	private final Length allowableError;
	
	/*
	 * Make a synchronous command to wait for the target position, without a timeout
	 */
	public MotorWaitForTargetPosition(Motor motor, Length allowableError, RunningInstructions duration, Lockout lockout) {
		super(motor, duration, lockout);
		this.allowableError = allowableError;
		addArg("allowableError", allowableError);
		setName(toString());
	}
	public MotorWaitForTargetPosition(Motor motor, Length allowableError, RunningInstructions duration) {
		this(motor,allowableError,duration,Lockout.Disabled);
	}
	public MotorWaitForTargetPosition(Motor motor, Length allowableError) {
		this(motor, allowableError, RunningInstructions.RUN_NORMALLY);
	}

	/*
	 * Make a synchronous command to wait for the target position, without a timeout
	 */
	public MotorWaitForTargetPosition(Motor motor) {
		this(motor, motor.getConfiguration().getNativeDisplayLengthUOM().create(0.5));
	}

	@Override
	protected void ghscInitialize() {
		// NB: If the motor is moving to a position (relative or absolute)
		if(!isFinished()) {
			actionFormatted("initialize","waiting for %s to reach %s.",subsystem,subsystem.getTargetState().getTargetAbsolutePosition());
		} else {
			actionFormatted("initialize","NOT waiting pointlessly for %s to reach position, it's already done that.", subsystem);
		}
	}

	@Override
	protected boolean ghscIsFinished() {
		// NB: If there's no target position (absolute or relative), this will always return true.
		boolean finished = subsystem.getCurrentPositionErrorWithin(allowableError);
		// NB: If the motor is moving to a position (relative or absolute)
		if(!finished) {
			traceFormatted("isFinished", "waiting for %s to reach %s within +/- %s.",subsystem,subsystem.getTargetState().getTargetAbsolutePosition(), allowableError);
		}
		return finished;
	}

	/**
	 * Return false, we don't interrupt any other command using the motor - it's possibly moving the motor where we want it.
	 */
	@Override
	public boolean ghscIsSubsystemRequired() {
		return false;
	}
}
