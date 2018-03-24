package org.usfirst.frc2813.Robot2018.triggers;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *  Return true if the joystick is centered, within a given tolerance.
 *  
 *  @author Sean
 */
public class JoystickCentered extends Trigger {
	private final Joystick stick;
	private static final double DEADZONE = 0.01;
	
	public JoystickCentered(Joystick stick) {
		this.stick=stick;
	}
	
    public boolean get() {
        return stick.getMagnitude() <= DEADZONE;
    }
}
