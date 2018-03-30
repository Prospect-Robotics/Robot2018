package org.usfirst.frc2813.Robot2018.commands.subsystem;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.TargetedCommand;
import org.usfirst.frc2813.Robot2018.interlock.IInterlock;
import org.usfirst.frc2813.Robot2018.interlock.IInterlockable;
import org.usfirst.frc2813.Robot2018.interlock.LOCKED;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command targets a subsystem and knows how to require the resource (optionally) and handle locking/exclusivity of the resource. 
 * 'require' the target subsystem in the constructor
 */
public abstract class SubsystemCommand<SUBSYSTEM_TYPE extends GearheadsSubsystem> extends TargetedCommand<SUBSYSTEM_TYPE> implements IInterlock,IInterlockable {
	protected final SUBSYSTEM_TYPE subsystem;	
	protected final Lockout lockout;
	private boolean subsystemLocked = false;
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
			this.setInterruptible(false);
		}
		// Call the subclass if it's safe to do so
		if(isSafeToOperate())
			ghscInitialize();
	}

	/**
	 * When we end, we will handle unlocking here 
	 */
	@Override
	protected final void ghcEnd() {
		// Call the subclass if it's safe to do so
		if(isSafeToOperate())
			ghscEnd();
	}

	@Override
	protected final void ghcInterrupted() {
		// Call the subclass if it's safe to do so
		if(isSafeToOperate())
			ghscInterrupted();
		// A hook for shutting down that's conditional
		if(true) { // XXX this should check if the command is asynchronous.
			traceFormatted("interrupted", "interrupted while waiting, calling interruptedWhileWaiting.");
			if(isSafeToOperate())
				ghscinterruptedWhileWaiting();
		}
		/*
		 * If we weren't merely trying to lock out other commands while running this one,
		 * we will then lock the subsystem against subsequent commands.
		 */
		if(lockout.isUntilUnlocked()) {
			traceFormatted("lock","locking subsystem %s", subsystem);
			subsystem.addInterlock(LOCKED.ALWAYS);
		}
		/* At this point, "unlocking" the command is always safe, we're done and don't carea bout 
		 * interruptible anymore.
		 */
		this.setInterruptible(true);
	}

	@Override
	protected final void ghcExecute() {
		if(isSafeToOperate())
			ghscExecute();
	}

	@Override
	protected final boolean ghcIsFinished() {
		return ghscIsFinished();
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
