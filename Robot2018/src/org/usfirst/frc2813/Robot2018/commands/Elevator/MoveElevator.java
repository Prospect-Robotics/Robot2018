// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Elevator;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.Log;
import org.usfirst.frc2813.Robot2018.TalonState;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

/**
 * Move or maintain elevator position.
 * No argument to halt.
 * Direction to move in a direction.
 * Position to move to that position in inches above floor.
 */
public class MoveElevator extends Command {
	// Desired state/action
	private final TalonState state;
	// direction, only valid if we are moving at a constant speed
	private final Direction direction;
	// position, only valid if we are moving to a set position
	private final double positionInInches;
	// command logger
	private final Log log;
	/*
	 * Hold the current position
	 */
	public MoveElevator() {
		this.state = TalonState.HOLDING_POSITION;
		this.direction = Direction.NEUTRAL; // Not used
		this.positionInInches = 0; // Not used
		this.log = new Log("MoveElevator-HOLD_POSITION");
		requires(Robot.elevator);
	}
	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveElevator(Direction direction) {
		this.state = TalonState.MOVING;
		this.direction = direction; 
		this.positionInInches = 0; // Not used
		this.log = new Log("MoveElevator-" + direction);
		requires(Robot.elevator);
	}
	/*
	 * Move to a specific position and hold the position
	 */
	public MoveElevator(double positionInInches) {
		this.state = TalonState.SET_POSITION;
		this.positionInInches = positionInInches;
		this.direction = Direction.NEUTRAL; // Not used
		this.log = new Log("MoveElevator-" + positionInInches);
		requires(Robot.elevator);
	}
	// Called repeatedly when this Command is scheduled to run
	// @Override
	protected void execute() {
		log.print("execute");
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
		log.print("end");
	}
	@Override
	protected void interrupted() {
		log.print("interrupted");
		if(state == TalonState.MOVING || state == TalonState.SET_POSITION) {
			log.print("was moving, so changing to HOLD.");
			Robot.elevator.holdCurrentPosition();
		}
	}
}
