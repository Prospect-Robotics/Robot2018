package org.usfirst.frc2813.Robot2018.subsystems.solenoid;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
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
	private Direction targetPosition;

	public Solenoid(SolenoidConfiguration configuration, edu.wpi.first.wpilibj.Solenoid solenoid) {
		this.configuration = configuration;
		this.solenoid = solenoid;
		initialize();
		setName(configuration.getName());
	}
	
	public Direction getTargetPosition() {
		return targetPosition;
	}
	
	public Direction getCurrentPosition() {
		if(isEmulated()) {
			return targetPosition;
		} else {
			boolean state = solenoid.get();
			if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
				state = !state;
			}
			return state ? Direction.ON : Direction.OFF;
		}
	}

	public void setTargetPosition(Direction direction) {
		boolean state = direction.isPositive(); 
		if (configuration.getSolenoidLogic() == SolenoidLogic.SolenoidLogicReversed) {
			state = !state;
		}
		if(solenoid.get() == state) {
			Logger.info(configuration.getName() + " told to set state to " + direction + ", but it already is.");
		} else {
			Logger.info(configuration.getName() + " changing state to " + direction + " [HW state " + state + "].");
		}
		if(!isEmulated()) {
			solenoid.set(state);
		}
		this.targetPosition = direction;
	}

	@Override
	protected void initDefaultCommand() {
		if(configuration.getDefaultCommandFactory() != null) {
			GearheadsCommand c = configuration.getDefaultCommandFactory().createCommand(this);
			if(c != null) {
				setDefaultCommand(c);
				c.setIsDefaultCommand(true);
			}
		}
	}

	public String toString() {
		return configuration.getName();
	}

	protected void initialize() {
		if(configuration.getDefaultPosition() != null) {
			setTargetPosition(configuration.getDefaultPosition());
		} else if(isEmulated()) {
			setTargetPosition(Direction.NEGATIVE);
		} else {
			// update targetPosition value to current position, in case we didn't have a default and it's real hardware
			this.targetPosition = getCurrentPosition();
		}
	}
}