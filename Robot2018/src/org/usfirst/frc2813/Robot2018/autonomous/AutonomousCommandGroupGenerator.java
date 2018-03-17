// AutonomousCommand - a class to run autonomous mode

package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


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
	private static final SendableChooser<Direction> positionSelector = new SendableChooser<>();
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
		// Read our location on the field
		Direction position = positionSelector.getSelected();

		 // allows left->right and right->left to share code
		directionBias = (position == Direction.LEFT) ? 1 : -1;

		if (RobotMap.gameData.getScale() == Direction.OFF) {
			// there is no game data. Cross the auto line
			autoCmdList.driveForward(LengthUOM.Feet.create(5));
			return;
		}
		
		// These return immediately and can happen while we drive
		autoCmdList.elevatorMoveToPosition(switchHeight); // min needed and max safe during drive
		autoCmdList.raiseArm();

		if (position == RobotMap.gameData.getScale()) {
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(LengthUOM.Feet.create(24));
			autoCmdList.elevatorMoveToPosition(scaleHeight);
			autoCmdList.turnRight(90 * directionBias);
			deliverCubeRoutine(Target.SCALE);
		}
		else if (position != Direction.CENTER) {
			// from far side we cross over between switch and scale and place block on scale
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

			autoCmdList.driveForward(LengthUOM.Inches.create(6)); // enough to turn
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.driveForward(LengthUOM.Feet.create(6)); // diagonally from start to far side of near switch
			autoCmdList.turnRight(45);
			autoCmdList.elevatorMoveToPosition(switchHeight); 
			deliverCubeRoutine(Target.SWITCH);
		}
	}
	
	private void deliverCubeRoutine(Target target) {
		if (target == Target.SCALE) {
			// FIXME! wait for elevator to reach height here!
		}
		autoCmdList.driveForward(LengthUOM.Feet.create(2));
		autoCmdList.dropCube();
		autoCmdList.driveBackward(LengthUOM.Feet.create(2));
		if (target == Target.SWITCH) {
			autoCmdList.elevatorMoveToPosition(switchHeight);
		}
	}
}
