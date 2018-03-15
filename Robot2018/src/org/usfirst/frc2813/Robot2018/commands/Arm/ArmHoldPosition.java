package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;

/**
 * Maintain arm position. Arm does this actively with
 * PID via the motor controller, so no further intervention is
 * required to maintain this.
 */
public class ArmHoldPosition extends GearheadsCommand {
	public ArmHoldPosition() {
		requires(Robot.arm);
	}

	// @Override
	protected void initialize() {
		logger.finer("in initialize");
		if(!Robot.arm.getMotorControllerState().equals(MotorControllerState.HOLDING_POSITION)) {
			Robot.arm.holdCurrentPosition();
		}
		setInterruptible(true); // more to document the intent, than necessary
	}

	// @Override
	protected boolean isFinished() {
		return false;
	}
}
