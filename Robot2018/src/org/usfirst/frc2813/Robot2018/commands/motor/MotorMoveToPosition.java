package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
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
		if(motor.getState().getOperation() == MotorOperation.MOVING_TO_POSITION && motor.getPosition() == position) {
			Logger.info("NOT telling " + motor + " to move to " + position + ", it's already doing that.");
		} else {
			Logger.info("Telling " + motor + " to move to " + position + ".");
			motor.moveToPosition(position);
		}
	}
}
