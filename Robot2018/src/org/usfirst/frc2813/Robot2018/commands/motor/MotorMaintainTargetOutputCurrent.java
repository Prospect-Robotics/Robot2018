package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Hold specific current level with PID  
 */
public final class MotorMaintainTargetOutputCurrent extends MotorCommand {
	private final Direction targetDirection;
	private final Double targetOutputCurrent;
	
	/**
	 * This command will hold for as long as it's running.
	 */
	public MotorMaintainTargetOutputCurrent(Motor motor, Direction targetDirection, Double targetOutputCurrent, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
    	this.targetOutputCurrent = targetOutputCurrent;
    	this.targetDirection = targetDirection;
		setName(toString());
		addArg("targetDirection", targetDirection);
		addArg("targetOutputCurrent", targetOutputCurrent);
    }
	
	public MotorMaintainTargetOutputCurrent(Motor motor, Direction targetDirection, Double targetOutputCurrent, RunningInstructions duration) {
		this(motor,targetDirection,targetOutputCurrent,duration,Lockout.Disabled);
	}
	
	public MotorMaintainTargetOutputCurrent(Motor motor, Direction targetDirection, Double targetOutputCurrent) {
		this(motor,targetDirection,targetOutputCurrent,RunningInstructions.RUN_NORMALLY);
	}

	@Override
	protected void ghscInitialize() {		
		if(subsystem.getTargetState().getOperation() == MotorOperation.MAINTAINING_TARGET_OUTPUT_CURRENT 
				&& subsystem.getTargetState().getTargetOutputCurrent().equals(targetOutputCurrent)
		)
		{
			traceFormatted("initialize","NOT setting %s to move %s at %s amps.  It's already doing that.", subsystem, targetDirection, targetOutputCurrent);
		} else {
			actionFormatted("initialize","setting %s to move %s at %s amps.", subsystem, targetDirection, targetOutputCurrent);
			subsystem.maintainTargetOutputCurrent(targetDirection, targetOutputCurrent);
		}
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected boolean ghscIsFinished() {
		return false;
	}
}
