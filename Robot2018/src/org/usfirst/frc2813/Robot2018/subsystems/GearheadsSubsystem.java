package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsSubsystem extends Subsystem {
	static {
		Logger.addMe();
	}
	private boolean _isEmulated = false;
	private boolean lockout = false;
	public boolean encoderFunctional = true;

	/**
	 * Is the subsystem emulated?
	 * @return true/false
	 */
	public boolean isEmulated() {
		return _isEmulated;
	}

	/**
	 * enable emulation mode. Override to initialize emulation state
	 */
	public void enableEmulator() {
		_isEmulated = true;
	}

	/**
	 * disable emulation mode
	 */
	public void disableEmulator() {
		_isEmulated = false;
	}
	
	/**
	 * Short cut for determining if the robot is enabled.  I need this
	 * @return true if the robot is enabled. 
	 */
	public boolean isRobotEnabled() {
		return DriverStation.getInstance().isEnabled();
	}
	private Command lockedBy = null;	
	/**
	 * Lockout all operations on the subsystem
	 */
	public void lock(Command lockedBy) {
		if(this.lockedBy != null && this.lockedBy != lockedBy) {
			throw new RuntimeException("The " + this + " subsystem has already been locked by " + this.lockedBy + " and cannot be locked by " + lockedBy);
		}
		if(getCurrentCommand() != lockedBy) {
			throw new RuntimeException("The " + this + " subsystem can only be locked by the current command.");
		}
		this.lockedBy = lockedBy;
	}
	/**
	 * Lockout all operations on the subsystem
	 */
	public void unlock(Command lockedBy) {
		if(getCurrentCommand() != lockedBy) {
			throw new RuntimeException("The " + this + " subsystem can only be unlocked by the current command.");
		}
		if(this.lockedBy == lockedBy) {
			this.lockedBy = null;
		} else if(this.lockedBy != null) {
			throw new RuntimeException("The " + this + " subsystem has already been locked by " + this.lockedBy + " and cannot be unlocked by " + lockedBy);
		} else {
			throw new RuntimeException("The " + this + " subsystem is not locked.");
		}
	}
	/**
	 * Is this subsystem supposed to stop, because we need to lockout other commands
	 * @return
	 */
	public boolean isLocked() {
		return this.lockout;
	}
	
	/**
	 * Do not allow re-queuing default command if we're locked.
	 */
	@Override
	public Command getDefaultCommand() {
		if(isLocked()) {
			return null;
		}
		return super.getDefaultCommand();
	}
	
}
