package org.usfirst.frc2813.Robot2018.commands;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc2813.Robot2018.interlock.IInterlock;
import org.usfirst.frc2813.Robot2018.interlock.IInterlockable;
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
public abstract class GearheadsCommand extends Command implements IInterlockable {
	private final RunningInstructions runningInstructions;
	/**
	 * If TRACING_LOG_LEVEL is within our current log level for the system, we will trace out when all the core Command functions are executing.
	 * This is on by default for all command instances, but can be disabled object-by-object with setTracingEnabled. 
	 */
	private boolean enableTracing = true;
	/**
	 * This log level controls the logging of which of the main 
	 * high level functions being called on command, i.e. "in end', "in isfinished" etc... 
	 */
	private static final LogType TRACING_LOG_LEVEL = LogType.DEBUG;
	/**
	 * We will populate this array with the key/value pairs of arguments for formatting into a nice pretty name.
	 * @see addArg
	 */
	private final ArrayList<Object> args = new ArrayList<Object>();
	/**
	 * This is the list of interlocks to check before performing the command
	 */
	private final List<IInterlock> interlocks = new ArrayList<IInterlock>();
	/**
	 * Create a new command with the specified type of duration and timer value
	 */
	protected GearheadsCommand(RunningInstructions duration) {
		this.runningInstructions = duration != null ? duration : new RunningInstructions();
		// Do not log implicit duration
		if(duration != null) {
			addArg("duration",duration);
		}
		if(this.runningInstructions.getTime() != null) {
			setTimeout(this.runningInstructions.getTime());
		}
	}
	/**
	 * Add an interlock to the subsystem.  Will not add duplicates.  
	 * Do not expect reference counting.
	 */
	public final void addInterlock(IInterlock interlock) {
		if(!interlocks.contains(interlock)) {
			interlocks.add(interlock);
		}
	}
	/**
	 * Remove the interlock from the subsystem.
	 * Do not expect reference counting.
	 */
	public final void removeInterlock(IInterlock interlock) {
		interlocks.remove(interlock);
	}
	/**
	 * Check the status of all interlocks to see if it's safe
	 */
	public final boolean isSafeToOperate() {
		for(IInterlock interlock : interlocks) {
			if(!interlock.isSafeToOperate())
				return false;
		}
		return true;
	}
	/**
	 * Get the duration function of the command, if there's a rule about time
	 */
	public final RunningInstructions getRunningInstructions() {
		return runningInstructions;
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
		if(!isSafeToOperate()) {
			Logger.error(this + ": INTERLOCK PREVENTED INTERRUPTED OPERATION.");
		} else {
			ghcEnd();
		}
		super.end(); // NB: This is safe. 
		returning("end");
	}
	/**
	 * NB: We invoke the superclass after calling the subclass's implementation.
	 * super.interrupted() will call end(), so any user hooks for interrupted-only can go in interruptedImpl and common stuff in endImpl.
	 */
	@Override
	protected final void interrupted() {
		entered("interrupted");
		if(!isSafeToOperate()) {
			Logger.error(this + ": INTERLOCK PREVENTED INTERRUPTED OPERATION.");
		} else {
			ghcInterrupted();
		}
		super.interrupted(); // NB: This is safe.
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
		if(!isSafeToOperate()) {
			Logger.error(this + ": INTERLOCK PREVENTED ISFINISHED OPERATION. RETURN TRUE.");
			isFinished = true;
		} else if(!isFinished && runningInstructions.isForever()) {
			trace("isFinished", "command is in running forever mode.");
			isFinished = false;
		} else if(getRunningInstructions().isAsynchronous()) {
			traceFormatted("isFinished", "returning true.  Duration set to Asynchronous.");
			return true;
		} else if(!isFinished && runningInstructions.isTimeout() && isTimedOut()) {
			trace("isFinished", "command failed with timeout error.");
			isFinished = true;
		} else if(!isFinished && runningInstructions.isTimer() && isTimedOut()) {
			trace("isFinished", "timed command completed, ran for " + runningInstructions.getTime() + ".");
			isFinished = true;
		} else if(!isFinished && isTimedOut()) {
			Logger.error(this + " WARNING: Someone is messing with timer.  If the timer is set, the duration should be either Timer or Timeout.");
			isFinished = true;
		} else if(!isFinished && ghcIsFinished()) {
			trace("isFinished", "subclass says it's finished.");
			isFinished = true;
		} else if(!isFinished && runningInstructions.isTimer()) {
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
		if(!isSafeToOperate()) {
			Logger.error(this + ": INTERLOCK PREVENTED EXECUTE OPERATION.");
		} else {
			ghcExecute();
		}
		returning("execute");
	}
	/**
	 * By default, we pass through to the superclass but we trace that we were here.
	 */
	@Override
	protected final void initialize() {
		setName(toString());
		entered("initialize");
		if(!isSafeToOperate()) {
			Logger.error(this + ": INTERLOCK PREVENTED INITIALIZE OPERATION.");
		} else {
			ghcInitialize();
		}
		returning("initialize");
	}
	/**
	 * Build a formatted representation of the command, by calling getArgs().
	 * Currently we don't have a way to automate the capture of command arguments
	 * but we'll make one anyway.
	 */
	public String toString() {
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
	protected void ghcInitialize() { }
	/**
	 * Concrete subclass may implement this method
	 * @see execute
	 */
	protected void ghcExecute() { }
	/**
	 * Concrete subclass may implement this method
	 * @see cancel
	 */
	protected void ghcEnd() { }
	/**
	 * Concrete subclass may implement this method
	 * @see interrupted
	 */
	protected void ghcInterrupted() { }
	/**
	 * Concrete subclass must implement this method.
	 * @see isFinished
	 */
	protected abstract boolean ghcIsFinished();
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
