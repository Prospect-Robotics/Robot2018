package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public class MotorCalibrateSensor extends AbstractMotorInstantCommand {
	private final Direction direction;

	public MotorCalibrateSensor(Motor motor, Direction direction) {
		super(motor, true);
		this.direction = direction;
	}

	@Override
	protected void initialize() {
		super.initialize();
		if(motor.getTargetState().getOperation() == MotorOperation.CALIBRATING_SENSOR_IN_DIRECTION && motor.getTargetDirection() == direction) {
			Logger.printFormat(LogType.INFO,"%s NOT telling %s to calibrate the sensor in  to %s, it's already doing that.",this,motor,direction);
		} else {
			Logger.printFormat(LogType.INFO,"%s telling %s to calibrate using hard limit switch in %s direction.",this,motor,direction);
			motor.calibrateSensorInDirection(direction);
		}
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", " + direction + ")";
    }
}
