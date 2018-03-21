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
public class SolenoidSetStateInstant extends InstantCommand {
	private final Solenoid solenoid;
	private final Direction position;
	private final boolean isDefaultCommand;

    public SolenoidSetStateInstant(Solenoid solenoid, Direction position, boolean isDefaultCommand) {
    	this.solenoid = solenoid;
        this.position = position;
        this.isDefaultCommand = isDefaultCommand;
    }

    public SolenoidSetStateInstant(Solenoid solenoid, Direction position) {
    	this(solenoid, position, false);
    }

	//@Override
	protected void initialize() {
		super.initialize();
		if(solenoid.getPosition() == position) {
			// NB: Do not be noisy if it's the default command
			if(!isDefaultCommand) {
				Logger.printFormat(LogType.INFO,"%1$s NOT changing %2$s from %3$s to %4$s, it's already %4$s",this,solenoid,solenoid.getPosition(),position);
			}
		} else {
			Logger.printFormat(LogType.INFO,"%s changing %s from %s to %s",this,solenoid,solenoid.getPosition(),position);
			solenoid.setPosition(position);
		}
	}

    public String toString() {
        return "SolenoidSetState(" + solenoid + ", position=" + position + ")";
    }
}
