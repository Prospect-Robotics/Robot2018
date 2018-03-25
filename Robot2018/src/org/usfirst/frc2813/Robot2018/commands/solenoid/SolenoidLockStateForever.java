// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsInstantCommand;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Open/Close the Robot arm jaws
 */
public class SolenoidLockStateForever extends GearheadsInstantCommand {
	private final Solenoid solenoid;
	private final Direction position;

    public SolenoidLockStateForever(Solenoid solenoid, Direction position) {
    	this.solenoid = solenoid;
        this.position = position;
    }

	//@Override
	protected void initialize() {
		super.initialize();
		Logger.printFormat(LogType.INFO,"%s setting as default command for %s.",this, solenoid);
		solenoid.setDefaultCommand(this);
		Logger.printFormat(LogType.INFO,"%s setting to not interruptable.",this);
		this.setInterruptible(false);
		Logger.printFormat(LogType.INFO,"%s setting %s to %s state until canceled.",this,solenoid,position);
		solenoid.setTargetPosition(position);
		
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + solenoid + ", position=" + position + ")";
    }
    
    public boolean isFinished() {
    	return false;
    }
}
