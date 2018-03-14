package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.logging.Logger;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * This class contains code common to all Subsystems 
 */
public abstract class GearheadsSubsystem extends Subsystem {
	protected Logger logger = Logger.getLogger(this.getClass().getSimpleName());
	public boolean encoderFunctional = true;
}
