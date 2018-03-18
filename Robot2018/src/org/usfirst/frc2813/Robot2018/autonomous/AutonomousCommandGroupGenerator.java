// AutonomousCommand - a class to run autonomous mode

package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;


/**
 * Generate a series of sequential commands to operate autonomously. Takes into
 * account game data. Used exclusively by AutonomousCommandGroup
 */
public class AutonomousCommandGroupGenerator {
	AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;
	public enum Target { SWITCH, SCALE; }

	private static final Length scaleHeight = LengthUOM.Inches.create(60);
	private static final Length switchHeight = LengthUOM.Inches.create(24);

	private int directionBias;  // used to share code between left/right

	/**
	 * Code to be run during the Autonomous 15 second period.
	 * This code uses the gameData from the driver station and a
	 * sendable chooser on the Smart Dashboard to decide which
	 * sequence to run. Called by AutonomousCommandGroup
	 */
	public AutonomousCommandGroupGenerator() {
		// Read our location on the field
		Direction position = Robot.positionSelector.getSelected();

		 // allows left->right and right->left to share code
		directionBias = (position == Direction.LEFT) ? 1 : -1;

		if (RobotMap.gameData.getScale() == Direction.OFF) {
			// there is no game data. Cross the auto line
			Logger.info("Autonomous: no game data");
			autoCmdList.driveForward(LengthUOM.Feet.create(5));
			return;
		}
		
		// These return immediately and can happen while we drive
		Logger.info("Autonomous: set default elevator/arm position");
		autoCmdList.elevatorMoveToPosition(switchHeight); // min needed and max safe during drive
		autoCmdList.raiseArm();

		if (position == RobotMap.gameData.getScale()) {
			Logger.info("Autonomous: robot and scale on same side");
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(LengthUOM.Feet.create(24));
			autoCmdList.elevatorMoveToPosition(scaleHeight);
			autoCmdList.turnRight(90 * directionBias);
			deliverCubeRoutine(Target.SCALE);
		}
		else if (position != Direction.CENTER) {
			// from far side we cross over between switch and scale and place block on scale
			Logger.info("Autonomous: robot and scale on opposite side");
			autoCmdList.driveForward(LengthUOM.Feet.create(14));
			autoCmdList.turnRight(90 * directionBias);
			autoCmdList.driveForward(LengthUOM.Feet.create(15));
			autoCmdList.turnLeft(90 * directionBias);
			autoCmdList.driveForward(LengthUOM.Feet.create(8));
			autoCmdList.elevatorMoveToPosition(scaleHeight);
			autoCmdList.turnLeft(90 * directionBias);
			deliverCubeRoutine(Target.SCALE);
		}
		else {
			// We are in the center start position
			 // allows left->right and right->left to share code
			directionBias = (position == Direction.LEFT) ? 1 : -1;

			Logger.info("Autonomous: robot in center position");
			autoCmdList.driveForward(LengthUOM.Inches.create(8)); // enough to turn
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.driveForward(LengthUOM.Feet.create(6)); // diagonally from start to far side of near switch
			autoCmdList.turnRight(45 * directionBias);
			autoCmdList.elevatorMoveToPosition(switchHeight); 
			deliverCubeRoutine(Target.SWITCH);
		}
	}
	
	private void deliverCubeRoutine(Target target) {
		if (target == Target.SCALE) {
			autoCmdList.waitForElevator();
		}
		autoCmdList.driveForward(LengthUOM.Feet.create(2));
		autoCmdList.dropCube();
		autoCmdList.driveBackward(LengthUOM.Feet.create(2));
		if (target == Target.SCALE) {
			autoCmdList.elevatorMoveToPosition(switchHeight);
		}
	}
}
