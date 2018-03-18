package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Length;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 */
public class MotorWaitForTargetPosition extends MotorCommand {
	private final Length allowableError;
	
	public MotorWaitForTargetPosition(Motor motor, Length allowableError) {
		super(motor, true);
		this.allowableError = allowableError;
	}

	@Override
	protected void initialize() {
		super.initialize();
		// NB: If the motor is moving to a position (relative or absolute)
		if(!isFinished()) {
			Logger.printFormat(LogType.INFO,"%s waiting for %s to reach %s.",this,motor,motor.getTargetState().getTargetAbsolutePosition());
		} else {
			Logger.printFormat(LogType.INFO,"%s waiting pointlessly for %s to reach position, it's already done that.",this,motor);
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		// NB: If there's no target position (absolute or relative), this will always return true.
		return motor.getCurrentPositionErrorWithin(allowableError);
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + "," + allowableError + ")";
    }
}
