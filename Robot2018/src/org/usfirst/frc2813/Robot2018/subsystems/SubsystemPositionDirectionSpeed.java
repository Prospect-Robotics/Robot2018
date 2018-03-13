package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.util.unit.Direction;

/**
 * Subsystem baseclass for subsystems which move in two direction
 * over a range of positions.
 *
 * This subsytem can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * 
 * The public interfaces are:
 *     readPosition     - return the position in user defined units
 *     readDefaultSpeed - return the default speed
 *     disable          - disable all active code
 *     holdPosition     - actively hold position with PID
 *     moveToPosition   - move to a position. This is normally a fire and forget
 *                        command done at the motor controller level.
 *     moveAtSpeedAndDirection - move in the given speed and direction
 *     moveInDirection  - move at the last set or default speed in the given direction
 */
public abstract class SubsystemPositionDirectionSpeed extends GearheadsSubsystem {
	protected Direction direction, oldDirection;
	protected double speed, oldSpeed;
	protected double position, oldPosition;
	protected MotorControllerState state, oldState;

	/**
	 * GEOMETRY - define in terms of your motor controller
	 * You must define these in your subsystem. You must choose units for
	 * distance and speed and define default min and max.
	 */
	public double MAX_POSITION;
	public double MIN_POSITION;
	protected double PULSES_PER_UNIT_POSITION;
	protected double PULSES_PER_UNIT_POSITION_PER_TIME;
	public double DEFAULT_SPEED;

	/**
	 * configure your motor controller and set your
	 * geometry and state, then you MUST call initialize
	 */
	protected void initialize() {
		// track state and change as required. Start in moving so initialize can halt
		direction = Direction.NEUTRAL;
		position = MIN_POSITION;
		speed = DEFAULT_SPEED;
		oldSpeed = 0;
		oldDirection = Direction.NEUTRAL;
		oldState = MotorControllerState.DISABLED;
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
	 * Abstract method to read controller state
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
	protected void changeState(MotorControllerState newState) {
		logger.fine(String.format("Changing state: encoderFunctional: %s, " +
								 "old state: %s, new state: %s, old speed: %s, new speed: %s" +
								 "old direction: %s, new direction %s, old position: %s, new position %s",
				encoderFunctional, state, newState, oldSpeed, speed, oldDirection, direction, oldPosition, position));
		if (!encoderFunctional) {
			logger.warning("encoder not functional. Refusing action.");
			return;	
		}

		oldState = state;
		state = newState;
		
		// Check that state change is actually changing something. If so, do it.
		switch(state) {
		case DISABLED:
			if (oldState == state) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state.", oldState, state));
				return;
			}
			disableController();
			break;
		case HOLDING_POSITION:
			if (oldState == state) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state.", oldState, state));
				return;
			}
			holdControllerPosition();
			break;
		case MOVING:
			if ((oldState == state) && (oldSpeed == speed) && (oldDirection == direction)) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state, with no change in direction or speed.", oldState, state));
				return;
			}
			setControllerSpeedAndDirection(speedToController(speed));
			break;
		case SET_POSITION:
			if ((oldState == state) && (oldPosition == position)) {
				logger.warning(String.format("bug in code: Transitioning from %s state to %s state, with no change in position.", oldState, state));
				return;
			}
			setControllerPosition(positionToController(position));
			break;
		}
		oldPosition = position;
		oldSpeed = speed;
		oldDirection = direction;
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
	public void moveAtSpeedAndDirection(double newSpeed, Direction newDirection) {
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
	public void moveToPosition(double newPosition) {
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
}
