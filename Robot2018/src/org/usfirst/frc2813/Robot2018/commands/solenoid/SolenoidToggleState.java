// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Open/Close the Robot arm jaws, depending on the current state
 */
public class SolenoidToggleState extends InstantCommand {
	private final Solenoid solenoid;

    public SolenoidToggleState(Solenoid solenoid) {
    	this.solenoid = solenoid;
    }

	//@Override
	protected void initialize() {
		super.initialize();
		Direction oldState = solenoid.getState();
		Direction newState = oldState.getInverse();
		Logger.info(this + " toggling " + solenoid + " from " + oldState + " to " + newState);
		solenoid.setState(newState);
	}

    public String toString() {
        return "SolenoidToggleState(" + solenoid + ")";
    }
}
