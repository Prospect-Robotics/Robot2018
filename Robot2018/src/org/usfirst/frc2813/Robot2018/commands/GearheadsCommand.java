package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsCommand extends Command {
	
	private static final LogType TRACING_LOG_LEVEL = LogType.DEBUG;
	@Override
	protected void end() {
		Logger.print(TRACING_LOG_LEVEL, this + " in end.");
		super.end();
	}

	@Override
	public synchronized void cancel() {
		Logger.print(TRACING_LOG_LEVEL, this + " in cancel.");
		super.cancel();
	}

	private boolean isDefaultCommand = false;
	static {
		Logger.addMe();
	}

	public GearheadsCommand() {
		super();
	}

	public GearheadsCommand(String name) {
		super(name);
	}

	public GearheadsCommand(double timeout) {
		super(timeout);
	}

	public GearheadsCommand(String name, double timeout) {
		super(name, timeout);
	}

	protected boolean isFinished() {
		if(!isDefaultCommand()) {
			Logger.print(TRACING_LOG_LEVEL, this + " in isFinished");
		}
		return false;
	}

	// @Override
	protected void execute() {
		if(!isDefaultCommand()) {
			Logger.print(TRACING_LOG_LEVEL, this + " in execute");
		}
	}

	// @Override
	protected void initialize() {
		Logger.print(TRACING_LOG_LEVEL, this + " in initialize");
	}

	// @Override
	protected void interrupted() {
		Logger.print(TRACING_LOG_LEVEL, this + " interrupted");
	}
	
	public void setIsDefaultCommand(boolean isDefaultCommand) {
		this.isDefaultCommand = isDefaultCommand;
	}
	
	public boolean isDefaultCommand() {
		return isDefaultCommand;
	}
}
