package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.subsystems.SubsystemBinary;
import org.usfirst.frc2813.units.Direction;

/**
 * Solenoid subsystem controls a simple subsystem that goes on and off.
 */
public class Solenoid extends SubsystemBinary {
	private final SolenoidConfiguration configuration;
	private final edu.wpi.first.wpilibj.Solenoid solenoid;
	
	public Solenoid(SolenoidConfiguration configuration, edu.wpi.first.wpilibj.Solenoid solenoid) {
		this.configuration = configuration;
		this.solenoid = solenoid;
	}

	/*
	 * Get the current state
	 */
	public Direction getState() {
		return getControllerState();
	}

	/**
	 * Set the current state
	 */
	public void setState(Direction direction) {
		setControllerState(direction);
	}

	@Override
	protected Direction getControllerState() {
		boolean state = solenoid.get();
		if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
			state = !state;
		}
		return state ? Direction.ON : Direction.OFF;
	}
	
	@Override
	protected void setControllerState(Direction direction) {
		boolean state = direction.isPositive(); 
		if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
			state = !state;
		}
		solenoid.set(state);
	}

	@Override
	protected void initDefaultCommand() {
		if(configuration.getDefaultCommand() != null) {
			setDefaultCommand(configuration.getDefaultCommand());
		}
	}
	
	public String toString() {
		return configuration.getName();
	}
}