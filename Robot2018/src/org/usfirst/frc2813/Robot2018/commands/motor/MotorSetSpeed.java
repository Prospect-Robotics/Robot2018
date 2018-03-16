package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.Robot2018.subsystems.motor.MotorControllerState;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Rate;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * If the motor is moving, change speeds.  If not, has no effect.Set the speed on a Motor subsystem.  It will take immediate effect
 * Also, do NOT attempt to change direction! 
 */
public class MotorSetSpeed extends MotorInstantCommand {
	private final Rate speed;

	public MotorSetSpeed(Motor motor, Rate speed) {
		super(motor, false /* NB: we don't need to 'interrupt' existing movement for this */);
		this.speed=speed;
		if(speed.getValue() < 0) {
			throw new IllegalArgumentException("Speed may not be negative.  Use directions to go in reverse.");
		}
	}

	// Called once when the command executes
	protected void initialize() {
		super.initialize();
		if(motor.getMotorControllerState() == MotorControllerState.MOVING && motor.getSpeed() == speed) {
			Logger.info("NOT telling " + motor.getName() + " to change speed to " + speed + ", it's already moving at that speed.");
		} else if(motor.getMotorControllerState() != MotorControllerState.MOVING ) {
			Logger.info("NOT telling " + motor.getName() + " to change speed to " + speed + ", it's NOT MOVING.");
		} else {
			Logger.info("Telling " + motor.getName() + " to change speed to " + speed + ".");
			motor.changeSpeed(speed);
		}
		
	}
}
