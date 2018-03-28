package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;
import org.usfirst.frc2813.units.Direction;

/*
 * Ratchet configuration
 */
public class RatchetConfiguration extends SolenoidConfiguration {
	/*
	 * Configuration of the Jaws subsystem
	 */
	public RatchetConfiguration() {
		super(
				"Ratchet", 
				SolenoidLogic.SolenoidLogicNormal, // NB: If Jack reverses solenoid polarity, this is where you fix the code.
				SolenoidType.SingleSolenoid, 
				Direction.DISENGAGED,
				null);
	}
}
