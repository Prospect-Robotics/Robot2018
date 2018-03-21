package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsCommand extends Command {
	private boolean isDefaultCommand = false;
	static {
		Logger.addMe();
	}

	public GearheadsCommand() {
		super();
	}

	public GearheadsCommand(String name) {
		super(name);
	}

	public GearheadsCommand(double timeout) {
		super(timeout);
	}

	public GearheadsCommand(String name, double timeout) {
		super(name, timeout);
	}
	
	protected boolean isFinished() {
		Logger.debug(this + " in isFinished");
		return false;
	}

	// @Override
	protected void execute() {
		Logger.debug(this + " in execute");
	}
	
	// @Override
	protected void initialize() {
		Logger.debug(this + " in initialize");
	}
	
	// @Override
	protected void interrupted() {
		Logger.debug(this + " interrupted");
	}
	
	public void setIsDefaultCommand(boolean isDefaultCommand) {
		this.isDefaultCommand = isDefaultCommand;
	}
	
	public boolean isDefaultCommand() {
		return isDefaultCommand;
	}
}
