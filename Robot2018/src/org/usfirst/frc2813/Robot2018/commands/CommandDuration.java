package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.units.values.Time;

/**
 * This object encapsulates how long a command should run, and an optional timer value.
 *
 */
public class CommandDuration {
	private final CommandDurationType type;
	private final Time time;

	public static CommandDuration DISABLED = new CommandDuration(CommandDurationType.Disabled, null);
	public static CommandDuration FOREVER = new CommandDuration(CommandDurationType.Forever, null);
	public static CommandDuration ASYNCHRONOUS = new CommandDuration(CommandDurationType.Asynchronous, null);
	
	/**
	 * Create a new command duration with a specific type an time (optional) 
	 * @param type
	 * @param duration
	 */
	public CommandDuration(CommandDurationType type, Time time) {
		this.type = type;
		this.time = time;
		if(type == null) {
			throw new IllegalArgumentException("Type may not be null.");
		}
		// Make sure the timeout parameter is there when it should be and not there when it shouldn't
		switch(type) {
		case Disabled:
		case Forever:
		case Asynchronous:
		default:
			if(time != null) {
				throw new IllegalArgumentException("Duration is " + type + ", so time parameter must be null.");
			}
			break;
		case Timer:
		case Timeout:
			if(time == null) {
				throw new IllegalArgumentException("Duration is " + type + ", so time parameter must NOT be null.");
			}
			break;
		}
	}
	/**
	 * Create a new command duration with a specific type an time (optional) 
	 * @param type
	 * @param duration
	 */
	public CommandDuration() {
		this(CommandDurationType.Disabled, null);
	}

	public CommandDurationType getType() {
		return type;
	}
	
	public Time getTime() {
		return time;
	}
	
	public boolean isForever() { return type.isForever(); }
	public boolean isAsynchronous() { return type.isAsynchronous(); }
	public boolean isTimer() { return type.isTimer(); }
	public boolean isTimeout() { return type.isTimeout(); }
	public boolean isDisabled() { return type.isDisabled(); }
	
	public String toString() {
		return time != null ? (type + "(" + time + ")") : type.toString(); 
	}
}
