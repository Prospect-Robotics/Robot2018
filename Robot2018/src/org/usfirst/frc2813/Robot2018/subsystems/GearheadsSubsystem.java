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
	private boolean _isEmulated = false;
	public boolean encoderFunctional = true;

	/**
	 * Is the subsystem emulated?
	 * @return true/false
	 */
	public boolean isEmulated() {
		return _isEmulated;
	}

	/**
	 * enable emulation mode. Override to initialize emulation state
	 */
	public void enableEmulator() {
		_isEmulated = true;
	}

	/**
	 * disable emulation mode
	 */
	public void disableEmulator() {
		_isEmulated = false;
	}
}
