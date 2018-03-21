package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 */
public final class MotorWaitForHardLimitSwitchSync extends AbstractMotorCommand {
	private final Direction switchDirection;

	public MotorWaitForHardLimitSwitchSync(Motor motor, Direction switchDirection) {
		super(motor, false); // NB: IF this is running asynchronously, we don't interrupt the other command...
		this.switchDirection = switchDirection;
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		// NB: If the motor is moving to a position (relative or absolute)
		if(!isFinished()) {
			Logger.printFormat(LogType.INFO,"%s waiting for %s to reach %s hard limit switch.", this, motor, switchDirection);
		} else {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT waiting pointlessly for %s to reach %s hard limit switch, it's already there.", this, motor, switchDirection);
			}
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		// NB: If there's no target position (absolute or relative), this will always return true.
		return motor.getCurrentLimitSwitchStatus(switchDirection);
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", switchDirection=" + switchDirection + ")";
    }
}
