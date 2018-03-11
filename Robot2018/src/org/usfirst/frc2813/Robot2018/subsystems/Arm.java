package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;
import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;
import org.usfirst.frc2813.Robot2018.commands.Elevator.MoveElevator;

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
	private static final double INCHES_PER_REVOLUTION = Math.PI * 1.25;
	private static final double PULSES_PER_INCH = Talon.PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
    private static final double DEFAULT_SPEED = 12;
	
	private Log log;
	private Talon motorController;
	
	// State data for debugging/diagnostics only
    private final String label;
	// State data for debugging/diagnostics only
	private double lastPositionInches = 0;
	// State data for debugging/diagnostics only
	private int lastPositionSrx = 0;
	// State data for debugging/diagnostics only
	private Direction direction = Direction.NEUTRAL;
	// Speed in inches per second (last stored/assigned value)
	private double speedInchesPerSecond = DEFAULT_SPEED;
	// Speed in SRX units (last stored/assigned value)
	private double speedSrx = speedToSrx(DEFAULT_SPEED);
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
	 * TODO: Find maximum allowable arm rotation. 
	 */
	public static final double DEGREES = 24;
	private static final double GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
	private static final double PULSES_PER_REVOLUTION = GEAR_RATIO * Talon.PULSES_PER_REVOLUTION;
	private static final double PULSES_PER_DEGREE = PULSES_PER_REVOLUTION / 360;
	private static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.
	private static final double PULSES_PER_DEGREE_PER_SECOND = PULSES_PER_DEGREE * VELOCITY_TIME_UNITS_PER_SEC;
	private static final double ARM_DEFAULT_SPEED = 5;

	public Arm() {
		this.label = "Arm-Subsystem";
		log = new Log(this.label);

		motorController = new Talon(RobotMap.srxArm, this.label, new Log("Elevator-Talon"));
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, revolutionsToSrx(DEGREES));
	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.1, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 2, 0, 0);
	    
        intakeDirection = Direction.IN;
		intakeIsHalted = false;
		jawsState = RobotMap.jawsSolenoid.get() ? Direction.OPEN : Direction.CLOSE;
	}

    /**
    * Map position from inches to controller ticks
    */
    private static int revolutionsToSrx(double degrees) {
		return (int)(degrees * PULSES_PER_DEGREE);
    }

    /**
    * Map position from controller ticks to inches from bottom
    */
    private static double srxToPosition(int ticks) {
        return ticks / PULSES_PER_DEGREE;
    }

    /**
    * Map speed from inches per second to controller format
    */
    private static double speedToSrx(double degreesPerSecond) {
        return degreesPerSecond * PULSES_PER_DEGREE_PER_SECOND;
    }
    
    public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}    
	public double readPosition() {
		return srxToPosition(motorController.readPosition());
	}
    
	public void disable() {
		MotorControllerState oldState = motorController.getState();
		motorController.disable();
		if(oldState != motorController.getState()) {
			log.print("disable [transitioned from " + oldState + " to " + motorController.getState() + ".");
		}
	}

	// [ACTION]
	public void holdCurrentPosition() {
		MotorControllerState oldState = motorController.getState();
		motorController.holdCurrentPosition();
		if(oldState != motorController.getState()) {
			log.print("holdCurrentPosition [transitioned from " + oldState + " to " + motorController.getState() + ".");
		}
	}

	// [ACTION]
	public void setPosition(double inches) {
		MotorControllerState oldState = motorController.getState();
		double newPositionInches = inches;
		int newPositionSrx = revolutionsToSrx(inches);
		motorController.setPosition(revolutionsToSrx(inches));
		if(motorController.getState() == MotorControllerState.HOLDING_POSITION) {
			// TODO: Add flags
			// NB: this version shows old -> new transitions
			log.print("setPosition ["
					+ "PositionInches (" + lastPositionInches + " -> " + newPositionInches + ")" 
					+ ", PositionSrx (" + lastPositionSrx + " -> " + newPositionSrx + ")"
					+ "]");
		}
		// TODO: Add flags
		// NB: this version shows new values only
		log.print("setPosition ["
				+ "PositionInches=" + newPositionInches 
				+ ", PositionSrx=" + newPositionSrx
				+ "]");
		this.lastPositionInches = newPositionInches;
		this.lastPositionSrx = newPositionSrx;
	}

	/*
	 * Set direction and speed in one call, without enabling movement.
	 */
	public void setMoveConfiguration(Direction newDirection, double newSpeedInchesPerSecond) {
		MotorControllerState oldState = motorController.getState();
		double     newSpeedSrx = speedToSrx(newSpeedInchesPerSecond);
		if(motorController.getState() == MotorControllerState.MOVING) {
			// TODO: Add flags
			// NB: this version shows transition
			log.print("setMoveConfiguration ["
					+ "Direction (" + this.direction + " -> " + newDirection + ")" 
					+ ", InchesPerSecond (" + this.speedInchesPerSecond + " -> " + newSpeedInchesPerSecond + ")"
					+ ", Velocity (" + speedSrx + " -> " + newSpeedSrx + ")"
					+ "]");
		}
		// TODO: Add flags
		// NB: this version shows new values only
		log.print("setMoveConfiguration ["
				+ "Direction=" + newDirection 
				+ ", InchesPerSecond=" + newSpeedInchesPerSecond
				+ ", Velocity=" + newSpeedSrx
				+ "]");
		// If we are currently moving by velocity and the value changes, update the Talon
		if(motorController.getState() == MotorControllerState.MOVING) {
			motorController.move(newDirection, newSpeedSrx);
		}
		this.direction = newDirection;
		this.speedInchesPerSecond = newSpeedInchesPerSecond;
		this.speedSrx = newSpeedSrx;
	}
    
	// Set the speed (will update the controller only if we are already in MOVING state (TalonMode.Velocity)
	public void setArmDirection(Direction newDirection) {
		setMoveConfiguration(newDirection, speedInchesPerSecond);
	}

	// Set the speed (will update the controller only if we are already in MOVING state (TalonMode.Velocity)
	public void setSpeedInchesPerSecond(double newSpeedInchesPerSecond) {
		setMoveConfiguration(this.direction, newSpeedInchesPerSecond);
	}

	// [ACTION]
	public void move(Direction newDirection, double newSpeedInchesPerSecond) {
		/* Set the new speed and/or direction.  If we are already moving, 
		 * setMoveConfiguration will update the Talon.  If not, we have to tell it 
		 * to start moving.
		 */
		setMoveConfiguration(newDirection, newSpeedInchesPerSecond);
		if(motorController.getState() != MotorControllerState.MOVING) {
			motorController.move(this.direction, this.speedSrx);
		}
	}

	// [ACTION] - Change to moving state as necessary and update direction
	public void move(Direction newDirection) {
		move(newDirection, this.speedInchesPerSecond);
	}

	// [ACTION] - Change to moving state using configured direction and speed
	public void move() {
		move(this.direction, this.speedInchesPerSecond);
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new MoveElevator());
	}
	public double getSpeedInchesPerSecond() {
		return speedInchesPerSecond;
	}
	@Override
	public void periodic() {}
	
	@Override
	public String toString() {
		return label;
	}
    
	// Manage jaws
	public static void setJaws(Direction direction) {
		if (jawsState != direction) {
			RobotMap.jawsSolenoid.set(direction == Direction.CLOSE ? true : false);
			jawsState = direction;
		}
	}
	
	public static void setIntakeDirection(Direction direction) {
		intakeDirection = direction;
        if (!intakeIsHalted) spinIntake();  // commit state if not halted
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
}
