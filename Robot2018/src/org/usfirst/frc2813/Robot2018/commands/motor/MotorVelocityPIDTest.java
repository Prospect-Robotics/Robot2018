package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Move motor in given direction until interrupted.
 * Hold current position with PID when interrupted. 
 */
public final class MotorVelocityPIDTest extends AbstractMotorCommand {
	private Direction targetDirection = Direction.FORWARD;
	private final Rate targetRate;
	private final boolean continuousRotation;
	
	/**
	 * Run a velocity test between limits (hard and/or soft) at the specified velocity  
	 */
	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation, Rate targetRate) {
		super(motor, true);
		this.targetRate = targetRate;
		this.continuousRotation = continuousRotation;
		setName(toString());
	}

	/**
	 * Run a velocity test between limits (hard and/or soft) at the default velocity
	 */
	public MotorVelocityPIDTest(Motor motor, boolean continuousRotation) {
		this(motor, continuousRotation, motor.getConfiguration().getDefaultRate());
	}

	/**
	 * Run a velocity test between limits (hard and/or soft) at the default velocity, assuming NO continuous rotation
	 * for safety reasons.
	 */
	public MotorVelocityPIDTest(Motor motor) {
		this(motor, false, motor.getConfiguration().getDefaultRate());
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(continuousRotation) {
			if(!motor.getHasHardOrSoftLimit(Direction.FORWARD)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test with a hard or soft FORWARD limit in CONTINOUS mode.");
			}
			if(!motor.getHasHardOrSoftLimit(Direction.REVERSE)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test with a hard or soft REVERSE limit in CONTINOUS mode.");
			}
		} else {
			if(!motor.getHasHardOrSoftLimit(Direction.FORWARD)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test without a hard or soft FORWARD limit in NON-CONTINOUS mode.");
			}
			if(!motor.getHasHardOrSoftLimit(Direction.REVERSE)) {
				throw new UnsupportedOperationException("Cowardly refusing to run the velocity pid test without a hard or soft REVERSE limit in NON-CONTINOUS mode.");
			}
		}
		motor.moveInDirectionAtRate(targetDirection, targetRate);
	}

	protected void execute() {
		if((motor.getHasHardLimit(targetDirection) && motor.getCurrentHardLimitSwitchStatus(targetDirection))
		|| (motor.getHasSoftLimit(targetDirection) && motor.isSoftLimitReached(targetDirection))) {
			targetDirection = targetDirection.getInverse();
			Logger.info("REVERSING.  GOING " + targetDirection + " @ " + motor.getCurrentPosition() + " Error " + motor.getCurrentRateError() + " Goal " + targetRate);
			motor.moveInDirectionAtRate(targetDirection, targetRate);
		} else {
			Logger.info("NOT THERE.  GOING " + targetDirection + " @ " + motor.getCurrentPosition() + " Error " + motor.getCurrentRateError() + " Goal " + targetRate);
		}
	}

	@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		super.interrupted();
        /*
        * NOTE: Typically this is also the default command for motor subsystems, so it's kind of redundant but logical. 
        */
		motor.holdCurrentPosition();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", continuousRotation=" + continuousRotation + ", targetRate=" + targetRate + ")";
    }
}
