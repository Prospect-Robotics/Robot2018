package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;

/*
 * NB: We considered having a default command for the jaws to open, so that if you weren't running
 * a "holding", "intaking", or "outtaking" command that they would spring back open again - 
 * such as automatically springing open when the outtake was done...but decided the potential to
 * lose a cube due to a software bug was too greate. 
 */
public class JawsConfiguration extends SolenoidConfiguration {

	/*
	 * Configuration of the Jaws subsystem
	 */
	public JawsConfiguration() {
		super(
				"Jaws", 
				SolenoidLogic.SolenoidLogicNormal, 
				SolenoidType.SingleSolenoid, 
				null /* defaultCommandFactory */
				);
	}

}
