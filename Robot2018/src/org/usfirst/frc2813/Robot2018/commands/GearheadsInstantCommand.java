package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.InstantCommand;

public abstract class GearheadsInstantCommand extends GearheadsCommand {
	static {
		Logger.addMe();
	}

	public GearheadsInstantCommand() {
		super();
	}

	public GearheadsInstantCommand(String name) {
		super(name);
	}

	/*
	 * Yep, this is the only difference
	 */
	protected boolean isFinished() {
	  return true;
	}
	
}
