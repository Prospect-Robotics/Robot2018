package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;

/**
 * Abstract base class for motor commands.
 */
public abstract class AbstractMotorCommand extends GearheadsCommand {
	protected final Motor motor;
	public AbstractMotorCommand(Motor subsystem, boolean requiresSubsystem) {
		this.motor = subsystem;
		if(requiresSubsystem) {
			requires(subsystem);
		}
		Logger.debug(this + "constructed");
	}

	// @Override
	protected void execute() {
		Logger.debug(this + "in execute");
	}
	
	// @Override
	protected void initialize() {
		Logger.debug(this + "in initialize");
	}
	
	// @Override
	protected void interrupted() {
		Logger.debug(this + "interrupted");
	}
    // @Override
    public String toString() {
    	return getClass().getSimpleName() + "(" + motor + ", ...)";
    }
}
