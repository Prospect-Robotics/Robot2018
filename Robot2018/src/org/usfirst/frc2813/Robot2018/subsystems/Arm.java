package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Arm subsystem - 3 subsystems in one. Arm/Intake/Jaws.
 * 
 * ARM can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in degrees.
 * speed is in degrees per second.
 *
 * INTAKE can halt, or move in a direction.
 * Speed can be set separately.
 * speed is in inches per second.
 * 
 * JAWS can open and close.
 * FIXME: can jaws open if closed too long? Should we care?
 */
public class Arm extends Subsystem {
	public boolean encoderFunctional = true;
	private static Log log;
	private static Talon motorController;

	/**
	 * Arm state
	 */
	private static Direction armDirection;
	private static double armSpeed;
	private static double armPosition;
    /**
     * True if moving to position, false if moving in direction
     */
    private static boolean armPositionMode;
	private static boolean armIsHalted;

	/**
	 * Intake state
	 */
	private static Direction intakeDirection;
	private static double intakeSpeed;
	private static boolean intakeIsHalted;

	/**
	 * Jaws state
	 */
	private static Direction jawsState; // open or close
	

	/**
	 * INTAKE GEOMETRY
	 */
	private static final double INTAKE_DEFAULT_SPEED = 0.7;

	/**
	 * ARM GEOMETRY
	 */
	public static final double DEGREES = 24;
	private static final double GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
	private static final double PULSES_PER_REVOLUTION = GEAR_RATIO * Talon.PULSES_PER_REVOLUTION;
	private static final double PULSES_PER_DEGREE = PULSES_PER_REVOLUTION / 360;
	private static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.
	private static final double PULSES_PER_DEGREE_PER_SECOND = PULSES_PER_DEGREE * VELOCITY_TIME_UNITS_PER_SEC;
	private static final double ARM_DEFAULT_SPEED = 5;

	private final String label; 
	public Arm() {
		this.label = "Arm";
		log = new Log(this.label);

		motorController = new Talon(RobotMap.srxArm, this.label, log);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, armPositionToSrx(DEGREES));
	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.1, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 2, 0, 0);
        armDirection = Direction.DOWN;
        armPosition = 0;
        armPositionMode = false;
		armIsHalted = false;
        intakeDirection = Direction.IN;
		intakeIsHalted = false;
		jawsState = RobotMap.armJawsSolenoid.get() ? Direction.OPEN : Direction.CLOSE;
	}

    /**
    * Map position from inches to controller ticks
    */
    private static int armPositionToSrx(double degrees) {
		return (int)(degrees * PULSES_PER_DEGREE);
    }

    /**
    * Map position from controller ticks to inches from bottom
    */
    private static double srxToArmPosition(int ticks) {
        return ticks / PULSES_PER_DEGREE;
    }

    /**
    * Map speed from inches per second to controller format
    */
    private static double armSpeedToSrx(double degreesPerSecond) {
        return degreesPerSecond * PULSES_PER_DEGREE_PER_SECOND;
    }

	public static void setArmSpeed() {
        setArmSpeed(ARM_DEFAULT_SPEED);
    }

	public static void setArmSpeed(double degreesPerSecond) {
        armSpeed = degreesPerSecond;
        if (!armIsHalted) moveArm();  // commit state if not halted
	}

	public static void setArmDirection(Direction direction) {
        armDirection = direction;
        armPositionMode = false;
        if (!armIsHalted) moveArm();  // commit state if not halted
    }

	public static double readArmPosition() {
		return srxToArmPosition(motorController.readPosition());
	}

	/**
	 * set position in degrees
	 */
	public static void setArmPosition(double degrees) {
        armPositionMode = true;
        armPosition = degrees;
        if (!armIsHalted) moveArm();  // commit state if not halted
	}

	public static void setIntakeSpeed(double speed) {
		intakeSpeed = speed;
        if (!intakeIsHalted) spinIntake();  // commit state if not halted
	}
	public static void setIntakeSpeed() { setIntakeSpeed(INTAKE_DEFAULT_SPEED); }
	
	public static void setIntakeDirection(Direction direction) {
		intakeDirection = direction;
        if (!intakeIsHalted) spinIntake();  // commit state if not halted
	}

	// Start arm moving
	public static void moveArm() {
		// FIXME! this one variable requires the whole class to be non-static
		// We should change POST to use statics
        // if (!encoderFunctional) return;

		armIsHalted = false;
        if (armPositionMode) {
            motorController.holdCurrentPosition();
            log.print("Starting elevator movement. Target position " + armPosition);
        }
        else {
            motorController.move(armDirection, armSpeedToSrx(armSpeed));
            log.print("Starting elevator movement. Speed " + armSpeed);
        }
	}

    public static void moveArm(Direction direction) {
        armDirection = direction;
        moveArm();
    }

    public static void moveArm(double speed) {
        armSpeed = speed;
        moveArm();
    }

    public static void moveArm(double speed, Direction direction) {
        armSpeed = speed;
        armDirection = direction;
        moveArm();
    }

	// stop elevator from moving - this is active as we require pid to resist gravity
	public static void haltArm() {
		// FIXME! this one variable requires the whole class to be non-static
		// We should change POST to use statics
        // if (!encoderFunctional) return;
		armIsHalted = true;
		motorController.disable();
		motorController.zeroEncodersIfNecessary();
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
	
	@Override
	public String toString() {
		return label;
	}
}
