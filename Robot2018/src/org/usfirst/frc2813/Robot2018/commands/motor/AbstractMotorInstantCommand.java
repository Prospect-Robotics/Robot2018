package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

/**
 * Abstract motor instant command
 */
public abstract class AbstractMotorInstantCommand extends GearheadsInstantCommand {
	protected final Motor motor;
	public AbstractMotorInstantCommand(Motor motor, boolean requiresSubsystem) {
		this.motor = motor;
		if(requiresSubsystem) {
			requires(motor);
		}
	}

	@Override
	protected void execute() {
		Logger.debug(this + " execute");
	}
	
	@Override
	protected void initialize() {
		Logger.debug(this + " initialize");
	}
	
	@Override
	protected void interrupted() {
		Logger.debug(this + " interrupted");
	}
	
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", ...)";
    }
}
