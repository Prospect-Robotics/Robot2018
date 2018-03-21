package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.solenoid.SolenoidLogic;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Solenoid subsystem controls a simple subsystem that goes on and off.
 */
public class Solenoid extends GearheadsSubsystem {
	private final SolenoidConfiguration configuration;
	private final edu.wpi.first.wpilibj.Solenoid solenoid;

	public Solenoid(SolenoidConfiguration configuration, edu.wpi.first.wpilibj.Solenoid solenoid) {
		this.configuration = configuration;
		this.solenoid = solenoid;
		initialize();
		setName(configuration.getName());
	}
	
	public Direction getPosition() {
		boolean state = solenoid.get();
		if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
			state = !state;
		}
		return state ? Direction.ON : Direction.OFF;
	}

	public void setPosition(Direction direction) {
		boolean state = direction.isPositive(); 
		if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
			state = !state;
		}
		if(solenoid.get() == state) {
			Logger.info(configuration.getName() + " told to set state to " + direction + ", but it already is.");
		} else {
			Logger.info(configuration.getName() + " changing state to " + direction + " [HW state " + state + "].");
		}
		solenoid.set(state);
	}

	@Override
	protected void initDefaultCommand() {
		if(configuration.getDefaultCommandFactory() != null) {
			setDefaultCommand(configuration.getDefaultCommandFactory().createCommand(this));
		}
	}

	public String toString() {
		return configuration.getName();
	}

	protected void initialize() {
		if(configuration.getDefaultPosition() != null) {
			setPosition(configuration.getDefaultPosition());
		}
	}
}