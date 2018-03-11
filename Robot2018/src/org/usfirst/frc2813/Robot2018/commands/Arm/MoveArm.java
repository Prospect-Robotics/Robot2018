package org.usfirst.frc2813.Robot2018.commands.Arm;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Move or maintain arm position.
 * No argument to halt.
 * Direction to move in a direction.
 * Position to move to that position in degrees from bottom.
 */
public class MoveArm extends GearheadsCommand {
	// Desired state/action
	private final MotorControllerState state;
	// direction, only valid if we are moving at a constant speed
	private final Direction direction;
	// position, only valid if we are moving to a set position
	private final double positionInDegrees;

	/*
	 * Hold the current position
	 */
	public MoveArm() {
		state = MotorControllerState.HOLDING_POSITION;
		direction = Direction.NEUTRAL; // Not used
		positionInDegrees = 0; // Not used
		requires(Robot.arm);
	}

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveArm(Direction direction) {
		state = MotorControllerState.MOVING;
		this.direction = direction;
		positionInDegrees = 0; // Not used
		logger.info("MoveElevator-" + direction);
		requires(Robot.arm);
	}

	/*
	 * Move to a specific position and hold the position
	 */
	public MoveArm(double positionInInches) {
		state = MotorControllerState.SET_POSITION;
		this.positionInDegrees = positionInInches;
		if (Robot.arm.readPosition() < positionInInches) {
			direction = Direction.UP;
		}
		else {
			direction = Direction.DOWN;
		}
		logger.info("MoveArm-" + positionInInches);
		requires(Robot.arm);
	}

	// Called once to begin the command
	// @Override
	protected void initialize() {
		logger.finer("in execute");
		switch(state) {
		case HOLDING_POSITION:
			Robot.arm.holdCurrentPosition();
			break;
		case MOVING:
			Robot.arm.setDirection(direction);
			break;
		case SET_POSITION:
			Robot.arm.setPosition(positionInDegrees);
			break;
		case DISABLED:
			Robot.arm.disable();
			break;
		}
	}
	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		double targetPosition = 0;
		double actualPosition;

		switch(state) {
		case DISABLED:
		case HOLDING_POSITION:
			return true;
		case MOVING:
			if (direction.isPositive()) {
				targetPosition = Robot.arm.MAX_POSITION;
			}
			else {
				targetPosition = Robot.arm.MIN_POSITION;
			}
			break;
		case SET_POSITION:
			targetPosition = positionInDegrees;
			break;
		}
		actualPosition = Robot.arm.readPosition();
		if (direction.isPositive()) {
			return actualPosition >= targetPosition;
		}
		return actualPosition <= targetPosition;
	}

	@Override
	protected void end() {
		logger.finer("in end");
	}

	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
	}
}
