package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

/**
 * Disable motor.
 * See GearheadsCommand for duration and timeout information.
 * See Subsystem for lockout information.
 */
public final class MotorDisable extends MotorCommand {
	
    public MotorDisable(Motor motor, CommandDuration duration, Lockout lockout) {
    	super(motor, duration, lockout);
    }
    public MotorDisable(Motor motor, CommandDuration duration) {
    	this(motor, duration, Lockout.Disabled);
    }
    public MotorDisable(Motor motor) {
    	this(motor, CommandDuration.DISABLED);
    }

	@Override
	protected void subsystemInitializeImpl() {
		if(subsystem.getTargetState().getOperation() == MotorOperation.DISABLED) {
			if(!isTracingEnabled()) {
				traceFormatted("initialize", "NOT SETTING %s to disable (neutral state), it's already disabled.", subsystem);
			}
		} else {
			traceFormatted("initialize", "setting %s to disable (neutral state).",subsystem);
		}
		subsystem.disable();
	}

	@Override
	public boolean isSubsystemRequired() {
		return true;
	}

	@Override
	protected boolean subsystemIsFinishedImpl() {
		return true;
	}
}
