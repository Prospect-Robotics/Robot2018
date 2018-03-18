package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to a relative position. Motor controller does this without
 * further intervention.
 */
public class MotorMoveToRelativePosition extends MotorInstantCommand {
	private final Direction direction;
	private final Length relativeDistance;

	public MotorMoveToRelativePosition(Motor motor, Direction direction, Length relativeDistance) {
		super(motor, true);
		this.direction = direction;
		this.relativeDistance = relativeDistance;
	}

	@Override
	protected void initialize() {
		super.initialize();
		Logger.info(this + " telling " + motor + " to move " + direction + " by " + relativeDistance + ".");
		motor.moveToRelativePosition(direction, relativeDistance);
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + direction + "," + relativeDistance + ")";
    }
}
