package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.units.values.Time;

/**
 * This object encapsulates how long a command should run, and an optional timer value.
 *
 */
public class RunningInstructions {
	private final RunMode type;
	private final Time time;

	public static RunningInstructions RUN_NORMALLY = new RunningInstructions(RunMode.RunNormally, null);
	public static RunningInstructions RUN_FOREVER = new RunningInstructions(RunMode.RunForever, null);
	//public static RunningInstructions RUN_ASYNCHRONOUSLY = new RunningInstructions(RunMode.RunAsynchronously, null);
	
	/**
	 * Create a new command duration with a specific type an time (optional) 
	 * @param type
	 * @param duration
	 */
	public RunningInstructions(RunMode type, Time time) {
		this.type = type;
		this.time = time;
		if(type == null) {
			throw new IllegalArgumentException("Type may not be null.");
		}
		// Make sure the timeout parameter is there when it should be and not there when it shouldn't
		switch(type) {
		case RunNormally:
		case RunForever:
		//case RunAsynchronously:
		default:
			if(time != null) {
				throw new IllegalArgumentException("Duration is " + type + ", so time parameter must be null.");
			}
			break;
		case RunTimed:
		case RunWithTimeout:
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
	public RunningInstructions() {
		this(RunMode.RunNormally, null);
	}

	public RunMode getType() {
		return type;
	}
	
	public Time getTime() {
		return time;
	}
	
	public boolean isForever() { return type.isRunForever(); }
	//public boolean isAsynchronous() { return type.isRunAsynchronously(); }
	public boolean isTimer() { return type.isRunTimed(); }
	public boolean isTimeout() { return type.isRunWithTimeout(); }
	public boolean isDisabled() { return type.isRunNormally(); }
	
	public String toString() {
		return time != null ? (type + "(" + time + ")") : type.toString(); 
	}
}
