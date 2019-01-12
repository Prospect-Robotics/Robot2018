package org.usfirst.frc2813.Robot2018.commands.solenoid;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.TargetedCommand;

/**
 * Toggle a basic WPI solenoid state
 */
public final class WPISolenoidToggle extends TargetedCommand<edu.wpi.first.wpilibj.Solenoid> {
	
    public WPISolenoidToggle(edu.wpi.first.wpilibj.Solenoid solenoid, RunningInstructions duration) {
    	super(solenoid, duration);
    	setName(toString());
    }
    
    public WPISolenoidToggle(edu.wpi.first.wpilibj.Solenoid solenoid) {
    	this(solenoid, RunningInstructions.RUN_NORMALLY);
    }

	//@Override
	protected void ghcInitialize() {
        boolean old = target.get();
        actionFormatted("intialize", "toggling %s from %b to %b", target, old, !old);
        target.set(!old);
	}
	
	// @Override
	public boolean ghcIsFinished() {
		return true;
	}
}

