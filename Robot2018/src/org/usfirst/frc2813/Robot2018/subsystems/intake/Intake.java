package org.usfirst.frc2813.Robot2018.subsystems.intake;

import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSlowBurn;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * minimal subsystem to manage Intake. It spins in/out with speed or stops
 */
public class Intake extends GearheadsSubsystem {
	private WPI_TalonSRX mc;
	double defaultSpeed;
	double targetSpeed;
	Direction targetDirection;
	
	public static final int PEAK_CURRENT_LIMIT = 35;
	public static final int CONTINUOUS_CURRENT_LIMIT = 35;
	public static final int PEAK_CURRENT_DURATION = 0;

	public Intake(WPI_TalonSRX mc) {
		this.mc = mc;
		targetDirection = Direction.IDLE;
		defaultSpeed = 1.0;
		targetSpeed = defaultSpeed;

 		// Set static current limits
 		mc.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, 10);
		mc.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, 10);
		mc.configPeakCurrentDuration(PEAK_CURRENT_DURATION, 10);
		mc.enableCurrentLimit(true);
	}

	public void initDefaultCommand() {
//		setDefaultCommand(new IntakeSlowBurn(this, direction));
	}

	public void setDefaultSpeed(double defaultSpeed) {
		if (defaultSpeed < 0 || 1 < defaultSpeed) {
			throw new UnsupportedOperationException("Speed must be in [0, 1]");
		}
		this.defaultSpeed = defaultSpeed;
	}

	public void setTargetSpeed(double targetSpeed) {
		if (targetSpeed < 0 || 1 < targetSpeed) {
			throw new UnsupportedOperationException("Speed must be in [0, 1]");
		}
		this.targetSpeed = targetSpeed;
	}

	public double getDefaultSpeed() {
		return defaultSpeed;
	}

	public double getTargetSpeed() {
		return targetSpeed;
	}

	public Direction getTargetDirection() {
		return targetDirection;
	}

	public double getCurrentSpeed() {
		if(isEmulated()) {
			return targetSpeed;
		} else {
			return mc.get();
		}
	}

	public Direction getCurrentDirection() {
		if(isEmulated()) {
			return targetDirection;
		} else if(getCurrentSpeed() == 0) {
			return Direction.IDLE;
		} else if(mc.get() == 0) {
			return Direction.IDLE;
		}
		else if(mc.get() < 0) {
			return Direction.IN;
		} 
		else if(mc.get() > 0) {
			return Direction.OUT;
		}
		throw new IllegalStateException("Universe implosion imminent!");
	}

	public boolean isEnabled() {
		if(isEmulated()) {
			return targetSpeed > 0;
		} else {
			return mc.get() > 0;
		}
	}

	/**
	 * Spin in the indicated direction at indicatd speed
	 * @param targetDirection direction to spin
	 * @param speed percentage of maximum power (range -1.0 to 1.0)
	 */
	public void spin(Direction targetDirection, double speed) {
		if (targetDirection.isNeutral()) {
			Logger.debug(this + " stopping.  Direction is NEUTRAL.");
			if (!isEmulated()) {
				mc.set(0); // NB: Just in case disable doesn't clear speed.  Couldn't find a isDisabled() function.
				mc.disable();
			}
			this.targetDirection = Direction.IDLE;
		} else {
			double rate = targetDirection.isPositive() ? -speed : speed;
			Logger.debug(this + " spinning @" + Math.round(100 * rate) + "% power.");
			if(!isEmulated()) {
				mc.set(rate);
			}
		}
		this.targetSpeed = speed;
		this.targetDirection = targetDirection;
	}

	/**
	 * Spin in the indicated direction at default speed
	 * @param targetDirection direction to spin
	 */
	public void spin(Direction targetDirection) {
		spin(targetDirection, this.defaultSpeed);
	}

	public void stop() {
		spin(Direction.IDLE);
	}
}

