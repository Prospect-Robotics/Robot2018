package org.usfirst.frc2813.Robot2018.commands.subsystem;

import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set the default command for a subsystem. Do not require exclusive access to the subsystem.
 */
public class SubsystemRestoreDefaultCommand extends SubsystemCommand<GearheadsSubsystem> {

	public SubsystemRestoreDefaultCommand(GearheadsSubsystem subsystem) {
		super(subsystem);
		setName(toString());
	}

	@Override
	protected void ghscInitialize() {
		subsystem.resetDefaultCommand();
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
