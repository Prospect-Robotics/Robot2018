package org.usfirst.frc2813.Robot2018.interlock;

/**
 * Unconditional interlock for locking out individual commands or subsystems...
 * @author mike.taylor
 */
public class LOCKED implements IInterlock {

	/**
	 * Use this value for anytime you need to stop something with an interlock unconditionally
	 */
	public static final IInterlock ALWAYS = new LOCKED();
	
	private LOCKED() {}

	@Override
	public boolean isSafeToOperate() {
		return false;
	}
}
