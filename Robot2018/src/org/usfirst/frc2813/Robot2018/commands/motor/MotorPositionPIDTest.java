package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor in given direction until interrupted.
 * Hold current position with PID when interrupted.
 * It's either synchronous or timed. 
 */
public final class MotorPositionPIDTest extends MotorCommand {
	private final Length minHeight; 
	private final Length maxHeight;
	private final Length allowableError;

	private Direction targetDirection = Direction.REVERSE;
	private Length targetPosition = LengthUOM.Inches.create(0);

	public MotorPositionPIDTest(Motor motor, Length minHeight, Length maxHeight, Length allowableError, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.allowableError = allowableError;
		addArg("minHeight", minHeight);
		addArg("maxHeight", maxHeight);
		addArg("allowableError", allowableError);
		setName(toString());
	}
	public MotorPositionPIDTest(Motor motor, Length minHeight, Length maxHeight, Length allowableError, RunningInstructions duration) {
		this(motor, minHeight, maxHeight, allowableError, duration, Lockout.Disabled);
	}
	public MotorPositionPIDTest(Motor motor, Length minHeight, Length maxHeight, Length allowableError) {
		this(motor, minHeight, maxHeight, allowableError, RunningInstructions.RUN_NORMALLY);
	}
	public MotorPositionPIDTest(Motor motor, Length minHeight, Length maxHeight) {
		this(motor, minHeight, maxHeight, motor.getConfiguration().getNativeDisplayLengthUOM().create(0.5));
	}
	
	@Override
	protected void ghscInitialize() {
		subsystem.moveToAbsolutePosition(minHeight);
	}

	@Override
	protected void ghscExecute() {
		if(subsystem.getCurrentPositionErrorWithin(allowableError)) {
			targetDirection = targetDirection.getInverse();
			trace("execute", "REVERSING.  GOING " + targetDirection + " @ " + subsystem.getCurrentPosition() + " Error " + subsystem.getCurrentPositionError() + " Goal " + targetPosition);
			Length targetPosition = targetDirection.equals(Direction.FORWARD) ? minHeight : maxHeight;
			subsystem.moveToAbsolutePosition(targetPosition);
		} else {
			trace("execute", "NOT THERE.  GOING " + targetDirection + " @ " + subsystem.getCurrentPosition() + " Error " + subsystem.getCurrentPositionError() + " Goal " + targetPosition);
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
