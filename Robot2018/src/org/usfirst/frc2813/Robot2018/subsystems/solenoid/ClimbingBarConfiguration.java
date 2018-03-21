package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;
import org.usfirst.frc2813.units.Direction;

public class ClimbingBarConfiguration extends SolenoidConfiguration {
	/*
	 * Configuration of the Jaws subsystem
	 */
	public ClimbingBarConfiguration() {
		super("ClimbingBar", 
				SolenoidLogic.SolenoidLogicNormal, 
				SolenoidType.SingleSolenoid,
				Direction.IN,
				null);
	}
}
