package org.usfirst.frc2813.Robot2018.interlock;

/**
 * An IIinterlock object is used as a callback to see when it's safe to perform an operation.
 * @author mike.taylor
 */
public interface IInterlock {
	/**
	 * Is it safe to operate the subsystem?
	 * @return true if safe, false to disable the subsystem
	 */
	public boolean isSafeToOperate();
}
