package org.usfirst.frc2813.Robot2018.commands;

/**
 * The Duration parameter tells us whether there's a time component to the command and helps us handle the default case for isFinished.<br/>
 * <br/>
 * Disabled - off, this parameter has no effect and there's no timer value.<br/>
 * Timeout  - the timer value is a timeout and the command must return when it elapses<br/>
 * Timer    - this is a TimedCommand and runs for a fixed duration, or until the subclass says it's done.
 * Forever  - no timer value, but we're going to run forever and ignore anything the subclass has to say about it.
 */
public enum CommandDurationType {
	/**
	 * Off, no timer value will be used and this parameter will have no effect 
	 */
	Disabled,
	/**
	 * There will be a timeout and we will return if the timeout elapses even if the command is not yet completed.<br/>
	 * This is a command with an error timeout.
	 */
	Timeout, 
	/**
	 * There will be a timeout and we will return if the timeout elapses even if the command is not yet completed.<br/>
	 * This is a command that runs until a timer goes off.
	 */
	Timer, 
	/**
	 * This command is going to run forever and will ignore whether the command is complete.
	 * This is like returning false from isFinished().
	 */
	Forever,
	/**
	 * This command is going to return immediately, ignoring the subclass's isFinished.
	 */
	Asynchronous;
	public boolean isForever() { return this == Forever; }
	public boolean isTimer() { return this == Timer; }
	public boolean isTimeout() { return this == Timeout; }
	public boolean isDisabled() { return this == Disabled; }
	public boolean isAsynchronous() { return this == Asynchronous; }
}
