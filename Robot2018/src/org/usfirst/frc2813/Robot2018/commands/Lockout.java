package org.usfirst.frc2813.Robot2018.commands;

/**
 * This enumeration is used to specify the execution behavior of a command that supports
 * taking over exclusive access to a resource.
 * 
 * Locking behavior is quite simple:
 * 
 * *  Lockout.WhileRunning means to set interruptible to false and force the command to have exclusive access to any
 * subsystems it requires.  On exit, it's set to interruptible and releases access to any subsystems it "required".  
 * 
 * *  Lockout.UntilUnlocked means that in addition to being uninterruptible, we will also add Locked.ALWAYS
 * to the target subsystem before we set the command to interruptible.  This is a "thread safe" extension of
 * the command so that you can perform some action on the subsystem and lock it down atomically, without the fear
 * of pre-emption by a default command or another command that could happen if you used a separate Lock command.
 * 
 * NOTE: RunMode.Runforever could be used as a poor substitute for Lockout.UntilUnlocked, but that solution lacks the ability to "unlock" 
 * 
 * EXAMPLES: 
 * 
 * 1.  Disable a motor and prevent the default command (or any other) from moving it until it's explicitly locked.  
 * Use Lockout.RunForever
 * 
 * 2.  Run a command that needs to complete and shouldn't be interrupted.
 * Use Lockout.WhileRunning
 * 
 * 3.  Need to lockout actuator A before engaging actuator B.
 * Use Lock command on A, then use the command on B.
 * 
 * 3.  Need to lockout actuator A before engaging actuator B and then lock down B until explicitly unlocked
 * Use Lock command on A, then use the command on B with Lockout.UntilUnlocked.
 *
 * WARNINGS:
 * 
 * You should use interlocks instead of locking for interactive commands whenever possible.  For example, if you want to lockout 
 * a button while a motor is moving - it's better to code up an interlock and add it to the button command or to the subsystem
 * that the button invokes.  This is cleaner, is always in effect, and is safe always.  Using subsystem locking (Lockout.UntilUnlocked)
 * is more appropriate for temporary lockouts and automatic command sequences. 
 * 
 * We do not have any error handling right now, so you must use interlocks for that and they aren't scriptable yet.
 * Let's say you want to lock the Elevator before you engage the ratchet.  Your command to lock the Elevator fails for
 * some reason (not sure how it can happen, but lets say it did).  Now you engage the Ratchet and things break.  Workaround is 
 * to add an interlock on the Elevator commands that disables operation when the ratchet is engaged.  
 * There's an example of this in OI.   
 * 
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
