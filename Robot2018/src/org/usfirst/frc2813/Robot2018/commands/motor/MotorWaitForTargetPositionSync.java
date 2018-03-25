package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Length;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 */
public final class MotorWaitForTargetPositionSync extends AbstractMotorCommand {
	private final Length allowableError;
	
	public MotorWaitForTargetPositionSync(IMotor motor, Length allowableError) {
		super(motor, false); // NB: IF this is running asynchronously, we don't interrupt the other command...
		this.allowableError = allowableError;
		setName(toString());
	}
	public MotorWaitForTargetPositionSync(IMotor motor) {
		this(motor, motor.getConfiguration().getNativeDisplayLengthUOM().create(0.5));
	}

	@Override
	protected void initialize() {
		super.initialize();
		// NB: If the motor is moving to a position (relative or absolute)
		if(!isFinished()) {
			Logger.printFormat(LogType.INFO,"%s waiting for %s to reach %s.",this,motor,motor.getTargetState().getTargetAbsolutePosition());
		} else {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT waiting pointlessly for %s to reach position, it's already done that.",this,motor);
			}
		}
	}

	@Override
	protected boolean isFinished() {
		// NB: If there's no target position (absolute or relative), this will always return true.
		boolean finished = motor.getCurrentPositionErrorWithin(allowableError);
		// NB: If the motor is moving to a position (relative or absolute)
		if(!finished) {
			Logger.printFormat(LogType.INFO,"%s waiting for %s to reach %s within +/- %s.",this,motor,motor.getTargetState().getTargetAbsolutePosition(), allowableError);
		}
		return finished;
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", allowableError=" + allowableError + ")";
    }
}
