package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 * Do not require the subsystem, in case someone is driving this in parallel.
 */
public final class MotorWaitForHardLimitSwitch extends SubsystemCommand<Motor> {
	private final Direction direction;
	
	/*
	 * Make a synchronous command to wait for the target position, without a timeout
	 */
	public MotorWaitForHardLimitSwitch(Motor motor, Direction direction, CommandDuration duration, Lockout lockout) {
		super(motor, duration, lockout);
		this.direction = direction;
		addArg("direction", direction);
		setName(toString());
	}
	public MotorWaitForHardLimitSwitch(Motor motor, Direction direction, CommandDuration duration) {
		this(motor, direction, duration, Lockout.Disabled);
	}
	public MotorWaitForHardLimitSwitch(Motor motor, Direction direction) {
		this(motor, direction, CommandDuration.DISABLED);
	}

	@Override
	protected void ghscInitialize() {
		// NB: If the motor is moving to a position (relative or absolute)
		if(!isFinished()) {
			traceFormatted("initialize","waiting for %s to reach %s hard limit switch.",subsystem,direction);
		} else {
			traceFormatted("initialize","NOT waiting pointlessly for %s to reach the % hard limit switch, it's already doing that.", subsystem, direction);
		}
	}

	@Override
	protected boolean ghscIsFinished() {
		boolean finished = subsystem.getCurrentHardLimitSwitchStatus(direction);
		if(!finished) {
			traceFormatted("isFinished", "waiting for %s to reach %s hard limit switch.",subsystem,direction);
		} else {
			traceFormatted("isFinished", "%s reached %s hard limit switch.",subsystem,direction);
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
