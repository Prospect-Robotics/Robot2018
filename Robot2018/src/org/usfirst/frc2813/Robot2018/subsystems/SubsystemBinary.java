package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.units.Direction;

/**
 * Subsystem baseclass for simple subsystems which have one of
 * two states.
 */
public abstract class SubsystemBinary extends GearheadsSubsystem {
	protected static Direction state;

    /**
     * Constructor. configure your motor controller and set your
     * geometry and state. Override and call super. Note that
     * to change any of the defaults set here, call super first!
     */
	public SubsystemBinary() {
        state = getControllerState();
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
	/*
	 * Get the current state
	 */
	public Direction getState() {
		return getControllerState();
	}
	
}
