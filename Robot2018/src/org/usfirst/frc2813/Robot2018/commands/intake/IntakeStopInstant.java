package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.subsystems.Intake;
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
		intake.spin(Direction.STOP);
	}
}
