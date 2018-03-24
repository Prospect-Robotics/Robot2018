package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.values.Rate;

/**
 * If the motor is moving, change speeds.  If not, has no effect.Set the speed on a Motor subsystem.  It will take immediate effect
 * Also, do NOT attempt to change direction! 
 */
public final class MotorAdjustTargetRateInstant extends AbstractMotorInstantCommand {
	private final Rate targetRate;

	public MotorAdjustTargetRateInstant(Motor motor, Rate targetRate) {
		super(motor, false /* NB: we don't need to 'interrupt' existing movement for this */);
		this.targetRate=targetRate;
		if(targetRate.getValue() < 0) {
			throw new IllegalArgumentException("Speed may not be negative.  Use directions to go in reverse.");
		}
		setName(toString());
	}

	// Called once when the command executes
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.MOVING_IN_DIRECTION_AT_RATE && motor.getTargetState().getTargetRate() == targetRate) {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT telling %s to change target rate to %s, it's already moving at that speed.",this,motor,targetRate);
			}
		} else if(motor.getTargetState().getOperation() != MotorOperation.MOVING_IN_DIRECTION_AT_RATE ) {
			if(!isDefaultCommand()) {
				Logger.printFormat(LogType.INFO,"%s NOT telling %s to change target rate to %s, it's NOT MOVING.",this,motor,targetRate);
			}
		} else {
			Logger.printFormat(LogType.INFO,"%s telling %s to change target rate to %s.",this,motor,targetRate);
			motor.setTargetRate(targetRate);
		}		
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", targetRate=" + targetRate + ")";
    }

}
