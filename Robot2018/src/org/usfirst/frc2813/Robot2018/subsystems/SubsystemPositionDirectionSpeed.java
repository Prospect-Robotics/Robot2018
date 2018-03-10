package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem baseclass for subsystems which move in two direction
 * over a range of positions.
 * 
 * This subsytem can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 */
public abstract class SubsystemPositionDirectionSpeed extends Subsystem {
	public boolean encoderFunctional = true;
	protected static Log log;
	protected static Direction direction;
	protected static double speed;
	protected static double position;
    protected static boolean positionMode;  // true if we are moving to a position
                                          // false if we move by direction and speed
	protected static boolean isHalted;
	
	/**
	 * GEOMETRY - define in terms of your motor controller
	 * You must define these in your subsystem. You must choose units for
	 * distance and speed and define default min and max.
	 */
	public static double MAX_POSITION;
	public static double MIN_POSITION;
	protected static double PULSES_PER_UNIT_POSITION;
    protected static double DEFAULT_SPEED;

    /**
     * Constructor. configure your motor controller and set your
     * geometry and state. Override and call super. Note that
     * to change any of the defaults set here, call super first!
     */
	public SubsystemPositionDirectionSpeed() {
		log = new Log(this.getClass().getName());

		// track state and change as required. Start in moving so initialize can halt
        direction = Direction.DOWN;
        position = MIN_POSITION; 
        speed = DEFAULT_SPEED;
        positionMode = false;
        isHalted = true;
	}

    /**
    * Map position from inches to controller ticks
    */
    protected static int positionToController(double pos) {
		return (int)(pos * PULSES_PER_UNIT_POSITION);
    }

    /**
    * Map position from controller ticks to distance units
    */
    protected static double controllerToPosition(int ticks) {
        return ticks / PULSES_PER_UNIT_POSITION;
    }

    /**
    * Map speed from speed units to controller ticks
    */
    protected static double speedToController(double speedParam) {
        return speedParam * PULSES_PER_UNIT_POSITION;
    }

	public void setSpeed() {
        setSpeed(DEFAULT_SPEED);
    }

	/**
	 * This is the external interface to set the speed
	 * @param speedParam
	 */
	public void setSpeed(double speedParam) {
        speed = speedParam;
        if (!isHalted) move();  // commit state if not halted
	}

	/**
	 * This is the external interface to set the direction
	 * @param directionParam
	 */
	public void setDirection(Direction directionParam) {
        direction = directionParam;
        positionMode = false;
        if (!isHalted) move();  // commit state if not halted
    }

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
	 * Abstract method to set controller speed
	 * @param speedParam
	 */
	protected abstract void setControllerSpeed(int speedParam);

	/**
	 * Abstract method to set controller direction
	 * @param directionParam
	 */
	protected abstract void setControllerDirection(Direction directionParam);

	/**
	 * Abstract method to halt the controller movement
	 * NOTE: if a physical limit switch is set, it may
	 * be wise to set the controller position
	 */
	protected abstract void haltController();

	/**
	 * Read the current position in distance units
	 * @return
	 */
	public double readPosition() {
		return controllerToPosition(readControllerPosition());
	}
	
	/**
	 * Set position in distance units
	 */
	public void setPosition(double pos) {
        positionMode = true;
        position = pos;
        if (!isHalted) move();  // commit state if not halted
	}

	/**
	 * Start the subsystem moving!
	 */
	public void move() {
        if (!encoderFunctional) return;

		isHalted = false;
        if (positionMode) {
            setControllerPosition(positionToController(position));
            log.print("Starting movement. Target position: " + position);
        }
        else {
            setControllerDirection(direction);
            log.print("Starting movement. Speed: " + speed);
        }
	}

    public void move(Direction directionParam) {
        direction = directionParam;
        move();
    }

    public void move(double speedParam) {
        speed = speedParam;
        move();
    }

    public void move(double speedParam, Direction directionParam) {
        speed = speedParam;
        direction = directionParam;
        move();
    }

	/**
	 * stop moving - this is active as we often require pid to resist gravity
	 */
	public void halt() {
        if (!encoderFunctional) return;

		isHalted = true;
		haltController();
	}
}