// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Elevator;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;
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
	/*
	 * Hold the current position
	 */
	public MoveElevator() {
		this.state = TalonState.HOLDING_POSITION;
		this.direction = Direction.NEUTRAL; // Not used
		this.positionInInches = 0; // Not used
		requires(Robot.elevator);
	}
	/*
	 * Move in a particular direction until stopped, hold the velocity
	 */
	public MoveElevator(Direction direction) {
		this.state = TalonState.MOVING;
		this.direction = direction; 
		this.positionInInches = 0; // Not used
	}
	/*
	 * Move to a specific position and hold the position
	 */
	public MoveElevator(double positionInInches) {
		this.state = TalonState.SET_POSITION;
		this.positionInInches = positionInInches;
		this.direction = Direction.NEUTRAL; // Not used
	}
	// Called repeatedly when this Command is scheduled to run
	// @Override
	protected void execute() {
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
		// Turns out that it's all automatic once you ran execute once.
		return true;
	}
	@Override
	protected void end() {}
	@Override
	protected void interrupted() {
		
	}
}
