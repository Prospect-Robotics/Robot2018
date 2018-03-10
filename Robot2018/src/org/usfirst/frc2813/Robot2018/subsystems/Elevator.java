package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.TalonState;
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
	private static final double INCHES_PER_REVOLUTION = Math.PI * 1.25;
	private static final double PULSES_PER_INCH = Talon.PULSES_PER_REVOLUTION / INCHES_PER_REVOLUTION;
    private static final double DEFAULT_SPEED = 12;

	private Log log;
	private Talon motorController;
	/**
	 * ELEVATOR GEOMETRY
	 * TODO: find maximum allowable elevator height; 24 is only a placeholder.
	 */
	public final double HEIGHT = 24; // inches
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

	public Elevator() {
		this.label = "Elevator";
		log = new Log(this.label);
		motorController = new Talon(RobotMap.srxElevator, this.label, log);
		motorController.configHardLimitSwitch(Direction.BACKWARD);
		motorController.configSoftLimitSwitch(Direction.FORWARD, (int)(HEIGHT * PULSES_PER_INCH));
  	    motorController.setPID(Talon.MAINTAIN_PID_LOOP_IDX, 0.8, 0, 0);
	    motorController.setPID(Talon.MOVE_PIDLOOP_IDX, 0.75, 0.01, 40);
	}

    /**
    * Map position from inches to controller ticks
    */
    private static int inchesToSrx(double inchesFromBotton) {
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
    private double speedToSrx(double inchesPerSecond) {
        return inchesPerSecond * PULSES_PER_INCH / 10.0;
    }

	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	public double readPosition() {
		return srxToPosition(motorController.readPosition());
	}

	// [ACTION]
	public void disable() {
		TalonState oldState = motorController.getState();
		motorController.disable();
		if(oldState != motorController.getState()) {
			log.print("disable [transitioned from " + oldState + " to " + motorController.getState() + ".");
		}
	}

	// [ACTION]
	public void holdCurrentPosition() {
		TalonState oldState = motorController.getState();
		motorController.holdCurrentPosition();
		if(oldState != motorController.getState()) {
			log.print("holdCurrentPosition [transitioned from " + oldState + " to " + motorController.getState() + ".");
		}
	}

	// [ACTION]
	public void setPosition(double inches) {
		TalonState oldState = motorController.getState();
		double newPositionInches = inches;
		int newPositionSrx = inchesToSrx(inches);
		motorController.setPosition(inchesToSrx(inches));
		if(motorController.getState() == TalonState.HOLDING_POSITION) {
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
		TalonState oldState = motorController.getState();
		double     newSpeedSrx = speedToSrx(newSpeedInchesPerSecond);
		if(motorController.getState() == TalonState.MOVING) {
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
		if(motorController.getState() == TalonState.MOVING) {
			motorController.move(newDirection, newSpeedSrx);
		}
		this.direction = newDirection;
		this.speedInchesPerSecond = newSpeedInchesPerSecond;
		this.speedSrx = newSpeedSrx;
	}

	// Set the speed (will update the controller only if we are already in MOVING state (TalonMode.Velocity)
	public void setDirection(Direction newDirection) {
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
		if(motorController.getState() != TalonState.MOVING) {
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
}
