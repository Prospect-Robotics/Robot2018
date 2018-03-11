package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.MotorControllerState;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

/**
 * Move or maintain elevator position.
 * No argument to halt.
 * Direction to move in a direction.
 * Position to move to that position in inches above floor.
 */
public class MoveElevator extends GearheadsCommand {
	// Desired state/action
	private final MotorControllerState state;
	// direction, only valid if we are moving at a constant speed
	private final Direction direction;
	// position, only valid if we are moving to a set position
	private final double positionInInches;

	/*
	 * Hold the current position
	 */
	public MoveElevator() {
		state = MotorControllerState.HOLDING_POSITION;
		direction = Direction.NEUTRAL; // Not used
		positionInInches = 0; // Not used
		requires(Robot.elevator);
	}

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveElevator(Direction direction) {
		state = MotorControllerState.MOVING;
		this.direction = direction; 
		positionInInches = 0; // Not used
		logger.info("MoveElevator-" + direction);
		requires(Robot.elevator);
	}

	/*
	 * Move to a specific position and hold the position
	 */
	public MoveElevator(double positionInInches) {
		state = MotorControllerState.SET_POSITION;
		this.positionInInches = positionInInches;
		if (Robot.arm.readPosition() < positionInInches) {
			direction = Direction.UP;
		}
		else {
			direction = Direction.DOWN;
		}
		logger.info("MoveElevator-" + positionInInches);
		requires(Robot.elevator);
	}

	// Called once to begin the command
	// @Override
	protected void initialize() {
		logger.finer("in execute");
		switch(state) {
		case HOLDING_POSITION:
			Robot.elevator.holdCurrentPosition();
			break;
		case MOVING:
			Robot.elevator.setDirection(direction);
			break;
		case SET_POSITION:
			Robot.elevator.setPosition(positionInInches);
			break;
		case DISABLED:
			Robot.elevator.disable();
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
			targetPosition = positionInInches;
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
