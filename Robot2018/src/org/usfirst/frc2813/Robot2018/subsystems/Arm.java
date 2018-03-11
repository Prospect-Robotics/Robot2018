package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

/**
 * Arm subsystem. Rotates up and down
 *
 * Arm can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in degrees.
 * speed is in inches per second.
 */
public class Arm extends SubsystemPositionDirectionSpeed {
	private static final double GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
    private static final double PULSES_PER_REVOLUTION = GEAR_RATIO * Talon.PULSES_PER_REVOLUTION;

    private static final double PULSES_PER_DEGREE = PULSES_PER_REVOLUTION / 360;
    private static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.


	private Talon motorController;

	public Arm() {
		MAX_POSITION = 24;
		MIN_POSITION = 0;
		PULSES_PER_UNIT_POSITION = PULSES_PER_REVOLUTION / 360;
		PULSES_PER_UNIT_POSITION_PER_TIME = PULSES_PER_DEGREE * VELOCITY_TIME_UNITS_PER_SEC;
	    DEFAULT_SPEED = 5;

		motorController = new Talon(RobotMap.srxArm, logger);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION));
	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.1, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 2, 0, 0);

	    initialize();
	}

	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	protected int readControllerPosition() {
		return motorController.readPosition();
	}

	protected void setControllerPosition(int positionParam) {
		motorController.setPosition(positionParam);
	}

	protected MotorControllerState readMotorControllerState() {
		return motorController.getState();
	}

	protected void setControllerSpeedAndDirection(int speedParam) {
		motorController.move(direction, speedParam);
	}

	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes arm in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new MoveArm());
	}

	@Override
	public void periodic() {}
}
