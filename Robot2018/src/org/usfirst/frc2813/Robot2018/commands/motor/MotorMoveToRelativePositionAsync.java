package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor to a relative position. Motor controller does this without
 * further intervention.
 */
public final class MotorMoveToRelativePositionAsync extends AbstractMotorInstantCommand {
	private final Direction direction;
	private final Length relativeDistance;

	public MotorMoveToRelativePositionAsync(Motor motor, Direction direction, Length relativeDistance) {
		super(motor, true);
		this.direction = direction;
		this.relativeDistance = relativeDistance;
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		Logger.printFormat(LogType.INFO,"%s telling %s to move %s by %s.",this,motor,direction,relativeDistance);
		motor.moveToRelativePosition(direction, relativeDistance);
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", direction=" + direction + ", relativeDistance=" + relativeDistance + ")";
    }
}
