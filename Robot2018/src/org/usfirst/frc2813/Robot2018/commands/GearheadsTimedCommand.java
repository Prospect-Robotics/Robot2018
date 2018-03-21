package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsTimedCommand extends GearheadsCommand {
	static {
		Logger.addMe();
	}

	public GearheadsTimedCommand(double timeout) {
		super(timeout);
	}

	public GearheadsTimedCommand(String name, double timeout) {
		super(name, timeout);
	}

	/**
	 * Ends command when timed out.
	 */
	protected boolean isFinished() {
		return isTimedOut();
	}
	
}
