package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.logging.Logger;
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
		super.initialize();
		if(intake.getTargetDirection().equals(direction)) {
			if(!isDefaultCommand()) {
				Logger.warning("Telling " + intake + " to spin " + direction + ", but it already is.");
			}
		} else {
			Logger.warning("Telling " + intake + " to spin " + direction + ".");
			intake.spin(direction);
		}
	}
}
