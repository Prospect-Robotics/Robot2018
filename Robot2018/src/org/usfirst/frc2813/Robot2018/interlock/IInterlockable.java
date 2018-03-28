package org.usfirst.frc2813.Robot2018.interlock;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveInDirection;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Any object that can have safety interlocks will have this interface.
 * 
 * The idea is simple.  An interlockable device will maintain a list of 
 * IInterlock objects.  Before executing any operations, the 
 * IInterlockable object will iterate through all IInterlock objects
 * and call isSafeToOperate().  If any of them returns false, then
 * the IInterlockable object will not operate any actuators.
 *
 * The basic implementation of the interface should look like this:
 * 	
 * //Add an interlock to the subsystem.  Will not add duplicates.  
 * // Do not expect reference counting.
 * public final void addInterlock(IInterlock interlock) {
 * 	if(!interlocks.contains(interlock)) {
 * 		 interlocks.add(interlock);
 * 	}
 * }
 * 
 * // Remove the interlock from the subsystem.
 * public final void removeInterlock(IInterlock interlock) {
 * 	interlocks.remove(interlock);
 * }
 * 
 * // Check the status of all interlocks to see if it's safe
 * public final boolean isSafeToOperate() {
 * 	for(IInterlock interlock : interlocks) {
 * 		if(!interlock.isSafeToOperate())
 * 			return false;
 * 	}
 * 	return true;
 * }
 * 
 * Example:
 * 	
 *	 public Command createElevatorUp() {
 *		SubsystemCommand<Motor> elevatorUp = new MotorMoveInDirection(Robot.elevator, Direction.UP);
 *		// Add an interlock on elevatorUp so that it will not engage with the ratchet 
 *		elevatorUp.addInterlock(new IInterlock() {
 *			public boolean isSafeToOperate() {
 *				return Robot.ratchet.getTargetPosition().equals(Direction.DISENGAGED);
 *			}
 *		});
 *		return elevatorUp;
 *	 }
 *
 * @author mike.taylor
 */
public interface IInterlockable extends IInterlock {
	/**
	 * Register an interlock callback
	 * @param interlock An interlock to check before all operations
	 */
	public void addInterlock(IInterlock interlock);
	/**
	 * Unregister an interlock callback
	 * @param interlock An interlock to check before all operations
	 */
	public void removeInterlock(IInterlock interlock);
}
