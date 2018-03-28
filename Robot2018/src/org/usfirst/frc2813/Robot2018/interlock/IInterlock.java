package org.usfirst.frc2813.Robot2018.interlock;

/**
 * Interlocks can be added to GearheadsSubsystem instances.
 * If any interlock returns false from isInterlockSafe(), the subsystem should refuse all commands.
 * @author mike.taylor
 *
 */
public interface IInterlock {
	/**
	 * Is it safe to operate the subsystem?
	 * @return true if safe, false to disable the subsystem
	 */
	public boolean isSafeToOperate();
}
