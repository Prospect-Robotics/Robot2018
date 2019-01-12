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
	private final double targetOutputCurrentAmps;
	
    public IntakeSlowBurn(Intake intake, Direction direction, double targetOutputCurrentAmps, RunningInstructions duration, Lockout lockout) {
		super(intake, duration, lockout);
		this.direction = direction;
		this.targetOutputCurrentAmps = targetOutputCurrentAmps;
		addArg("direction", direction);
		addArg("targetOutputCurrentAmps", targetOutputCurrentAmps);
		setName(toString());
    }

    public IntakeSlowBurn(Intake intake, Direction direction, double targetOutputCurrentAmps, RunningInstructions duration) {
    	this(intake, direction, targetOutputCurrentAmps, duration, Lockout.Disabled);
    }

    public IntakeSlowBurn(Intake intake, Direction direction, double targetOutputCurrentAmps) {
    	this(intake, direction, targetOutputCurrentAmps, RunningInstructions.RUN_NORMALLY);
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected void ghscInitialize() {
		action("initialize", "starting slow " + direction + " spin @ " + targetOutputCurrentAmps + " amps.");
		subsystem.spinConstantCurrent(direction, targetOutputCurrentAmps);
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
