// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Open/Close the Robot arm jaws, depending on the current state
 */
public class SolenoidToggleStateInstant extends GearheadsInstantCommand {
	private final Solenoid solenoid;

    public SolenoidToggleStateInstant(Solenoid solenoid) {
    	this.solenoid = solenoid;
    }

	//@Override
	protected void initialize() {
		super.initialize();
		Direction oldPosition = solenoid.getCurrentPosition();
		Direction newPosition = oldPosition.getInverse();
		Logger.info(this + " toggling " + solenoid + " from " + oldPosition + " to " + newPosition);
		solenoid.setTargetPosition(newPosition);
	}

    public String toString() {
        return "SolenoidToggleState(" + solenoid + ")";
    }
}
