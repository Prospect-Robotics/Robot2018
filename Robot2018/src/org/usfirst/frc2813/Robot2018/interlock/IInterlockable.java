package org.usfirst.frc2813.Robot2018.interlock;

/**
 * Any object that can have safety interlocks will have this interface.
 * @author mike.taylor
 */
public interface IInterlockable extends IInterlock {
	/**
	 * Register an interlock callback
	 * @param interlock An interlock to check before all operations
	 */
	public void addInterlock(IInterlock interlock);
	/**
	 * Unregister an interlock callback
	 * @param interlock An interlock to check before all operations
	 */
	public void removeInterlock(IInterlock interlock);
}
