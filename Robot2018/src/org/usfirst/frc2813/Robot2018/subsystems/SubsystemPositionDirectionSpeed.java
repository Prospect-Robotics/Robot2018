package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.MotorControllerState;

/**
 * Subsystem baseclass for subsystems which move in two direction
 * over a range of positions.
 *
 * This subsytem can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 */
public abstract class SubsystemPositionDirectionSpeed extends GearheadsSubsystem {
	protected Direction direction;
	protected Direction oldDirection; // for debugging
	protected double speed;
	protected double oldSpeed; // for debugging
	protected double position;
	protected double oldPosition; // for debugging
	protected MotorControllerState state;

	/**
	 * GEOMETRY - define in terms of your motor controller
	 * You must define these in your subsystem. You must choose units for
	 * distance and speed and define default min and max.
	 */
	public double MAX_POSITION;
	public double MIN_POSITION;
	protected double PULSES_PER_UNIT_POSITION;
	protected double PULSES_PER_UNIT_POSITION_PER_TIME;
	protected double DEFAULT_SPEED;

	/**
	 * configure your motor controller and set your
	 * geometry and state, then you MUST call initialize
	 */
	public void initialize() {
		// track state and change as required. Start in moving so initialize can halt
		direction = Direction.NEUTRAL;
		position = MIN_POSITION;
		speed = DEFAULT_SPEED;
		state = readMotorControllerState();
	}

	/**
	 * Map position from inches to controller ticks
	 */
	protected int positionToController(double pos) {
		return (int)(pos / PULSES_PER_UNIT_POSITION);
	}

	/**
	 * Map position from controller ticks to distance units
	 */
	protected double controllerToPosition(int ticks) {
		return ticks * PULSES_PER_UNIT_POSITION;
	}

	/**
	 * Map speed from speed units to controller ticks
	 */
	protected int speedToController(double speedParam) {
		return (int)(speedParam / PULSES_PER_UNIT_POSITION_PER_TIME);
	}

	/**
	 * Map speed from speed units to controller ticks
	 */
	protected double controllerToSpeed(int speedParam) {
		return speedParam * PULSES_PER_UNIT_POSITION_PER_TIME;
	}
	
	/**
	 * Abstract method to read contrller state
	 * @return MotorControllerState
	 */
	protected abstract MotorControllerState readMotorControllerState();

	/**
	 * Abstract method to read controller position
	 * @return position in controller units
	 */
	protected abstract int readControllerPosition();

	/**
	 * Abstract method to set controller position
	 * @param positionParam
	 */
	protected abstract void setControllerPosition(int positionParam);

	/**
	 * Abstract method to set controller speed and direction
	 * @param speedParam
	 */
	protected abstract void setControllerSpeedAndDirection(int speedParam);

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
		return controllerToPosition(readControllerPosition());
	}

	/**
	 * TODO: add more logging
	 * Method to change state. Depending on state transition, log
	 * Note that some transitions may be illegal. Disabled may only
	 * transition to holding_position
	 * @param newState
	 * @return true if state change occurred
	 */
	protected boolean changeState(MotorControllerState newState) {
		if (!encoderFunctional) return false;
		if (state == newState) return true;

		if (state.isDisabled() && !newState.isHoldingCurrentPosition()) {
			logger.severe("Illegal transition from disabled to " + newState);
			return false;
		}
		state = newState;
		switch(state) {
		case DISABLED:
			disableController();
			break;
		case HOLDING_POSITION:
			holdControllerPosition();
			break;
		case MOVING:
			setControllerSpeedAndDirection(speedToController(speed));
			break;
		case SET_POSITION:
			setControllerPosition(positionToController(position));
			break;
		}
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
		disableController();
	}

	/**
	 *  [ACTION] Set the speed to the default speed
	 */
	public void setSpeed() {
		setSpeed(DEFAULT_SPEED);
	}

	/**
	 *  [ACTION] Set the speed to a new speed
	 * @param newSpeed
	 */
	public void setSpeed(double newSpeed) {
		oldSpeed = speed;
		speed = newSpeed;
		if (!state.isDisabled()) {
			changeState(MotorControllerState.MOVING);
		}
	}

	/**
	 *  [ACTION] Set the direction
	 * @param newDirection
	 */
	public void setDirection(Direction newDirection) {
		oldDirection = direction;
		direction = newDirection;
		if (!state.isDisabled()) {
			changeState(MotorControllerState.MOVING);
		}
	}

	/**
	 * This is the external interface to set the direction
	 * @param newSpeed
	 * @param newDirection
	 */
	public void setSpeedAndDirection(double newSpeed, Direction newDirection) {
		oldSpeed = speed;
		speed = newSpeed;
		oldDirection = direction;
		direction = newDirection;
		if (!state.isDisabled()) {
			changeState(MotorControllerState.MOVING);
		}
	}

	/**
	 *  [ACTION] Set the absolutePosition
	 * @param newDirection
	 */
	public void setPosition(double newPosition) {
		oldPosition = position;
		position = newPosition;
		if (!state.isDisabled()) {
			changeState(MotorControllerState.SET_POSITION);
		}
	}

	/**
	 * [ACTION]
	 * stop moving - this is active as we often require pid to resist gravity
	 */
	public void holdCurrentPosition() {
		if (!state.isDisabled()) {
			changeState(MotorControllerState.SET_POSITION);
		}
	}
}
