package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Elevator.MoveElevator;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Elevator subsystem. Moves up and down.
 * 
 * Elevator can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in inches.
 * speed is in inches per second.
 */
public class Elevator extends Subsystem {
	public boolean encoderFunctional = true;
	private static Log log;
	private static Direction direction;
	private static double speed;
	private static double position;
    private static boolean positionMode;  // true if we are moving to a position
                                          // false if we move by direction and speed
	private static boolean isHalted;
	private static Talon motorController;

	/**
	 * ELEVATOR GEOMETRY
	 * TODO: find maximum allowable elevator height; 24 is only a placeholder.
	 */
	public static final double HEIGHT = 24; // inches
	private static final double INCHES_PER_REVOLUTION = Math.PI * 1.25;
	private static final double PULSES_PER_INCH = Talon.PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
    private static final double DEFAULT_SPEED = 12;
    private final String label;
    
//    enum ElevatorState { HALTED, HOLDING, MOVING };
//    private ElevatorState state = ElevatorState.HALTED; 
    
	public Elevator() {
		this.label = "Elevator";
		log = new Log(this.label);
		motorController = new Talon(RobotMap.srxElevator, this.label, log);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, (int)(HEIGHT * PULSES_PER_INCH));
  	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.8, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 0.75, 0.01, 40);

	 // NB: Soon we need to go down to zero at startup to initialize/calibrate
		// track state and change as required. Start in moving so initialize can halt
        direction = Direction.NEUTRAL;
        position = 0;
        speed = DEFAULT_SPEED;
        positionMode = false;
        isHalted = true;
//        state = ElevatorState.HALTED; 
	}

    /**
    * Map position from inches to controller ticks
    */
    private static int positionToSrx(double inchesFromBotton) {
		return (int)(inchesFromBotton * PULSES_PER_INCH);
    }

    /**
    * Map position from controller ticks to inches from bottom
    */
    private static double srxToPosition(int ticks) {
        return ticks / PULSES_PER_INCH;
    }

    /**
    * Map speed from inches per second to controller format
    */
    private static double speedToSrx(double inchesPerSecond) {
        return inchesPerSecond * PULSES_PER_INCH / 10.0;
    }

	public static void setSpeed() {
        setSpeed(DEFAULT_SPEED);
    }

	public static void setSpeed(double inchesPerSecond) {
        speed = inchesPerSecond;
        if (!isHalted) move();  // commit state if not halted
	}

	public static void setDirection(Direction directionParam) {
        direction = directionParam;
        positionMode = false;
        if (!isHalted) move();  // commit state if not halted
    }

	public static double readPosition() {
		return srxToPosition(motorController.readPosition());
	}

	/**
	 * set position in inches
	 */
	public static void setPosition(double inches) {
        positionMode = true;
        position = inches;
        if (!isHalted) move();  // commit state if not halted
	}

	public static boolean readLimitSwitch(Direction switchDirection) {
		return Elevator.readLimitSwitch(switchDirection);
	}

	// Start elevator moving
	public static void move() {
		// FIXME! this one variable requires the whole class to be non-static
		// We should change POST to use statics
        // if (!encoderFunctional) return;

		isHalted = false;
        if (positionMode) {
            motorController.setPosition(positionToSrx(position));
            System.out.format("Starting elevator movement. Target position %f\n", position);
        }
        else {
            motorController.setSpeedAndDirection(speedToSrx(speed), direction);
            System.out.format("Starting elevator movement. Speed %f\n", speed);
        }
	}

    public static void move(Direction directionParam) {
        direction = directionParam;
        move();
    }

    public static void move(double speedParam) {
        speed = speedParam;
        move();
    }

    public static void move(double speedParam, Direction directionParam) {
        speed = speedParam;
        direction = directionParam;
        move();
    }

	// stop elevator from moving - this is active as we require pid to resist gravity
	public static void halt() {
		// FIXME! this one variable requires the whole class to be non-static
		// We should change POST to use statics
        // if (!encoderFunctional) return;

		isHalted = true;
		motorController.halt();
		if(motorController.readLimitSwitch(Direction.DOWN)) {
			motorController.zeroEncoders();
		}
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new MoveElevator());
	}

	@Override
	public void periodic() {}
	
	@Override
	public String toString() {
		return label;
	}
}
