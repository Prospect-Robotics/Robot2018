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

	/**
	 *  FIXME! ideally this should be scaled by the projection of the upcoming
	 *  angle if we are about to turn. Do this by scaling by the cosine of the
	 *  angle and clamping at +-90 degrees
	 */
	private static final double TRANSITION_SPEED = 0.2;
	private static final double FULL_STOP = 0.0;

	/*
	Ability to scale the distances involved for "mini testing" when we don't have sufficient surface area for testing.
	*/
	private static double distanceScale = 0.2;
	
	private static Direction SCRIPT_BIAS = Direction.LEFT;
	/**
	 * If we are in neutral or reverse direction (LEFT), return the direction unmodified.
	 * If we are in the right position (i.e. position isn't neutral and doesn't match the input, reverse it)
	 */
	private static Direction getBiasedDirection(Direction startingPosition, Direction goalPosition, Direction referenceDirection) {
		
		if(startingPosition.isNeutral()) {
			return goalPosition.equals(SCRIPT_BIAS) ? referenceDirection : referenceDirection.getInverse();
		} else if(!startingPosition.equals(SCRIPT_BIAS)) {
			return referenceDirection.getInverse();
		} else {
			return referenceDirection;
		}
	}

	/**
	 * Validate assumption about direction biasing
	 */
	private static void verify(Direction startingPosition, Direction goalPosition, Direction original, Direction expected) {
		if(getBiasedDirection(startingPosition, goalPosition, original) != expected) {
			throw new IllegalArgumentException("In " + startingPosition + " position, biased " + original + " should have been " + expected + ".");
		}
	}
	
	/**
	 * Validate direction assumptions
	 */
	private static void verifyDirections() {
		// If we start at the left, there's no change...
		verify(Direction.LEFT,  Direction.LEFT, Direction.LEFT, Direction.LEFT);
		verify(Direction.LEFT,  Direction.LEFT, Direction.RIGHT, Direction.RIGHT);
		verify(Direction.LEFT,  Direction.RIGHT, Direction.LEFT, Direction.LEFT);
		verify(Direction.LEFT,  Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);

		// If we start at the center, we will reverse direction if the target is on the right
		verify(Direction.CENTER, Direction.LEFT, Direction.LEFT, Direction.LEFT);
		verify(Direction.CENTER, Direction.LEFT, Direction.RIGHT, Direction.RIGHT);
		verify(Direction.CENTER, Direction.RIGHT, Direction.LEFT, Direction.RIGHT);
		verify(Direction.CENTER, Direction.RIGHT, Direction.RIGHT, Direction.LEFT);
		
		// If we start on the right, we always want the opposite
		verify(Direction.RIGHT,  Direction.LEFT, Direction.LEFT, Direction.RIGHT);
		verify(Direction.RIGHT,  Direction.LEFT, Direction.RIGHT, Direction.LEFT);
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.LEFT, Direction.RIGHT);
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.RIGHT, Direction.LEFT);
	}

	/*
	 * Helper to create a length in inches, scaled appropriately
	 */
	private Length inches(double inches) {
		return LengthUOM.Inches.create(inches).multiply(distanceScale);
	}
	
	/*
	 * Helper to create a length in feet, scaled appropriately
	 */
	private Length feet(double feet) {
		return LengthUOM.Feet.create(feet).multiply(distanceScale);
	}
	
	
	/**
	 * Code to be run during the Autonomous 15 second period.
	 * This code uses the gameData from the driver station and a
	 * sendable chooser on the Smart Dashboard to decide which
	 * sequence to run. Called by AutonomousCommandGroup
	 */
	public AutonomousCommandGroupGenerator() {
		// Read our location on the field
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = RobotMap.gameData.getNearSwitch();
		Direction scalePosition = RobotMap.gameData.getScale();

		/**
		 * The script is written from a left-hand position.  If we are on the right side, everything is reversed.
		 * If we are in the middle, then we use the switch direction.
		 */
		Direction left     = getBiasedDirection(robotStartingPosition, RobotMap.gameData.getNearSwitch(), Direction.LEFT);
		Direction right    = getBiasedDirection(robotStartingPosition, RobotMap.gameData.getNearSwitch(), Direction.RIGHT);

		// Sanity test our direction biases
		verifyDirections();
		Logger.info("AutonomousCommandGroupGenerator: Position=" + robotStartingPosition + " nearSwitch=" + nearSwitchPosition + " scale=" + scalePosition + " adjusted_left=" + left + " adjusted_right=" + right);

		if (RobotMap.gameData.getScale() == Direction.OFF) {
			// there is no game data. Cross the auto line
			Logger.info("Autonomous: no game data");
			autoCmdList.driveForward(feet(5), FULL_STOP);
			return;
		}

		// These return immediately and can happen while we drive
		Logger.info("Autonomous: set default elevator/arm position");
		startElevatorMovingToPlacementHeight(Target.SWITCH);
		autoCmdList.raiseArm();

		if (robotStartingPosition.equals(scalePosition)) {
			/*
    		 * If the robot and the scale are on the same side, 
    		 * drive forward and drop a cube into the scale from the end.
    		 * 
			 * NB: We write this script as if the robot and scale are both on the left,
			 * it will also be used inverted for when the robot and scale are both on the right.
			 */
			Logger.info("Autonomous: robot and scale on same side");
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(feet(24), FULL_STOP);
			startElevatorMovingToPlacementHeight(Target.SCALE);
			autoCmdList.quickTurn(right, 90);
			deliverCubeRoutine(Target.SCALE);
		}
		else if (!robotStartingPosition.equals(Direction.CENTER)) {
			/*
    		 * If the robot and the scale are on opposite sides,
    		 * drive forward past the switch, spin towards the
    		 * opposite side and drive down the alley
    		 * between the scale and switch to the opposite side
    		 * then turn towards the scale and then drop a cube 
    		 * into the scale from that side.
    		 * 
    		 * NB: Make sure the other team won't be coming the other direction down the alley! 
    		 * 
			 * NB: We write this script as if the robot and scale are both on the left,
			 * it will also be used inverted for when the robot and scale are both on the right.
			 */
			// from far side we cross over between switch and scale and place block on scale
			Logger.info("Autonomous: robot and scale on opposite side");
			autoCmdList.driveForward(feet(14), TRANSITION_SPEED);
			autoCmdList.quickTurn(right, 90);
			autoCmdList.driveForward(feet(15), TRANSITION_SPEED);
			autoCmdList.quickTurn(left, 90);
			autoCmdList.driveForward(feet(8), FULL_STOP);
			startElevatorMovingToPlacementHeight(Target.SCALE);
			autoCmdList.quickTurn(left, 90);
			deliverCubeRoutine(Target.SCALE);
		}
		else {
			/*
			 * If the robot is in the center, we're going to drop a cube into the switch
			 * on the correct side.  
    		 *
    		 * NB: Make sure there isn't another robot crossing your path before you pick this.
    		 * 
			 * NB: We write this script as if the our switch is active to the left of the  
			 * robot.  If this isn't the case, the script will be run inverted.
			 */
			Logger.info("Autonomous: robot in center position");
			autoCmdList.driveForward(inches(8), TRANSITION_SPEED); // enough to turn
			autoCmdList.quickTurn(left, 45);
			autoCmdList.driveForward(feet(6), TRANSITION_SPEED); // diagonally from start to far side of near switch
			autoCmdList.quickTurn(right, 45);
			deliverCubeRoutine(Target.SWITCH);
		}
	}

	/*
	 * Start the elevator moving in the background
	 */
	private void startElevatorMovingToPlacementHeight(Target target) {
		Length height = null;
		switch(target) {
		case SCALE:
			height = scaleHeight;
			break;
		case SWITCH:
			height = switchHeight;
			break;
		default:
			throw new IllegalArgumentException("Unsuported target: " + target);
		}
		autoCmdList.elevatorMoveToPosition(height);
	}
	
	private void deliverCubeRoutine(Target target) {
		autoCmdList.waitForElevator();
		autoCmdList.driveForward(feet(2), FULL_STOP);
		autoCmdList.dropCube();
		autoCmdList.driveBackward(feet(2), TRANSITION_SPEED);
		if (target == Target.SCALE) {
			autoCmdList.elevatorMoveToPosition(switchHeight);
		}
	}
}
