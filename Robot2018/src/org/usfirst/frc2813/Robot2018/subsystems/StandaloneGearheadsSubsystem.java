package org.usfirst.frc2813.Robot2018.subsystems;

import edu.wpi.first.wpilibj.command.Command;

/*
 * This class exists as a stand in for GearheadsSubsystem that isn't derived
 * from WPI Subsystem, so it can be run without roboRIO.  Temporarily switch
 * your subsystem to this base class for testing.  Sadly, no preprocessor
 * so it's a manual process. 
 */
public class StandaloneGearheadsSubsystem {
	protected boolean encoderFunctional = true;
	protected boolean emulated = true;

	private String name;
	public StandaloneGearheadsSubsystem() {
		this.name = getClass().getSimpleName();
	}
	
	public String getName() { 
		return name; 
	}
	public void setName(String name) {  
		this.name = name; 
	}
	public boolean isEmulated() { return emulated; }
	
	/**
	 * Doesn't do anything
	 */
	public void disableEmulator() {
	}
	/**
	 * Doesn't do anything
	 */
	public void enableEmulator() {
	}
	/**
	 * Doesn't do anything
	 * @return true
	 */
	public boolean isRobotEnabled() {
		return true;
	}
	/**
	 * Doesn't do anything
	 */
	public void initDefaultCommand() {
	}
	/**
	 * Doesn't do anything
	 * @param defaultCommand ignored. 
	 */
	public void setDefaultCommand(Command defaultCommand) {
		
	}
	/**
	 * Doesn't do anything
	 */
	public void periodic() {
	}
}
