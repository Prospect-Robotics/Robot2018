package org.usfirst.frc2813.Robot2018.subsystems;

import edu.wpi.first.wpilibj.Solenoid;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;

/**
 * Jaws subsystems to control robot jaws solenoid
 * 
 */
public class Jaws extends SubsystemBinary {
	static Solenoid jawsSolenoid;
	public Jaws() {
		super();
		jawsSolenoid = RobotMap.jawsSolenoid;
	}
	
	protected void setControllerState(Direction direction) {
		jawsSolenoid.set(direction.isPositive());
	}

	protected Direction getControllerState() {
		return jawsSolenoid.get() ? Direction.ON : Direction.OFF;
	}

	/**
	 * user facing command to change subsystem state
	 */
	public void setState(Direction direction) {
        if (!encoderFunctional) return;

		state = direction;
		setControllerState(direction);
	}

	@Override
	protected void initDefaultCommand() {}
}