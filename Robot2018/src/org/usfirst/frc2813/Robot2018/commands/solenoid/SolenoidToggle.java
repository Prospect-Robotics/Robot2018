// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.units.Direction;

/**
 * Toggle the state of the Solenoid.  
 * Requires the Solenoid.
 * Instant command.
 */
public class SolenoidToggle extends SubsystemCommand<Solenoid> {
	
    public SolenoidToggle(Solenoid solenoid, CommandDuration duration, Lockout lockout) {
    	super(solenoid, duration, lockout);
    }

    public SolenoidToggle(Solenoid solenoid) {
    	this(solenoid, CommandDuration.DISABLED, Lockout.Disabled);
    }

	//@Override
	protected void subsystemInitializeImpl() {
		Direction oldPosition = subsystem.getCurrentPosition();
		Direction newPosition = oldPosition.getInverse();
		traceFormatted("intialize", "toggling %s from %s to %s", subsystem, oldPosition, newPosition);
		subsystem.setTargetPosition(newPosition);
	}

	/**
	 * @return true
	 */
	@Override
	public boolean isSubsystemRequired() {
		return true;
	}

	/**
	 * @return true
	 */
	@Override
	protected boolean subsystemIsFinishedImpl() {
		return true;
	}
}
