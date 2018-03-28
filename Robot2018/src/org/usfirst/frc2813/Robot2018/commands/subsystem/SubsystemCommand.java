package org.usfirst.frc2813.Robot2018.commands.subsystem;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.TargetedCommand;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command targets a subsystem and knows how to require the resource (optionally) and handle locking/exclusivity of the resource. 
 * 'require' the target subsystem in the constructor
 */
public abstract class SubsystemCommand<SUBSYSTEM_TYPE extends GearheadsSubsystem> extends TargetedCommand<SUBSYSTEM_TYPE> {
	protected final SUBSYSTEM_TYPE subsystem;	
	protected final Lockout lockout;
	protected Command lastDefaultCommand;
	private boolean locked = false;
	/**
	 * Create a command that targets a subsystem.
	 * @param target the subsystem we are operating on
	 * @param lockout whether to prevent interruption and default command, while active
	 * @param duration can be Disabled, Timeout, Duration, Forever
	 */
	public SubsystemCommand(SUBSYSTEM_TYPE target, RunningInstructions duration, Lockout lockout) {
		super(target, duration);
		this.subsystem  = target;
		this.lockout = lockout;
		addArg("lockout", lockout);
		if(ghscIsSubsystemRequired()) {
			requires(target);
		}
		if(lockout.isWhileRunning() && !ghscIsSubsystemRequired()) {
			throw new IllegalArgumentException("You cannot lockout a subsystem unless you also 'require' the subsystem.  Fix isSubsystemRequired().");
		}
	}

	/**
	 * Create a command that targets a subsystem and has no special meaning for timer.
	 * @param target the subsystem we are operating on
	 * @param lockout whether to prevent interruption and default command
	 */
	public SubsystemCommand(SUBSYSTEM_TYPE target) {
		this(target, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
	}

	//@Override
	protected final void ghcInitialize() {
		// Handle lockout
		if(lockout.isEnabled()) {
			lock();
		}
		// Call the subclass
		ghscInitialize();
	}

	/**
	 * When we end, we will handle unlocking here 
	 */
	@Override
	protected final void ghcEnd() {
		// Call the subclass to get their deal
		ghscEnd();
	}

	@Override
	protected final void ghcInterrupted() {
		// A hook for shutting down
		ghscInterrupted();
		// A hook for shutting down that's conditional
		if(!getRunningInstructions().isAsynchronous()) {
			traceFormatted("interrupted", "interrupted while waiting, calling interruptedWhileWaiting.");
			ghscinterruptedWhileWaiting();
		}
		// Unlock the subsystem
		if(lockout.isWhileRunning()) {
			unlock();
		}
	}

	@Override
	protected final void ghcExecute() {
		ghscExecute();
	}

	@Override
	protected final boolean ghcIsFinished() {
		return ghscIsFinished();
	}

	protected void lock() {
		traceFormatted("lock","locking out %s", subsystem);
		subsystem.lock(this);
		this.locked = true;
		this.lastDefaultCommand = subsystem.getDefaultCommand();
		traceFormatted("lock", "setting as default command for %s.", subsystem);
		subsystem.setDefaultCommand(null);
		trace("lock","setting to not interruptable.");
		this.setInterruptible(false);
	}
	
	protected void unlock() {
		if(locked) {
			traceFormatted("unlock","unlocking %s", subsystem);
			// Now cleanup if necessary
			if(subsystem.getDefaultCommand() == null && lastDefaultCommand != null) {
				traceFormatted("unlock", "setting as default command for %s back to %s.", subsystem, lastDefaultCommand);
				subsystem.setDefaultCommand(lastDefaultCommand);
			}
			subsystem.unlock(this);
			this.locked = false;
		}
	}
	
	/**
	 * Is the subsystem "required"
	 * @see requires 
	 */
	public abstract boolean ghscIsSubsystemRequired();
	// For subclasses
	protected abstract void ghscInitialize();
	// For subclasses
	protected abstract boolean ghscIsFinished();
	// For subclasses
	protected void ghscEnd() {}
	// For subclasses
	protected void ghscExecute() {}
	// For subclasses - called on end or interrupted
	protected void ghscInterrupted() {}
	// For subclasses - called on end or interrupted, but only if we were in waiting mode and we set shutdownOnInterruption so we will safe on interruption.
	protected void ghscinterruptedWhileWaiting() {}
}
