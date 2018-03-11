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
	private final double positionInInches;
	
	/*
	 * Hold the current position
	 */
	public MoveArm() {
		this.state = MotorControllerState.HOLDING_POSITION;
		this.direction = Direction.NEUTRAL; // Not used
		this.positionInInches = 0; // Not used
		requires(Robot.arm);
	}

	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveArm(Direction direction) {
		this.state = MotorControllerState.MOVING;
		this.direction = direction; 
		this.positionInInches = 0; // Not used
		logger.info("MoveArm-" + direction);
		requires(Robot.arm);
	}

	/*
	 * Move to a specific position and hold the position
	 */
	public MoveArm(double positionInInches) {
		this.state = MotorControllerState.SET_POSITION;
		this.positionInInches = positionInInches;
		this.direction = Direction.NEUTRAL; // Not used
		logger.info("MoveArm-" + positionInInches);
		requires(Robot.arm);
	}

	// Called repeatedly when this Command is scheduled to run
	//@Override
	protected void execute() {
		logger.finer("in execute");
		switch(state) {
		case HOLDING_POSITION:
			Robot.arm.holdCurrentPosition();
			break;
		case MOVING:
			Robot.arm.move(direction);
			break;
		case SET_POSITION:
			Robot.arm.setPosition(positionInInches);
			break;
		case DISABLED:
			Robot.arm.disable();
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
		logger.finer("in end");}
	

	@Override
	protected void interrupted() {
		logger.finer("in interrupted");
		if(state == MotorControllerState.MOVING || state == MotorControllerState.SET_POSITION) {
			logger.info("was moving, so changing to HOLD.");
			Robot.arm.holdCurrentPosition();
		}
	}
}