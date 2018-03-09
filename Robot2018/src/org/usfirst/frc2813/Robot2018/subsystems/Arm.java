package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * Arm subsystem
 *
 * Arm rotates through 24 degrees.
 * Arm has state halted, speed and direction.
 * Arm can be moving up or down or be halted.
 *
 * Intake has state halted and direction.
 * Intake can be moving in, out or halted.
 *
 * Jaws can open and close.
 * Jaws have state tracking this. - FIXME: can jaws open if closed too long? Should we care?
 */
public class Arm extends Subsystem {
	public boolean encoderFunctional = true;
	private static Direction armDirection;
	private static double armSpeed;
	private static boolean armIsHalted;
	private static Direction intakeDirection;
	private static double intakeSpeed;
	private static Direction jawsState;
	private static Talon motorController;

	// INTAKE GEOMETRY
	private static final double INTAKE_DEFAULT_SPEED = 0.7;

	// ARM GEOMETRY
	static final double ARM_DEGREES = 24;
	static final double ARM_GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
	static final double ARM_PULSES_PER_REVOLUTION = ARM_GEAR_RATIO * Talon.PULSES_PER_REVOLUTION;
	static final double ARM_ONE_DEGREE = ARM_PULSES_PER_REVOLUTION / 360;
	static final double ARM_ONE_DEGREE_PER_SECOND = ARM_ONE_DEGREE * Talon.VELOCITY_TIME_UNITS_PER_SEC;
	static final double ARM_DEFAULT_SPEED = 5 * ARM_ONE_DEGREE_PER_SECOND;
	static final double ARM_MIN = 0.0;
	static final double ARM_MAX = ARM_ONE_DEGREE * ARM_DEGREES;

	public Arm() {
		motorController = new Talon(RobotMap.srxArm);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, (int)ARM_MAX);
        motorController.initPID();
	    motorController.setPID(Talon.MAINTAIN_PID_LOOOP_IDX, 0.1, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 2, 0, 0);
        setIntakeSpeed();
        setArmSpeed();
        armDirection = Direction.UP;
        intakeDirection = Direction.IN;
		armIsHalted = false;
		jawsState = RobotMap.armJawsSolenoid.get() ? Direction.OPEN : Direction.CLOSE;
	}

	// speed and direction for arm and intake are state
	public static void setArmSpeed(double speed) { armSpeed = speed; }
	public static void setArmSpeed() { setArmSpeed(ARM_DEFAULT_SPEED); }
	public static void setArmDirection(Direction direction) { armDirection = direction; }
	public static void setIntakeSpeed(double speed) { intakeSpeed = speed; }
	public static void setIntakeSpeed() { setIntakeSpeed(INTAKE_DEFAULT_SPEED); }
	public static void setIntakeDirection(Direction direction) { intakeDirection = direction; }

	public static double readArmPosition() {
		return motorController.readPosition();
	}

	public static double readArmEndPosition() {
		if (armDirection == Direction.DOWN) {
			return 0.0;
		}
		else {
			return ARM_MAX;
		}
	}

	// Start arm moving. Can transition from moving since speed/direction may have changed
	public static void moveArm() {
		armIsHalted = false;
		motorController.setSpeedAndDirection(armSpeed, armDirection);
		System.out.println("Starting arm movement. Speed " + armSpeed);
	}

	// stop arm from moving - this is active as we require pid to resist gravity
	public static void haltArm() {
		if (armIsHalted) {
			return;
		}
		armIsHalted = true;
		motorController.halt();
		System.out.println("Halting arm movement. Position " + motorController.readPosition());
	}

	// Manage jaws
	public static void setJaws(Direction direction) {
		if (jawsState != direction) {
			RobotMap.armJawsSolenoid.set(direction == Direction.CLOSE ? true : false);
			jawsState = direction;
		}
	}

	// Manage intake wheels
	public static void spinIntake() {
		// NOTE: 2nd speed controller is slaved to this one in RobotMap
		double speed = intakeSpeed * (intakeDirection == Direction.IN ? 1 : -1);
		RobotMap.intakeSpeedController.set(speed);
	}
	public static void haltIntake() {
		RobotMap.intakeSpeedController.set(0);
	}

	// initializes arm in static position
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new MoveArm());
	}

	@Override
	public void periodic() {}
}
