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
			autoCmdList.driveForward(LengthUOM.Feet.create(5).multiply(distanceScale), FULL_STOP);
			return;
		}

		// These return immediately and can happen while we drive
		Logger.info("Autonomous: set default elevator/arm position");
		autoCmdList.elevatorMoveToPosition(switchHeight); // min needed and max safe during drive
		autoCmdList.raiseArm();

		if (robotStartingPosition.equals(scalePosition)) {
			Logger.info("Autonomous: robot and scale on same side");
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(LengthUOM.Feet.create(24).multiply(distanceScale), FULL_STOP);
			autoCmdList.elevatorMoveToPosition(scaleHeight);
			autoCmdList.turn(right, 90);
			deliverCubeRoutine(Target.SCALE);
		}
		else if (!robotStartingPosition.equals(Direction.CENTER)) {
			// from far side we cross over between switch and scale and place block on scale
			Logger.info("Autonomous: robot and scale on opposite side");
			autoCmdList.driveForward(LengthUOM.Feet.create(14).multiply(distanceScale), TRANSITION_SPEED);
			autoCmdList.turn(right, 90);
			autoCmdList.driveForward(LengthUOM.Feet.create(15).multiply(distanceScale), TRANSITION_SPEED);
			autoCmdList.turn(left, 90);
			autoCmdList.driveForward(LengthUOM.Feet.create(8).multiply(distanceScale), FULL_STOP);
			autoCmdList.elevatorMoveToPosition(scaleHeight);
			autoCmdList.turn(left, 90);
			deliverCubeRoutine(Target.SCALE);
		}
		else {
			Logger.info("Autonomous: robot in center position");
			autoCmdList.driveForward(LengthUOM.Inches.create(8).multiply(distanceScale), TRANSITION_SPEED); // enough to turn
			autoCmdList.turn(left, 45);
			autoCmdList.driveForward(LengthUOM.Feet.create(6).multiply(distanceScale), TRANSITION_SPEED); // diagonally from start to far side of near switch
			autoCmdList.turn(right, 45);
			autoCmdList.elevatorMoveToPosition(switchHeight); 
			deliverCubeRoutine(Target.SWITCH);
		}
	}
	
	private void deliverCubeRoutine(Target target) {
		if (target == Target.SCALE) {
			autoCmdList.waitForElevator();
		}
		autoCmdList.driveForward(LengthUOM.Feet.create(2).multiply(distanceScale), FULL_STOP);
		autoCmdList.dropCube();
		autoCmdList.driveBackward(LengthUOM.Feet.create(2).multiply(distanceScale), TRANSITION_SPEED);
		if (target == Target.SCALE) {
			autoCmdList.elevatorMoveToPosition(switchHeight);
		}
	}
}
