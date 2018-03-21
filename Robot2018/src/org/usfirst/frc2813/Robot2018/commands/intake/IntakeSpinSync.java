package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Spin the Intake IN or OUT, while held.   Stop when interrupted.
 */
public class IntakeSpinSync extends AbstractIntakeCommand {
	private final Direction direction;
	public IntakeSpinSync(Intake intake, Direction direction) {
		super(intake, true /* require it */);
		this.direction = direction;
		setName(toString());
	}

	// We only need to call this once
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

	// We never finish until we are interrupted
	protected boolean isFinished() {
		return false;
	}

	// Called when we're interrupted
	protected void interrupted() {
		super.interrupted();		
		intake.stop();
	}

	public String toString() {
		return getClass().getSimpleName() + "(" + intake + ", direction=" + direction + ")";
	}
}
