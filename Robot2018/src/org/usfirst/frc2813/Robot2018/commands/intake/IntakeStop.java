package org.usfirst.frc2813.Robot2018.commands.intake;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.units.Direction;

/**
 * This class sets the Intake in/out/off and exits
 */
public class IntakeStop extends SubsystemCommand<Intake> {
	
	public IntakeStop(Intake intake, CommandDuration duration, Lockout lockout) {
		super(intake, duration, lockout);
	}
	public IntakeStop(Intake intake, CommandDuration duration) {
		this(intake, duration, Lockout.Disabled);
	}
	public IntakeStop(Intake intake) {
		this(intake, CommandDuration.DISABLED);
	}

	@Override
	protected void ghscInitialize() {
		if(subsystem.getCurrentDirection().equals(Direction.STOP)) {
			trace("initialize", "Telling " + subsystem + " to stop, but it already is.");
		} else {
			subsystem.spin(Direction.STOP);
		}
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected boolean ghscIsFinished() {
		return true;
	}
}
