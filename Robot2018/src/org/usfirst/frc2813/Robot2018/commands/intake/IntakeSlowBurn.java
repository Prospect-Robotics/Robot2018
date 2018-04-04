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
public class IntakeSlowBurn extends SubsystemCommand<Intake> {
	private final Direction direction;
	private static final double SLOW_BURN_SPEED = 0.6;
	
    public IntakeSlowBurn(Intake intake, Direction direction, RunningInstructions duration, Lockout lockout) {
		super(intake, duration, lockout);
		this.direction = direction;
		addArg("direction", direction);
		setName(toString());
    }

    public IntakeSlowBurn(Intake intake, Direction direction, RunningInstructions duration) {
    	this(intake, direction, duration, Lockout.Disabled);
    }

    public IntakeSlowBurn(Intake intake, Direction direction) {
    	this(intake, direction, RunningInstructions.RUN_NORMALLY);
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected void ghscInitialize() {
		action("initialize", "starting slow spin");
		subsystem.spin(direction, SLOW_BURN_SPEED);
	}

	@Override
	protected boolean ghscIsFinished() {
		return false;
	}

	protected void ghscEnd() {
		action("initialize", "stopping slow spin");
		subsystem.stop();
	}
	@Override
	protected void ghscinterruptedWhileWaiting() {
		subsystem.stop();
	}
}
