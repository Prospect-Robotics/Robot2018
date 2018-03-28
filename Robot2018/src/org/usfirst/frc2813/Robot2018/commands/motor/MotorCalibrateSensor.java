package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;

/**
 * Move motor to an absolute position. Motor controller does 
 * this without further intervention, and then holds the 
 * position with PID. 
 */
public final class MotorCalibrateSensor extends MotorCommand {
	private final Direction direction;

    public MotorCalibrateSensor(Motor motor, Direction direction, RunningInstructions duration, Lockout lockout) {
    	super(motor, duration, lockout);
		this.direction = direction;
		addArg("direction", direction);
		if(!motor.getConfiguration().getHasHardLimit(direction)) {
			throw new IllegalArgumentException("The motor " + motor + " does not have a hard limit sensor in the " + direction + " direction. Cowardly refusing to break the robot.");
		}
		setName(toString());
    }

    public MotorCalibrateSensor(Motor motor, Direction direction, RunningInstructions duration) {
    	this(motor, direction, duration, Lockout.Disabled);
    }

    public MotorCalibrateSensor(Motor motor, Direction direction) {
    	this(motor, direction, RunningInstructions.RUN_NORMALLY);
    }

	@Override
	protected void ghscInitialize() {
		if(subsystem.getTargetState().getOperation() == MotorOperation.CALIBRATING_SENSOR_IN_DIRECTION && subsystem.getTargetState().getTargetDirection() == direction) {
			traceFormatted("initialize", "NOT telling %s to calibrate the sensor in  to %s, it's already doing that.",subsystem,direction);
		} else {
			traceFormatted("initialize", "telling %s to calibrate using hard limit switch in %s direction.",subsystem,direction);
			subsystem.calibrateSensorInDirection(direction);
		}
	}

	/**
	 * We definitely require the subsystem
	 */
	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected boolean ghscIsFinished() {
		if(subsystem.getCurrentHardLimitSwitchStatus(direction)) {
			traceFormatted("isFinished", "hard limit reached.  calibration completed.");
			return true;
		}
		traceFormatted("isFinished", "still waiting.");
		return false;
	}
}
