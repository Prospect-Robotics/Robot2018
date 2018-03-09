package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

/**
 * Move elevator to bottom position
 */
public class FloorElevator extends Command {
	private static final double FEET_PER_SECOND = -1;
	/**
	 * Bring elevator down to limit switch
	 */
	public FloorElevator() {
		// Use requires() here to declare subsystem dependencies
		// eg. requires(chassis);
		requires(Robot.elevator);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		RobotMap.srxElevator.selectProfileSlot(1, Constants.movePIDLoopIdx);
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		 Elevator.setSpeedFeetPerSecond(FEET_PER_SECOND);
		 Elevator.move();
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return Elevator.isLimitSwitchPressed() == true;
	}
}
