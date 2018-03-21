package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;
import org.usfirst.frc2813.units.Direction;

/*
 * Gear shift solenoid configuration
 */
public class GearShiftConfiguration extends SolenoidConfiguration {
	/*
	 * Configuration of the Jaws subsystem
	 */
	public GearShiftConfiguration() {
		super("GearShifter", 
				SolenoidLogic.SolenoidLogicNormal, 
				SolenoidType.SingleSolenoid,
				Direction.LOW_GEAR,
				null);
	}
}
