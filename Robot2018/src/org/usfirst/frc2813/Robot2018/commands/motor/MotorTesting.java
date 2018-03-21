package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;

public class MotorTesting extends AbstractMotorInstantCommand {
	public MotorTesting(Motor motor) {
		super(motor, true);
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		Robot.arm.encoderRelativePositionTestingMode();
//		Robot.elevator.encoderRelativePositionTestingMode();
	}
}
