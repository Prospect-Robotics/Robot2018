package org.usfirst.frc2813.Robot2018.commands;

import java.util.ArrayList;

import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Time;
import org.usfirst.frc2813.util.Formatter;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * This command provides support for the different basic starting 
 * points for synchronous, asynchronous, timed, etc.
 */
public abstract class GearheadsCommand extends Command {
	private final CommandDuration duration;

	static {
		Logger.addMe();
	}
	/**
	 * If TRACING_LOG_LEVEL is within our current log level for the system, we will trace out when all the core Command functions are executing.
	 * This is on by default for all command instances, but can be disabled object-by-object with setTracingEnabled. 
	 */
	private boolean enableTracing = false;
	/**
	 * This log level controls the logging of which of the main 
	 * high level functions being called on command, i.e. "in end', "in isfinished" etc... 
	 */
	private static final LogType TRACING_LOG_LEVEL = LogType.DEBUG;
	/**
	 * We will populate this array with the key/value pairs of arguments for formatting into a nice pretty name.
	 * @see addArg
	 */
	private static final ArrayList<Object> args = new ArrayList<Object>(); 
	/**
	 * Create a new command with the specified type of duration and timer value
	 */
	protected GearheadsCommand(CommandDuration duration) {
		this.duration = duration != null ? duration : new CommandDuration();
		// Do not log implicit duration
		if(duration != null) {
			addArg("duration",duration);
		}
		if(this.duration.getTime() != null) {
			setTimeout(this.duration.getTime());
		}
	}
	/**
	 * Get the duration function of the command, if there's a rule about time
	 */
	public final CommandDuration getDuration() {
		return duration;
	}
	/**
	 * Enable or disable tracking on a per-object basis.  
	 * This is generally used inside subsystem's initDefaultCommand to prevent tracing on the default command
	 * @param isDefaultCommand
	 */
	public final void setTracingEnabled(boolean enableTracing) {
		this.enableTracing = enableTracing;
	}
	/**
	 * Is tracing enabled 
	 * @return true/false
	 */
	public final boolean isTracingEnabled() {
		return enableTracing;
	}
	/*
	 * Tracing messages go through here
	 */
	protected final void trace(String function, String message) {
		if(isTracingEnabled()) {
			Logger.print(TRACING_LOG_LEVEL, this + (function != null ? "." + function : "") + (message != null ? ": " + message : ""));
		}
	}
	/*
	 * Tracing messages go through here
	 */
	protected final void traceFormatted(String function, String format, Object...parameters) {
		trace(function, String.format(format, parameters));
	}
	/**
	 * Generate tracing messages for started here
	 */
	protected void entered(String function) {
		trace(function, "started");
	}
	/**
	 * Generate tracing messages for return values here
	 */
	protected void returning(String function) {
		trace(function, "returning");
	}
	/**
	 * Generate tracing messages for return values here
	 */
	protected void returning(String function, Object result) {
		trace(function, "returning " + result);
	}
	/**
	 * NB: We invoke the superclass after calling the subclass's implementation.
	 * super.interrupted() will call end(), so any user hooks for interrupted-only can go in interruptedImpl and common stuff in endImpl.
	 */
	@Override
	protected final void end() {
		entered("end");
		endImpl();
		super.end(); 
		returning("end");
	}
	/**
	 * NB: We invoke the superclass after calling the subclass's implementation.
	 * super.interrupted() will call end(), so any user hooks for interrupted-only can go in interruptedImpl and common stuff in endImpl.
	 */
	@Override
	protected final void interrupted() {
		entered("interrupted");
		interruptedImpl();
		super.interrupted();
		returning("interrupted");
	}

	/**
	 * For timed execution mode, our default behavior is to return true if the timer has expired.
	 * For asynchronous execution mode, our default behavior is to return true (execution in initialize like instant command)
	 * For synchronous execution mode, our default behavior is to return false.
	 */
	@Override
	protected final boolean isFinished() {
		boolean isFinished = false;
		// Trace first
		entered("isFinished");
		if(!isFinished && duration.isForever()) {
			trace("isFinished", "command is in running forever mode.");
			isFinished = false;
		} else if(getDuration().isAsynchronous()) {
			traceFormatted("isFinished", "returning true.  Duration set to Asynchronous.");
			return true;
		} else if(!isFinished && duration.isTimeout() && isTimedOut()) {
			trace("isFinished", "command failed with timeout error.");
			isFinished = true;
		} else if(!isFinished && duration.isTimer() && isTimedOut()) {
			trace("isFinished", "timed command completed, ran for " + duration.getTime() + ".");
			isFinished = true;
		} else if(!isFinished && isTimedOut()) {
			Logger.error(this + " WARNING: Someone is messing with timer.  If the timer is set, the duration should be either Timer or Timeout.");
			isFinished = true;
		} else if(!isFinished && isFinishedImpl()) {
			trace("isFinished", "subclass says it's finished.");
			isFinished = true;
		} else if(!isFinished && duration.isTimer()) {
			trace("isFinished", "timed command not finished yet.");
			isFinished = false;
		} else {
			trace("isFinished", "still waiting for command to complete.");
			isFinished = false;
		}
		returning("isFinished", isFinished);
		return isFinished;
	}

	/**
	 * By default, we pass through to the superclass but we trace that we were here.
	 */
	// @Override
	protected final void execute() {
		entered("execute");
		executeImpl();
		returning("execute");
	}
	/**
	 * By default, we pass through to the superclass but we trace that we were here.
	 */
	@Override
	protected final void initialize() {
		setName(toString());
		entered("initialize");
		initializeImpl();
		returning("initialize");
	}
	/**
	 * Build a formatted representation of the command, by calling getArgs().
	 * Currently we don't have a way to automate the capture of command arguments
	 * but we'll make one anyway.
	 */
	public final String toString() {
		return Formatter.formatConstructor(getClass(), args);
	}
	/**
	 * Add an arg to the list 
	 */
	protected final void addArg(String name, Object value) {
		args.add(name);
		args.add(value);
	}
	/**
	 * Concrete subclass may implement this method
	 * @see initialize
	 */
	protected void initializeImpl() { }
	/**
	 * Concrete subclass may implement this method
	 * @see execute
	 */
	protected void executeImpl() { }
	/**
	 * Concrete subclass may implement this method
	 * @see cancel
	 */
	protected void cancelImpl() { }
	/**
	 * Concrete subclass may implement this method
	 * @see cancel
	 */
	protected void endImpl() { }
	/**
	 * Concrete subclass may implement this method
	 * @see interrupted
	 */
	protected void interruptedImpl() { }
	/**
	 * Concrete subclass must implement this method.
	 * @see isFinished
	 */
	protected abstract boolean isFinishedImpl();
	/**
	 * If there is a timeout associated with the command, return it here.
	 * @see TimedCommand
	 */
	protected void setTimeout(Time timeout) {
		if(timeout == null) {
			super.setTimeout(-1);
		} else {
			super.setTimeout(timeout.convertTo(TimeUOM.Seconds).getValue());
		}
	}
}
