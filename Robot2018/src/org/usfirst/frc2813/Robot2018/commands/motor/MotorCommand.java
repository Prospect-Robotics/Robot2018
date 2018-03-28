package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public abstract class MotorCommand extends SubsystemCommand<Motor> {
	
	public MotorCommand(Motor target, RunningInstructions duration, Lockout lockout) {
		super(target, duration, lockout);
	}
	public MotorCommand(Motor target, RunningInstructions duration) {
		this(target, duration, Lockout.Disabled);
	}
	@Override
	protected void ghscinterruptedWhileWaiting() {
		traceFormatted("interrupted", "telling %s to hold position.", subsystem);
		subsystem.holdCurrentPosition();
	}
}

