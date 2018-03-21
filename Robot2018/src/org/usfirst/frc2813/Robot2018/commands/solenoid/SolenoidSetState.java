// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.LogType;
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
			Logger.printFormat(LogType.INFO,"%1$s NOT changing %2$s from %3$s to %4$s, it's already %4$s",this,solenoid,solenoid.getState(),state);
		} else {
			Logger.printFormat(LogType.INFO,"%s changing %s from %s to %s",this,solenoid,solenoid.getState(),state);
			solenoid.setState(state);
		}
	}

    public String toString() {
        return "SolenoidSetState(" + solenoid + ", state=" + state + ")";
    }
}
