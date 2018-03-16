package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;

/**
 * Maintain elevator position. Elevator does this actively with
 * PID via the motor controller, so no further intervention is
 * required to maintain this.
 */
public class ElevatorHoldPosition extends GearheadsCommand {
	public ElevatorHoldPosition() {
		requires(Robot.elevator);
	}

	// @Override
	protected void initialize() {
		Logger.debug("in execute");
		if(!Robot.elevator.getMotorControllerState().equals(MotorControllerState.HOLDING_POSITION)) {
			Robot.elevator.holdCurrentPosition();
		}
		setInterruptible(true);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
