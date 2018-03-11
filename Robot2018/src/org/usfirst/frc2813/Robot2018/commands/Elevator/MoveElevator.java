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
		this.state = MotorControllerState.HOLDING_POSITION;
		this.direction = Direction.NEUTRAL; // Not used
		this.positionInInches = 0; // Not used
		requires(Robot.elevator);
	}

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveElevator(Direction direction) {
		this.state = MotorControllerState.MOVING;
		this.direction = direction; 
		this.positionInInches = 0; // Not used
		logger.info("MoveElevator-" + direction);
		requires(Robot.elevator);
	}

	/*
	 * Move to a specific position and hold the position
	 */
	public MoveElevator(double positionInInches) {
		this.state = MotorControllerState.SET_POSITION;
		this.positionInInches = positionInInches;
		this.direction = Direction.NEUTRAL; // Not used
		logger.info("MoveElevator-" + positionInInches);
		requires(Robot.elevator);
	}

	// Called repeatedly when this Command is scheduled to run
	// @Override
	protected void execute() {
		logger.finer("in execute");
		switch(state) {
		case HOLDING_POSITION:
			Robot.elevator.holdCurrentPosition();
			break;
		case MOVING:
			Robot.elevator.move(direction);
			break;
		case SET_POSITION:
			Robot.elevator.setPosition(positionInInches);
			break;
		case DISABLED:
			Robot.elevator.disable();
			break;
		default:
			throw new IllegalArgumentException("Unsupported state: " + state);
		}
	}
	// Make this return true when this Command no longer needs to run execute()
	//@Override
	protected boolean isFinished() {
		// All of the move commands run indefinitely until cancelled
//		log.print("isFinished is returning false");
		return false;
	}
	
	@Override
	protected void end() {
		logger.finer("in end");
	}
	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		if(state == MotorControllerState.MOVING || state == MotorControllerState.SET_POSITION) {
			logger.info("was moving, so changing to HOLD.");
			Robot.elevator.holdCurrentPosition();
		}
	}
}
