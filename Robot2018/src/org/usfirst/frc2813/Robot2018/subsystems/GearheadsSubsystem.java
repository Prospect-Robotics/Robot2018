package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc2813.Robot2018.interlock.IInterlock;
import org.usfirst.frc2813.Robot2018.interlock.IInterlockable;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * This class contains code common to all Subsystems
 */
public abstract class GearheadsSubsystem extends Subsystem implements IInterlockable {
	static {
		Logger.addMe();
	}
	private boolean _isEmulated = false;
	private boolean lockout = false;
	public boolean encoderFunctional = true;
	private final List<IInterlock> interlocks = new ArrayList<IInterlock>();

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
	public final boolean isRobotEnabled() {
		return DriverStation.getInstance().isEnabled();
	}
	/**
	 * Add an interlock to the subsystem.  Will not add duplicates.  
	 * Do not expect reference counting.
	 */
	public final void addInterlock(IInterlock interlock) {
		if(!interlocks.contains(interlock)) {
			interlocks.add(interlock);
		}
	}
	/**
	 * Remove an interlock from the subsystem
	 * Do not expect reference counting.
	 */
	public final void removeInterlock(IInterlock interlock) {
		interlocks.remove(interlock);
	}
	/**
	 * Check the status of all interlocks to see if it's safe
	 */
	public final boolean isSafeToOperate() {
		for(IInterlock interlock : interlocks) {
			if(!interlock.isSafeToOperate())
				return false;
		}
		return true;
	}
	/**
	 * Do not allow re-queuing default command if we're locked.
	 */
	@Override
	public final Command getDefaultCommand() {
		if(!isSafeToOperate()) {
			Logger.warning(this + " could not getDefaultCommand because we are interlocked.");
			return null;
		}
		return super.getDefaultCommand();
	}

	/**
	 * Reset the default command (to be used after we disabled it for a while)
	 */
	public final void resetDefaultCommand() {
		if(isSafeToOperate()) {
			initDefaultCommand();
		} else {
			Logger.warning(this + " could not resetDefaultCommand because we are interlocked.");
		}
	}
	
}
