package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsSubsystem extends Subsystem {
	static {
		Logger.addMe();
	}
	public boolean encoderFunctional = true;
}
