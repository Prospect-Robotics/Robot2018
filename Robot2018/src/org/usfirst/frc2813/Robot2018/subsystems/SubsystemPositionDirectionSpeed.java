package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.TalonSensorPhase;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

/**
 * Subsystem base class for subsystems which move in two direction
 * over a range of positions.
 *
 * This subsystem can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * 
 * The public interfaces are:
 *     readPosition     - return the position in user defined units
 *     readDefaultSpeed - return the default speed
 *     disable          - disable all active code
 *     holdPosition     - actively hold position with PID
 *     moveToPosition   - move to a position. This is normally a fire and forget
 *                        command done at the motor controller level.
 *     moveInDirectionAtSpeed - move in the given direction at a
 *     given speed move  - at the last set or default speed in
 *     the given direction
 */
public abstract class SubsystemPositionDirectionSpeed extends GearheadsSubsystem {
	// State information
	protected Direction direction, oldDirection;
	double speed;
	protected double oldSpeed;
	protected int position, oldPosition;
	protected MotorControllerState state, oldState;
    protected double DEFAULT_SPEED;
    protected double MAX_POSITION;
	protected double MIN_POSITION;
	protected double PULSES_PER_UNIT_POSITION;
	protected double PULSES_PER_UNIT_POSITION_PER_TIME;
    
	/**
	 * configure your motor controller and set your
	 * geometry and state, then you MUST call initialize
	 * @param subsystemAxisConfiguration - This describes the way the subsystem presents the elevator vertical axis to the calling API.
	 */
	protected SubsystemPositionDirectionSpeed() {
		// track state and change as required. Start in moving so initialize can halt
		direction = Direction.NEUTRAL;
		position = readControllerPosition();
		speed = DEFAULT_SPEED;
		oldSpeed = 0;
		oldDirection = Direction.NEUTRAL;
		oldState = MotorControllerState.DISABLED;
		state = readMotorControllerState();
	}

	/**
	 * Abstract method to read controller state
	 * @return MotorControllerState
	 */
	protected abstract MotorControllerState readMotorControllerState();

	/**
	 * Abstract method to read controller position, expect "sensor" units
	 * @return position in controller units
	 */
	protected abstract int readControllerPosition();

	/**
	 * Abstract method to set controller position 
	 * TODO: convert to "sensor units" before setting
	 */
	protected abstract void setControllerPosition(int position2);

	/**
	 * Abstract method to set controller speed and direction
	 * @param speedParam
	 */
	protected abstract void setControllerDirectionAndSpeed(Direction direction, double speedParam);

	/**
	 * Abstract method to halt the controller movement
	 * NOTE: if a physical limit switch is set, it may
	 * be wise to set the controller position
	 */
	protected abstract void disableController();

	/**
	 * Abstract method to halt the controller movement
	 * NOTE: if a physical limit switch is set, it may
	 * be wise to set the controller position
	 */
	protected abstract void holdControllerPosition();

	/**
	 * Read the current position in distance units
	 * @return
	 */
	public double readPosition() {
		return readControllerPosition();
	}
	/*
	 * Need to be able to retrieve what we believe is the state
	 */
	public MotorControllerState getMotorControllerState() {
		return state;
	}

	protected boolean isStateTransitionAllowed(MotorControllerState newState) {
		// Validate the state transition before we do anything
		switch(state) {
		case DISABLED:
			if (state == newState) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state.", state, newState));
				new Exception().printStackTrace();
				return false;
			}
			break;
		case HOLDING_POSITION:
			if (state == newState) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state.", state, newState));
				new Exception().printStackTrace();
				return false;
			}
			break;
		case MOVING:
			if ((state == newState) && (oldSpeed == speed) && (oldDirection == direction)) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state, with no change in direction or speed.", state, newState));
				new Exception().printStackTrace();
				return false;
			}
			break;
		case SET_POSITION:
			if ((state == newState) && (oldPosition == position)) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state, with no change in position.", state, newState));
				new Exception().printStackTrace();
				return false;
			}
			break;
		}
		return true;
	}
	/**
	 * All device change come through here first.
	 * Log request
	 * Validate the actual actually changes something
	 * make the change
	 * flush old state so we do not get confused next time
	 * 
	 * @param newState
	 * NOTE: state variables other than the state enum are already updated,
	 * with the old state in oldSpeed, oldPosition and oldDirection
	 * @return true if state change occurred
	 */
	protected boolean changeState(MotorControllerState newState) {
		logger.fine(String.format("Changing state: encoderFunctional: %s, " +
								 "old state: %s, new state: %s, old speed: %s, new speed: %s" +
								 "old direction: %s, new direction %s, old position: %s, new position %s",
				encoderFunctional, state, newState, oldSpeed, speed, oldDirection, direction, oldPosition, position));
		if (!encoderFunctional) {
			disableController();
			logger.warning("encoder not functional. Refusing action.");
			return false;	
		}
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(newState)) {
			return false;
		}

		// Check state transitions are valid before executing them
		oldState = state;
		state = newState;
		
		// Check that state change is actually changing something. If so, do it.
		switch(state) {
		case DISABLED:
			disableController();
			break;
		case HOLDING_POSITION:
			holdControllerPosition();
			break;
		case MOVING:
			setControllerDirectionAndSpeed(direction, speed);
			break;
		case SET_POSITION:
			setControllerPosition(position);
			break;
		}
		oldPosition = position;
		oldSpeed = speed;
		oldDirection = direction;
		return true;
	}
	
	/*************************************************************
	 *  And now for the public commands that can transition the subsystem
	 */	

	/**
	 *  [ACTION] Disable the device. Required to handle run away bots
	 */
	public void disable() {
		changeState(MotorControllerState.DISABLED);
	}

	/**
	 *  [ACTION] move in direction at current speed
	 * @param newDirection
	 */
	public void moveInDirection(Direction newDirection) {
		oldDirection = direction;
		direction = newDirection;
		changeState(MotorControllerState.MOVING);
	}

	/**
	 *  [ACTION] Move in direction at speed
	 * @param newSpeed
	 * @param newDirection
	 */
	public void moveInDirectionAtSpeed(Direction newDirection, double newSpeed) {
		oldSpeed = speed;
		speed = newSpeed;
		oldDirection = direction;
		direction = newDirection;
		changeState(MotorControllerState.MOVING);
	}

	/**
	 *  [ACTION] Move to the absolutePosition
	 * @param newDirection
	 */
	public void moveToPosition(int newPosition) {
		oldPosition = position;
		position = newPosition;
		changeState(MotorControllerState.SET_POSITION);
	}

	/**
	 * [ACTION]
	 * stop moving - this is active as we often require pid to resist gravity
	 */
	public void holdCurrentPosition() {
		changeState(MotorControllerState.HOLDING_POSITION);
	}
	
	public void dumpState() {
		/*logger.fine*/
		System.out.println(String.format("STATE: [encoderFunctional: %s, state: %s, speed: %s, direction: %s, position %s]",
				encoderFunctional, state, speed, direction, position));
	}
	/*
	 * Is the sensor phase reversed 
	 */
	public TalonSensorPhase getSensorPhase() {
		return TalonSensorPhase.Normal;
	}
}

