package org.usfirst.frc2813.Robot2018.commands.subsystem;

import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set the default command for a subsystem. Do not require exclusive access to the subsystem.
 */
public class SubsystemSetDefaultCommand extends SubsystemCommand<GearheadsSubsystem> {
	private final Command defaultCommand;

	public SubsystemSetDefaultCommand(GearheadsSubsystem subsystem, Command defaultCommand) {
		super(subsystem);
		this.defaultCommand = defaultCommand;
		setName(toString());
		addArg("defaultCommand", defaultCommand);
	}

	@Override
	protected void ghscInitialize() {
		if(subsystem.getDefaultCommand() != defaultCommand) {
			actionFormatted("initialize","setting default command for %s to %s.", subsystem, defaultCommand);
			subsystem.setDefaultCommand(defaultCommand);
		} else {
			actionFormatted("initialize","doesn't need to set default command for %s, as it's already correct.", subsystem, defaultCommand);
		}
	}

	/**
	 * Returns false, we don't need exclusive access
	 */
	@Override
	public boolean ghscIsSubsystemRequired() {
		return false;
	}

	/**
	 * return true, it's an instant command
	 */
	@Override
	protected boolean ghscIsFinished() {
		return true;
	}
}
