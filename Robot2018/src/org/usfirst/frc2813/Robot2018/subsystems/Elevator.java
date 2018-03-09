package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Elevator.MoveElevator;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Elevator subsystem. Can set speed and move up/down/to abs position in inches or 0-1
 */
public class Elevator extends Subsystem {
	public boolean encoderFunctional = true;
	private static Direction direction;
	private static double speed;
	private static boolean isHalted;
	private static Talon motorController;

	// ELEVATOR GEOMETRY
	// TODO find maximum allowable elevator height; 24 is only a placeholder.
	public static final double ELEVATOR_HEIGHT = 24; // inches
	public static final double INCHES_PER_REVOLUTION = Math.PI * 1.25;
	public static final double INCHES_PER_PULSE = INCHES_PER_REVOLUTION / Talon.PULSES_PER_REVOLUTION;
	public static final double PULSES_PER_INCH = Talon.PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
    public static final double ELEVATOR_MAX = ELEVATOR_HEIGHT * PULSES_PER_INCH;
    public static final double ELEVATOR_DEFAULT_SPEED = feetPerSecondToSpeed(2);

	public Elevator() {
		motorController = new Talon(RobotMap.srxElevator);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, (int)ELEVATOR_MAX);
		//motor.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 1, 0, TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS);
        motorController.initPID();
	    motorController.setPID(Talon.MAINTAIN_PID_LOOOP_IDX, 0.8, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 0.75, 0.01, 40);

		// track state and change as required. Start in moving so initialize can halt
		isHalted = false;
        direction = Direction.UP;
        speed = ELEVATOR_DEFAULT_SPEED;
	}

	// speed and direction for elevator are state
	public static void setSpeed() { speed = ELEVATOR_DEFAULT_SPEED; }
	public static void setSpeed(double speedParam) { speed = speedParam; }

	public static double feetPerSecondToSpeed(double feetPerSecond) {
		return (feetPerSecond * 12.0 * PULSES_PER_INCH) / 10.0;
	}

	public static void setSpeedFeetPerSecond(double feetPerSecond) {
		setSpeed(feetPerSecondToSpeed(feetPerSecond));
	}

	public static void setDirection(Direction directionParam) { direction = directionParam; }

	public static double readPosition() {
		return motorController.readPosition();
	}

	public static double readEndPosition() {
		if (direction == Direction.DOWN) {
			return 0.0;
		}
		else {
			return ELEVATOR_MAX;
		}
	}

	// Start elevator moving
	public static void move() {
		isHalted = false;
		motorController.setSpeedAndDirection(speed, direction);
		System.out.format("Starting elevator movement. Speed %f", speed);
	}

	// stop elevator from moving - this is active as we require pid to resist gravity
	public static void halt() {
		if (isHalted) {
			return;
		}
		isHalted = true;
		motorController.halt();
		System.out.format("Halting elevator movement. Position %f", motorController.readPosition());
	}

	public static void goToPositionInches(double inchesFromBottom) {
		// round to nearest int because unless we're using an analog sensor, this expects values in inches.
		// Not sure what the talon will do if we give it a float value when it expects an int value.
		// It might floor it (it *should* floor it), but it *might* puke.
		motorController.goToPosition((int) (inchesFromBottom * PULSES_PER_INCH));
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new MoveElevator());
	}

	@Override
	public void periodic() {}
}
