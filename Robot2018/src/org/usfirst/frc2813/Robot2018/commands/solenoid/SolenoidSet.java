// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.units.Direction;

/**
 * Change the solenoid state to a particular value.
 * Requires the subsystem.
 */
public final class SolenoidSet extends SubsystemCommand<Solenoid> {
	private final Direction position;
	/**
	 * Change the Solenoid state to a particular value.
	 * If lockout is enabled, waits forever and prevents  
	 */
    public SolenoidSet(Solenoid solenoid, Direction position, RunningInstructions duration, Lockout lockout) {
    	super(solenoid, duration, lockout);
        this.position = position;
        addArg("position", position);
    }
	/**
	 * Change the Solenoid state to a particular value.
	 * If lockout is enabled, waits forever and prevents  
	 */
    public SolenoidSet(Solenoid solenoid, Direction position) {
    	this(solenoid, position, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
    }
	//@Override
	protected void ghscInitialize() {
		if(subsystem.getCurrentPosition() == position) {
			traceFormatted("initialize","NOT changing %2$s from %3$s to %4$s, it's already %4$s",subsystem,subsystem.getCurrentPosition(),position);
		} else {
			traceFormatted("initialize","changing %s from %s to %s",subsystem,subsystem.getCurrentPosition(),position);
			subsystem.setTargetPosition(position);
		}
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	/**
	 * Instant command
	 */
	@Override
	protected boolean ghscIsFinished() {
		return true;
	}
}
