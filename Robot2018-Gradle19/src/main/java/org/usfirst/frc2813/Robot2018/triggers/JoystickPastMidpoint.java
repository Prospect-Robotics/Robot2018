package org.usfirst.frc2813.Robot2018.triggers;

import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *  Return true if the joystick is past 50% in a given direction
 */
public class JoystickPastMidpoint extends Trigger {
	private final Joystick stick;
	private final Direction direction;
	
	public JoystickPastMidpoint(Joystick stick, Direction direction) {
		this.stick=stick;
		this.direction = direction;
	}
	
	/*
	 * Invert the value from the joystick as we consider 
	 * forward to be positive by convention, despite what the backwards HID guys think.  
	 */
	private double getShifterValue() {
		return -stick.getY();
	}
	
    public boolean get() {
    	if(direction.equals(Direction.FORWARD)) {
    		return getShifterValue() >= 0.5;
    	} else {
    		return getShifterValue() <= -0.5;
    	}
    }
}
