package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem baseclass for subsystems which move in two direction but
 * are positionless.
 * 
 * This subsytem can halt or move in a direction.
 * Speed can be set separately.
 */
public abstract class SubsystemDirectionSpeed extends Subsystem {
	public boolean encoderFunctional = true;
	protected static Log log;
	protected static Direction direction;
	protected static double speed;
	protected static boolean isHalted;
	
	/**
	 * GEOMETRY - define in terms of your motor controller
	 * You must define these in your subsystem. You must choose units for
	 *  speed.
	 */
    protected static double DEFAULT_SPEED;
    protected static double PULSES_PER_UNIT_POSITION; // map user speed to controller speed

    /**
     * Constructor. configure your motor controller and set your
     * geometry and state. Override and call super. Note that
     * to change any of the defaults set here, call super first!
     */
	public SubsystemDirectionSpeed() {
		log = new Log(this.getClass().getName());

		// track state and change as required. Start in moving so initialize can halt
        direction = Direction.DOWN;
        speed = DEFAULT_SPEED;
        isHalted = true;
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
        if (!isHalted) move();  // commit state if not halted
    }

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
	 * Start the subsystem moving!
	 */
	public void move() {
        if (!encoderFunctional) return;

		isHalted = false;
		setControllerDirection(direction);
		log.print("Starting movement direction: " + direction + " Speed: " + speed);
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