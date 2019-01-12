package org.usfirst.frc2813.Robot2018.commands.subsystem;

import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;

/**
 * Disable the default command on a subsystem.  Do not require the exclusive access.
 */
public final class SubsystemDisableDefaultCommand extends SubsystemSetDefaultCommand {
	public SubsystemDisableDefaultCommand(GearheadsSubsystem subsystem) {
		super(subsystem, null /* defaultCommand */);
	}
}
