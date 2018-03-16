package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonSensorPhase;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
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
	static {
		Logger.addMe();
	}
	// State information
	protected final LengthUOM subsystemLengthUnits;
	protected final LengthUOM sensorLengthUnits;
	protected final LengthUOM motorLengthUnits;
	protected final RateUOM subsystemRateUnits;
	protected final RateUOM sensorRateUnits;
	protected final RateUOM motorRateUnits;

	protected Direction direction;
	protected Direction oldDirection;
	protected Rate speed;
	protected Rate oldSpeed;
	protected Length position;
	protected Length oldPosition;
	protected MotorControllerState oldState;
	protected MotorControllerState state;

	/**
	 * configure your motor controller and set your
	 * geometry and state, then you MUST call initialize
	 * TODO: Pass in axisConfiguration instead of units
	 */
	protected SubsystemPositionDirectionSpeed(LengthUOM subsystemLengthUnits, LengthUOM sensorLengthUnits, LengthUOM motorLengthUnits, RateUOM subsystemRateUnits, RateUOM sensorRateUnits, RateUOM motorRateUnits) {
		this.subsystemLengthUnits = subsystemLengthUnits;
		this.sensorLengthUnits = sensorLengthUnits;
		this.motorLengthUnits = motorLengthUnits;
		this.subsystemRateUnits = subsystemRateUnits;
		this.sensorRateUnits = sensorRateUnits;
		this.motorRateUnits = motorRateUnits;
	}

	/*
	 * Run initialization at the end of the subclass's constructor!
	 */
	protected void initialize() {		
		// track state and change as required. Start in moving so initialize can halt
		direction = Direction.NEUTRAL;
		position = getCurrentPositionInSubsystemUnits();
		speed = getDefaultSpeed();
		oldSpeed = RateUOM.InchesPerSecond.create(0);
		oldDirection = Direction.NEUTRAL;
		oldState = MotorControllerState.DISABLED;
		state = readMotorControllerState();
	}
	// TODO: Replace with axisConfiguration
	protected abstract Rate getDefaultSpeed();
	/**
	 * Abstract method to read controller state
	 * @return MotorControllerState
	 */
	protected abstract MotorControllerState readMotorControllerState();
	/*
	 * Get the position
	 */
	protected abstract Length getCurrentPositionInSensorUnits();

	protected Length getTargetPositionInSubsystemUnits() {
		return position.convertTo(subsystemLengthUnits);
	}

	protected Length getTargetPositionInSensorUnits() {
		return toSensorUnits(position);
	}
	
	/* Set the position 
	 */
	protected abstract void setPosition(Length position);
	/**
	 * Abstract method to set controller speed and direction
	 * @param speedParam
	 */
	protected abstract void setControllerDirectionAndSpeed(Direction direction, Rate speedParam);
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
	/*
	 * Need to be able to retrieve what we believe is the state
	 */
	public MotorControllerState getMotorControllerState() {
		return state;
	}

	protected boolean isStateTransitionAllowed(MotorControllerState oldState, MotorControllerState newState) {
		// Validate the state transition before we do anything
		switch(newState) {
		case DISABLED:
			if (oldState == newState) {
				Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state.", oldState, newState);
				new Exception().printStackTrace();
				return false;
			}
			break;
		case HOLDING_POSITION:
			if (oldState == newState) {
				Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state.", oldState, newState);
				new Exception().printStackTrace();
				return false;
			}
			break;
		case MOVING:
			if ((oldState == newState) && (oldSpeed == speed) && (oldDirection == direction)) {
				Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state, with no change in direction or speed.", oldState, newState);
				new Exception().printStackTrace();
				return false;
			}
			break;
		case SET_POSITION:
			if ((oldState == newState) && (oldPosition == position)) {
				Logger.printFormat(LogType.WARNING, "bug in code: Transitioning from %s state to %s state, with no change in position.", oldState, newState);
				new Exception().printStackTrace();
				return false;
			}
			break;
		}
		return true;
	}

	protected boolean executeTransition(MotorControllerState oldState, MotorControllerState newState) {
		// Check that state change is actually changing something. If so, do it.
		switch(newState) {
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
			setPosition(position);
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
		Logger.formatDebug("Changing state: encoderFunctional: %s, " +
								 "old state: %s, new state: %s, old speed: %s, new speed: %s" +
								 "old direction: %s, new direction %s, old position: %s, new position %s",
				encoderFunctional, state, newState, oldSpeed, speed, oldDirection, direction, oldPosition, position);
		if (!encoderFunctional) {
			disableController();
			Logger.warning("encoder not functional. Refusing action.");
			return false;	
		}
		
		// Check that the state transition is legal before we do anything.
		if(!isStateTransitionAllowed(state, newState)) {
			return false;
		}

		// Execute the transition
		if(!executeTransition(oldState, newState)) {
			return false;
		}

		// Transition successful, save the state.
		oldState = state;
		oldPosition = position;
		oldSpeed = speed;
		oldDirection = direction;
		state = newState;
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
	public void moveInDirectionAtSpeed(Direction newDirection, Rate newSpeed) {
		oldSpeed = speed;
		speed = newSpeed;
		oldDirection = direction;
		direction = newDirection;
		changeState(MotorControllerState.MOVING);
		// TODO: Check rate is within limits from axisConfiguration
	}

	/**
	 *  [ACTION] Move to the absolutePosition
	 * @param newDirection
	 */
	public void moveToPosition(Length newPosition) {
		oldPosition = position;
		position = newPosition;
		changeState(MotorControllerState.SET_POSITION);
		// TODO: Check position is within limits from axisConfiguration
	}

	/**
	 * [ACTION]
	 * stop moving - this is active as we often require pid to resist gravity
	 */
	public void holdCurrentPosition() {
		changeState(MotorControllerState.HOLDING_POSITION);
	}
	
	public void dumpState() {
		/*Logger.debug*/
		System.out.println(String.format("STATE: [encoderFunctional: %s, state: %s, speed: %s, direction: %s, position %s]",
				encoderFunctional, state, speed, direction, position));
	}
	/*
	 * Is the sensor phase reversed 
	 */
	public TalonSensorPhase getSensorPhase() {
		return TalonSensorPhase.Normal;
	}
	
	// Convert a length to sensor units
	public final Length toSensorUnits(Length l) {
		return l.convertTo(sensorLengthUnits);
	}
	// Convert a length to motor units	
	public final Length toMotorUnits(Length l) {
		return l.convertTo(motorLengthUnits);
	}
	
	// Convert a length to display units	
	public final Length toSubsystemUnits(Length l) {
		return l.convertTo(subsystemLengthUnits);
	}
	
	// Convert a length to sensor units
	public final Rate toSensorUnits(Rate l) {
		return l.convertTo(sensorRateUnits);
	}
	// Convert a length to motor units	
	public final Rate toMotorUnits(Rate l) {
		return l.convertTo(motorRateUnits);
	}
	// Convert a length to display units	
	public final Rate toSubsystemUnits(Rate l) {
		return l.convertTo(subsystemRateUnits);
	}
	// Wrap a sensor unit value in a Length 	
	protected final Rate sensorUnitsToRate(double valueInSensorUnits) {
		return sensorRateUnits.create(valueInSensorUnits);
	}
	// Wrap a motor unit value in a Length 	
	protected final Rate motorUnitsToRate(double valueInMotorUnits) {
		return motorRateUnits.create(valueInMotorUnits);
	}
	// Wrap a display unit value in a Length 	
	protected final Rate subsystemUnitsToRate(double valueInSubsystemUnits) {
		return subsystemRateUnits.create(valueInSubsystemUnits);
	}
	// Wrap a sensor unit value in a Length 	
	protected final Length sensorUnitsToLength(double valueInSensorUnits) {
		return sensorLengthUnits.create(valueInSensorUnits);
	}
	// Wrap a motor unit value in a Length 	
	protected final Length motorUnitsToLength(double valueInMotorUnits) {
		return motorLengthUnits.create(valueInMotorUnits);
	}
	// Wrap a display unit value in a Length 	
	protected final Length subsystemUnitsToLength(double valueInSubsystemUnits) {
		return subsystemLengthUnits.create(valueInSubsystemUnits);
	}
	// Get controller position in subsystem units
	public final Length getCurrentPositionInSubsystemUnits() {
		return toSubsystemUnits(getCurrentPositionInSensorUnits());
	}
}

