package org.usfirst.frc2813.Robot2018.commands.interlock;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.interlock.IInterlockable;
import org.usfirst.frc2813.Robot2018.interlock.LOCKED;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Disable the default command on a subsystem.  Do not require the exclusive access.
 */
public class Lock extends GearheadsCommand {
	private final IInterlockable target;
	
	public Lock(IInterlockable target) {
		super(CommandDuration.DISABLED);
		this.target = target;
		setName(toString());
	}

	@Override
	protected void ghcInitialize() {
		traceFormatted("initialize","locking %s.", target);
		target.addInterlock(LOCKED.ALWAYS);
	}

	/**
	 * return true, it's an instant command
	 */
	@Override
	protected boolean ghcIsFinished() {
		return true;
	}
}
