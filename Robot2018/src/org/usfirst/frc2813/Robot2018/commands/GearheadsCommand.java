package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsCommand extends Command {
	static {
		Logger.addMe();
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
}
