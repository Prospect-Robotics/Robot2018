package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Log;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Subsystem baseclass for simple subsystems which have one of
 * two states.
 *
 */
public abstract class SubsystemBinary extends Subsystem {
	public boolean encoderFunctional = true;
	protected static Log log;
	protected static Direction state;

    /**
     * Constructor. configure your motor controller and set your
     * geometry and state. Override and call super. Note that
     * to change any of the defaults set here, call super first!
     */
	public SubsystemBinary() {
		log = new Log(this.getClass().getName());

		// track state and change as required. Start in moving so initialize can halt
        state = Direction.NEGATIVE;
	}

	/**
	 * Abstract method to set controller state
	 * @param direction
	 */
	protected abstract void setControllerState(Direction direction);

	/**
	 * Abstract method to get controller state
	 * @param direction
	 */
	protected abstract Direction getControllerState();

	/**
	 * user facing command to change subsystem state
	 */
	public void setState(Direction direction) {
        if (!encoderFunctional) return;

		state = direction;
		setControllerState(direction);
	}
}
