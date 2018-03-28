package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class IntakeStopInstant extends AbstractIntakeInstantCommand {
	public IntakeStopInstant(Intake subsystem) {
		super(subsystem, true /* require intake */);
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(intake.getCurrentDirection().equals(Direction.IDLE)) {
			if(!isDefaultCommand()) {
				Logger.warning("Telling " + intake + " to stop, but it already is.");
			}
		} else {
			intake.spin(Direction.IDLE);
		}
	}
}
