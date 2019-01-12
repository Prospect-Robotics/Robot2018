package org.usfirst.frc2813.Robot2018.commands.solenoid;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.TargetedCommand;
import org.usfirst.frc2813.units.Direction;

/**
 * Toggle a basic WPI solenoid state
 */
public final class WPISolenoidSet extends TargetedCommand<edu.wpi.first.wpilibj.Solenoid> {
	private final Direction position;
	/**
	 * Change the Solenoid state to a particular value.
	 * If lockout is enabled, waits forever and prevents  
	 */
    public WPISolenoidSet(edu.wpi.first.wpilibj.Solenoid solenoid, Direction position, RunningInstructions duration) {
    	super(solenoid, duration);
        this.position = position;
        addArg("position", position);
       	setName(toString());
    }
	/**
	 * Change the Solenoid state to a particular value.
	 * If lockout is enabled, waits forever and prevents  
	 */
    public WPISolenoidSet(edu.wpi.first.wpilibj.Solenoid solenoid, Direction position) {
    	this(solenoid, position, RunningInstructions.RUN_NORMALLY);
    }
	
	//@Override
	protected void ghcInitialize() {
        actionFormatted("initialize", "toggling %s to %b", target, position);
        target.set(position.isPositive());
	}
	
	// @Override
	public boolean ghcIsFinished() {
		return true;
	}
}

