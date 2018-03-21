package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetStateInstant;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;

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
				SolenoidLogic.SolenoidLogicNormal, 
				SolenoidType.SingleSolenoid, 
				Direction.DISENGAGED,
				null);
	}
}
