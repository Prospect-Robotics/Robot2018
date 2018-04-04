package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class IntakeSpin extends SubsystemCommand<Intake> {
	private final Direction direction;

    public IntakeSpin(Intake intake, Direction direction, RunningInstructions duration, Lockout lockout) {
		super(intake, duration, lockout);
		Logger.debug(this + ": IntakeSpin (Direction: " + direction + ", Duration: " + duration + ", Lockout: "+ lockout + ")");
		this.direction = direction;
		addArg("direction", direction);
		setName(toString());
    }
    public IntakeSpin(Intake intake, Direction direction, RunningInstructions duration) {
    	this(intake, direction, duration, Lockout.Disabled);
    }

    public IntakeSpin(Intake intake, Direction direction) {
    	this(intake, direction, RunningInstructions.RUN_NORMALLY);
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected void ghscInitialize() {
		if(subsystem.getTargetDirection().equals(direction)) {
			actionFormatted("initialize", "NOT telling %s to spin %s, but it already is.", subsystem, direction);
		} else {
			actionFormatted("initialize", "telling %s to spin %s.", subsystem, direction);
			subsystem.spin(direction);
		}
	}

	@Override
	protected boolean ghscIsFinished() {
		return false;
	}

	@Override
	protected void ghscinterruptedWhileWaiting() {
		actionFormatted("interrupted", "telling %s to stop.", subsystem);
		subsystem.stop();
	}
}
