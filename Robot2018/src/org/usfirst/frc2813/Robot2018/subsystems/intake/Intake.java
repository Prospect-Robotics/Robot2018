package org.usfirst.frc2813.Robot2018.subsystems.intake;

import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * minimal subsystem to manage Intake. It spins in/out with speed or stops
 */
public class Intake extends GearheadsSubsystem {
	private WPI_VictorSPX spx;
	double speed;
	Direction direction;

	public Intake(WPI_VictorSPX victor) {
		spx = victor;
		direction = Direction.STOP;
		speed = 1.0;
	}

	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}

	public void setTargetSpeed(double speed) {
		if (speed < 0 || 1 < speed) {
			throw new UnsupportedOperationException("Speed must be in [0, 1]");
		}
		this.speed = speed;
	}
	
	public double getTargetSpeed() {
		return speed;
	}
	
	public Direction getTargetDirection() {
		return direction;
	}
	
	public double getCurrentSpeed() {
		return spx.get();
	}
	
	public Direction getCurrentDirection() {
		return spx.get() < 0 ? Direction.OUT : (spx.get() == 0 ? Direction.STOP : Direction.IN);
	}
	
	public boolean isEnabled() {
		return spx.get() > 0;
	}

	public void spin(Direction direction) {
		if (direction.isNeutral()) {
			stop();
		}
		if (isEmulated()) {
			Logger.printLabelled(LogType.INFO, "EMULATOR: Intake spin", "Direction", direction, "Speed", speed);
		}
		else {
			Logger.printLabelled(LogType.INFO, "INTAKE spin", "Direction", direction, "Speed", speed);
			spx.set(direction.isPositive() ? -speed : speed);
		}
		this.direction = direction;
	}

	public void stop() {
		if (isEmulated()) {
			Logger.info("EMULATOR: Intake stop");
		}
		else {
			Logger.info("INTAKE stop");
			spx.set(0); // NB: Just in case disable doesn't clear speed.  Couldn't find a isDisabled() function.
			spx.disable();
		}
	}
}

