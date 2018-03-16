package org.usfirst.frc2813.Robot2018.commands.Motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move elevator to position. Motor controller does this without
 * further intervention.
 */
public class MotorMoveToPosition extends MotorInstantCommand {
	private final Length position;

	public MotorMoveToPosition(Motor motor, Length position) {
		super(motor, true);
		this.position = position;
	}

	@Override
	protected void initialize() {
		super.initialize();
		motor.moveToPosition(position);
	}
}
