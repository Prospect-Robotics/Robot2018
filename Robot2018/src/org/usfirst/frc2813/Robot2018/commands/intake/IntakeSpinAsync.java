package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.Intake;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class IntakeSpinAsync extends AbstractIntakeInstantCommand {
	private final Direction direction;
	public IntakeSpinAsync(Intake subsystem, Direction direction) {
		super(subsystem, true /* require intake */);
		this.direction = direction;
	}

	@Override
	protected void initialize() {
		intake.spin(direction);
	}
}
