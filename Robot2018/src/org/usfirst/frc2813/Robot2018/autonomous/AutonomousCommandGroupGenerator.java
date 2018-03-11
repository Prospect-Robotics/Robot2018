// AutonomousCommand - a class to run autonomous mode

package org.usfirst.frc2813.Robot2018.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc2813.Robot2018.Direction;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroup;


/**
 * Generate a series of sequential commands to operate autonomously. Takes into
 * account game data. Used exclusively by AutonomousCommandGroup
 */
public class AutonomousCommandGroupGenerator {
	private int directionBias;  // used to share code between left/right
	private static final SendableChooser<Direction> positionSelector = new SendableChooser<Direction>();
	static {
		positionSelector.addDefault("LEFT", Direction.LEFT);
		positionSelector.addObject("CENTER", Direction.CENTER);
		positionSelector.addObject("RIGHT", Direction.RIGHT);
		SmartDashboard.putData("Which position is the robot in?", positionSelector);
	}
	/**
	 * Code to be run during the Autonomous 15 second period.
	 * This code uses the gameData from the driver station and a
	 * sendable chooser on the Smart Dashboard to decide which
	 * sequence to run. Called by AutonomousCommandGroup
	 */
	public AutonomousCommandGroupGenerator() {

		AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;
		// Read our location on the field
		Direction position = positionSelector.getSelected();

		 // allows left->right and right->left to share code
		directionBias = (position == Direction.LEFT) ? 1 : -1;

		if (RobotMap.gameData.getScale() == Direction.OFF) {
			// there is no game data. Cross the auto line
			autoCmdList.driveForward(50);
		}

		if (position == RobotMap.gameData.getScale()) {
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(150);
			autoCmdList.turnRight(90 * directionBias);
			autoCmdList.raiseElevator();
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();
		}
		else if (position != Direction.CENTER) {
			// from far side we cross over between switch and scale and place block on scale
			autoCmdList.driveForward(50);
			autoCmdList.turnRight(45 * directionBias);
			autoCmdList.driveForward(50); // diagonally across field
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.raiseElevator();
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();
		}
		else {
			// We are in the center start position
			 // allows left->right and right->left to share code
			directionBias = (position == Direction.LEFT) ? 1 : -1;

			autoCmdList.driveForward(10); // enough to turn
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.driveForward(40); // diagonally from start to far side of near switch
			autoCmdList.turnRight(45);
			autoCmdList.raiseElevator(0.5);
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();
		}
	}
}
