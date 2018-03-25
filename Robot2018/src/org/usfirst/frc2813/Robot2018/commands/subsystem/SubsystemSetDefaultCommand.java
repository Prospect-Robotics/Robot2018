package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 */
public final class SubsystemSetDefaultCommand extends GearheadsInstantCommand {
	private final Subsystem subsystem;
	private final Command defaultCommand;

	public SubsystemSetDefaultCommand(Subsystem subsystem, Command defaultCommand) {
		this.subsystem = subsystem;
		this.defaultCommand = defaultCommand;
		requires(this.subsystem); // Interrupt any running commands?
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(subsystem.getDefaultCommand() != defaultCommand) {
			Logger.printFormat(LogType.INFO,"%s setting default command for %s to %s.", this, subsystem, defaultCommand);
			subsystem.setDefaultCommand(defaultCommand);
		} else {
			Logger.printFormat(LogType.INFO,"%s doesn't need to set default command for %s, as it's already correct.", this, subsystem, defaultCommand);
		}
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + subsystem + ")";
    }
}
