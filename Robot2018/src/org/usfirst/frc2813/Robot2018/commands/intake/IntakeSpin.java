package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class IntakeSpin extends SubsystemCommand<Intake> {
	private final Direction direction;

    public IntakeSpin(Intake intake, Direction direction, CommandDuration duration, Lockout lockout) {
    	super(intake, duration, lockout);
		this.direction = direction;
		addArg("direction", direction);
		setName(toString());
    }
    public IntakeSpin(Intake intake, Direction direction, CommandDuration duration) {
    	this(intake, direction, duration, Lockout.Disabled);
    }

    public IntakeSpin(Intake intake, Direction direction) {
    	this(intake, direction, CommandDuration.DISABLED);
    }

	@Override
	public boolean isSubsystemRequired() {
		return true;
	}

	@Override
	protected void subsystemInitializeImpl() {
		if(subsystem.getTargetDirection().equals(direction)) {
			traceFormatted("initialize", "NOT telling %s to spin %s, but it already is.", subsystem, direction);
		} else {
			traceFormatted("initialize", "telling %s to spin %s.", subsystem, direction);
			subsystem.spin(direction);
		}
	}

	@Override
	protected boolean subsystemIsFinishedImpl() {
		return false;
	}

	@Override
	protected void interruptedWhileWaitingImpl() {
		traceFormatted("interrupted", "telling %s to stop.", subsystem);
		subsystem.stop();
	}
}
