package org.usfirst.frc2813.Robot2018.subsystems.intake;

import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
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
		direction = Direction.IDLE;
		speed = 1.0;
	}

	public void initDefaultCommand() {
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
		if(isEmulated()) {
			return speed;
		} else {
			return spx.get();
		}
	}
	
	public Direction getCurrentDirection() {
		if(isEmulated()) {
			return direction;
		} else if(getCurrentSpeed() == 0) {
			return Direction.IDLE;
		} else if(spx.get() == 0) {
			return Direction.IDLE;
		}
		else if(spx.get() < 0) {
			return Direction.IN;
		} 
		else if(spx.get() > 0) {
			return Direction.OUT;
		}
		throw new IllegalStateException("Universe implosion imminent!");
	}

	public boolean isEnabled() {
		if(isEmulated()) {
			return speed > 0;
		} else {
			return spx.get() > 0;
		}
	}

	public void spin(Direction targetDirection) {
		Logger.info(this + " spin(" + targetDirection + ").");
		if (targetDirection.isNeutral()) {
			Logger.info(this + " spin(" + targetDirection + ") - stopping.");
			if (!isEmulated()) {
				spx.set(0); // NB: Just in case disable doesn't clear speed.  Couldn't find a isDisabled() function.
				spx.disable();
			}
			this.direction = Direction.IDLE;
		} else {
			Logger.info(this + " intake spin.  Direction=" + targetDirection + " speed=" + speed);
			if(!isEmulated()) {
				spx.set(targetDirection.isPositive() ? -speed : speed);
			}
		}
		this.direction = targetDirection;
	}

	public void stop() {
		spin(Direction.IDLE);
	}
}

