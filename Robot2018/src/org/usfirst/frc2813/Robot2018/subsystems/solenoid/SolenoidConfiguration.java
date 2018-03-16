package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidType;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;

import edu.wpi.first.wpilibj.command.Command;

/*
 * Configuration for a solenoid.  What am I and is my logic reversed?
 */
public class SolenoidConfiguration {
	private final String name;
	private final SolenoidLogic solenoidLogic;
	private final SolenoidType solenoidType;
	private final ICommandFactory<Solenoid> defaultCommandFactory;
	
	/*
	 * Create a new configuration for a binary solenoid
	 */
	public SolenoidConfiguration(String name, SolenoidLogic solenoidLogic, SolenoidType solenoidType, ICommandFactory<Solenoid> defaultCommandFactory) {
		this.name = name;
		this.solenoidLogic = solenoidLogic;
		this.solenoidType = solenoidType;
		this.defaultCommandFactory = defaultCommandFactory;
	}
	/*
	 * What is the name
	 */
	public String getName() {
		return name;
	}
	/*
	 * Get the logic description of the solenoid (is the logic reversed) 
	 */
	public SolenoidLogic getSolenoidLogic() {
		return solenoidLogic;
	}
	/*
	 * Get the type of solenoid (single or double);
	 */
	public SolenoidType getSolenoidType() {
		return solenoidType;
	}
	/*
	 * Get the default command to run (may be null)
	 */
	public ICommandFactory<Solenoid> getDefaultCommandFactory() {
		return defaultCommandFactory;
	}
	/*
	 * Get a description of the configuration
	 */
	public String getDescription() {
		StringBuffer buf = new StringBuffer();
		buf
		.append("----------------------------------------------------------------------------\n")
		.append("                             Solenoid Configuration                             \n")
		.append("----------------------------------------------------------------------------\n")
		.append("Name.................................." + getName() + "\n")
		.append("SolenoidType.........................." + getSolenoidType() + "\n")
		.append("SolenoidLogic........................." + getSolenoidLogic() + "\n")
		.append("DefaultCommandFactory................." + getDefaultCommandFactory() + "\n")
		.append("\n")
		.append("----------------------------------------------------------------------------\n")
		;
		return buf.toString();
	}

	public String toString() {
		return name;
	}

	public void dumpDescription() {
		System.out.println(getDescription());
	}

}
