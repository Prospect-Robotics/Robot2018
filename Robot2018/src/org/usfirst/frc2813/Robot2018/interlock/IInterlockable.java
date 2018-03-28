package org.usfirst.frc2813.Robot2018.interlock;

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
