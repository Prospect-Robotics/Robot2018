package org.usfirst.frc2813.Robot2018.commands;

/**
 * This enumeration is used to specify the execution behavior of a command that supports
 * taking over exclusive access to a resource.
 */
public enum Lockout {
	/**
	 * Prevent stealing of the resource while we're running.
	 */
	WhileRunning,
	/**
	 * Prevent stealing of the resource even when asynchronous
	 */
	UntilUnlocked,
	/**
	 * Do not prevent stealing of the resource, and then leave the subsystem interlocked.
	 */
	Disabled
	;
	
	/**
	 * Return true if we want to lock out everything
	 */
	public boolean isWhileRunning() {
		return this == WhileRunning;
	}
	/**
	 * Return true if we don't want to do this
	 */
	public boolean isDisabled() {
		return this == Disabled;
	}
	/**
	 * Return true if we want to leave it locked until we explicitly unlock it (if ever)
	 */
	public boolean isUntilUnlocked() {
		return this == UntilUnlocked;
	}
	/**
	 * Return true if we want to lock the subsystem when we start
	 */
	public boolean isEnabled() {
		return !isDisabled();
	}
}
