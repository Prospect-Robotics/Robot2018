package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move motor in given direction until interrupted.
 * Hold current position with PID when interrupted. 
 * This command can be Synchronous or SynchronousWithTimeout
 */
public final class MotorVelocityPIDTest extends SubsystemCommand<Motor> {
	private Direction targetDirection = Direction.FORWARD;
	private final Rate targetRate;
	private final boolean continuousRotation;
	

	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation, Rate targetRate, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.targetRate = targetRate;
		this.continuousRotation = continuousRotation;
		addArg("targetRate", targetRate);
		addArg("continuousRotation", continuousRotation);
		setName(toString());
		if(continuousRotation) {
			if(!subsystem.getConfiguration().getHasHardOrSoftLimit(Direction.FORWARD)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test with a hard or soft FORWARD limit in CONTINOUS mode.");
			}
			if(!subsystem.getConfiguration().getHasHardOrSoftLimit(Direction.REVERSE)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test with a hard or soft REVERSE limit in CONTINOUS mode.");
			}
		} else {
			if(!subsystem.getConfiguration().getHasHardOrSoftLimit(Direction.FORWARD)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test without a hard or soft FORWARD limit in NON-CONTINOUS mode.");
			}
			if(!subsystem.getConfiguration().getHasHardOrSoftLimit(Direction.REVERSE)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test without a hard or soft REVERSE limit in NON-CONTINOUS mode.");
			}
		}
	}
	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation, Rate targetRate, RunningInstructions duration) {
		this(motor, continuousRotation, targetRate, duration, Lockout.Disabled);
	}
	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation, Rate targetRate) {
		this(motor, continuousRotation, targetRate, RunningInstructions.RUN_NORMALLY);
	}
	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation) {
		this(motor, continuousRotation, motor.getConfiguration().getDefaultRate());
	}
	public MotorVelocityPIDTest(Motor motor) {
		this(motor, motor.getConfiguration().hasAll(MotorConfiguration.LimitPosition) ? false : true);
	}

	@Override
	protected void ghscExecute() {
		if((subsystem.getConfiguration().getHasHardLimit(targetDirection) && subsystem.getCurrentHardLimitSwitchStatus(targetDirection))
		|| (subsystem.getConfiguration().getHasSoftLimit(targetDirection) && subsystem.isSoftLimitReached(targetDirection))) {
			targetDirection = targetDirection.getInverse();
			trace("execute", "REVERSING.  GOING " + targetDirection + " @ " + subsystem.getCurrentPosition() + " Error " + subsystem.getCurrentRateError() + " Goal " + targetRate);
			subsystem.moveInDirectionAtRate(targetDirection, targetRate);
		} else {
			trace("execute", "NOT THERE.  GOING " + targetDirection + " @ " + subsystem.getCurrentPosition() + " Error " + subsystem.getCurrentRateError() + " Goal " + targetRate);
		}
	}
	
	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected void ghscInitialize() {
		subsystem.moveInDirectionAtRate(targetDirection, targetRate);
	}

	@Override
	protected boolean ghscIsFinished() {
		return false;
	}
}
