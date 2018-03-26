package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public abstract class MotorCommand extends SubsystemCommand<Motor> {
	
	public MotorCommand(Motor target, CommandDuration duration, Lockout lockout) {
		super(target, duration, lockout);
	}
	public MotorCommand(Motor target, CommandDuration duration) {
		this(target, duration, Lockout.Disabled);
	}
	@Override
	protected void interruptedWhileWaitingImpl() {
		traceFormatted("interrupted", "telling %s to hold position.", subsystem);
		subsystem.holdCurrentPosition();
	}
}

