// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Open/Close the Robot arm jaws
 */
public class SolenoidSetState extends InstantCommand {
	private final Solenoid solenoid;
	private final Direction state;

    public SolenoidSetState(Solenoid solenoid, Direction state) {
    	this.solenoid = solenoid;
        this.state = state;
    }

	//@Override
	protected void initialize() {
		super.initialize();
		if(solenoid.getState() == state) {
			Logger.info(this + " NOT changing " + solenoid + " from " + solenoid.getState() + " to " + state + ", it's already " + state);
		} else {
			Logger.info(this + " changing " + solenoid + " from " + solenoid.getState() + " to " + state);
			solenoid.setState(state);
		}
	}

    public String toString() {
        return "SolenoidSetState(" + solenoid + ", " + state + ")";
    }
}
