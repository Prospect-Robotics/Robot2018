package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class ToggleIntake extends GearheadsInstantCommand {
	private final Direction direction;
	public ToggleIntake(Direction direction) {
		this.direction = direction;
		requires(Robot.intake);
	}

	@Override
	protected void initialize() {
		Robot.intake.spin(direction);
	}
}
