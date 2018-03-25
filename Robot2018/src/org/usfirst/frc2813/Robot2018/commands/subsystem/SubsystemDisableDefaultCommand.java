package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Wait for a motor system to arrive at position, to within +/- allowableError
 */
public final class SubsystemDisableDefaultCommand extends GearheadsInstantCommand {
	private final Subsystem subsystem;

	public SubsystemDisableDefaultCommand(Subsystem subsystem) {
		this.subsystem = subsystem;
		requires(this.subsystem);
		// Interrupt any existing command
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(subsystem.getDefaultCommand() != null) {
			Logger.printFormat(LogType.INFO,"%s removing default command for %s.", this, subsystem);
			subsystem.setDefaultCommand(null);
		} else {
			Logger.printFormat(LogType.INFO,"%s NOT removing default command for %s, it didn't have one.", this, subsystem);
		}
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + subsystem + ")";
    }
}
