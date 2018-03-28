package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

/**
 * Generate a series of sequential commands to operate autonomously.
 *
 * All command generation is done via methods in AutonomousCommandGroup.
 *
 * Takes into account game data (switch and scale ownership) and robot starting
 * position.
 *
 * NOTE: If the robot is on the left or right, we assume we are on the left. The
 * variables 'left' and 'right' as well as 'clockwise' and 'counterclockwise'
 * will be reversed if we are on the right. When the robot is in the center
 * starting position, we similarly assume the switch is ours to the left.
 *
 * WARNING! A full reset is required to re-generate this code. Changing game
 * data or robot position will NOT change autonomous, and commands consumed will
 * NOT be regenerated.
 */
public class AutonomousCommandGroupGenerator {
	/**
	 * Some note about speed.
	 * 
	 * These speeds are scaled 0..1 The robot is capable of a range of throttle
	 * settings. This is managed elsewhere. Here we use 0..1.
	 * 
	 * Transitional speed vs max speed: The autonomous drive routine that call has a
	 * fixed min (slowest throttle setting which affects the robot) and a
	 * programmable max. When we pass speed into these routines it. is the speed to
	 * transition out with. That means that the robot speed will ramp for the
	 * current full speed to the speed you set. Please note the difference between
	 * setting max speed - which we do when we put the elevator up - and setting
	 * transition speed - which we do entering a turn or when we stop.
	 */
	/** Full speed ahead! Only transition with this if tangents line up */
	private static final double SPEED_FULL = 1.0;

	/** This is used our speed when we put the elevator up */
	private static final double SPEED_ELEVATOR_UP = 0.35;

	/** Speed zero for stopping after a move. */
	private static final double SPEED_STOP = 0.0;

	/** Speed zero for stopping after a move. */
	private static final double SPEED_TURN = 0.2;

	/** Elevator heights for placing cubes on the switch. */
	private static final Length ELEVATOR_HEIGHT_SWITCH = LengthUOM.Inches.create(3);

	/** Elevator height for placing cubes on the scale based on robot direction. */
	private static final Length ELEVATOR_HEIGHT_SCALE_FORWARD = LengthUOM.Inches.create(76);
	private static final Length ELEVATOR_HEIGHT_SCALE_BACKWARD = LengthUOM.Inches.create(76);

	/** Arm Position for Level extension. Use to grab cube. */
	final Length ARM_POSITION_GRAB_CUBE = ArmConfiguration.ArmDegrees.create(133);

	/** Arm Position for shooting over head */
	static final Length ARM_POSITION_BACKWARD_SHOOT = ArmConfiguration.ArmDegrees.create(15);
	static final Length ARM_POSITION_FORWARD_SHOOT = ArmConfiguration.ArmDegrees.create(70);

	/** This is the commandGroup we are populating with our autonomous routine */
	private AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;

	/**
	 * This is the bias for invertible scripts that can run on left or right sides.
	 */
	private static Direction SCRIPT_BIAS = Direction.LEFT;

	/**
	 * To re-use scripts, we write them as if we were on the left, going left, etc.
	 * and then we invert the directions as necessary.
	 *
	 * Inversion is necessary when we start on the right side, because our script is
	 * written for left side. Inversion is necessary when we start in the center and
	 * the switch plate is on the right side, because our 'center' script is written
	 * for the left side.
	 *
	 * This is the function that handles the inversion according to the following
	 * logic:
	 * <ul>
	 * <li>ROBOT NEAR_SWITCH GOAL_DIRECTION RESULT
	 * <li>----------- ----------- ----------- ------------
	 *
	 * <li>LEFT any any unchanged
	 *
	 * <li>RIGHT any any reversed
	 * 
	 * <li>NEUTRAL any LEFT unchanged
	 * <li>NEUTRAL any RIGHT reversed
	 * </ul>
	 * 
	 * @see SCRIPT_BIAS
	 * @param startingPosition
	 *            The starting placement of the robot, LEFT/CENTER/RIGHT
	 * @param nearSwitchPosition
	 *            The location of our platform on the near switch
	 * @param referenceDirection
	 *            The reference direction form our left hand oriented script
	 * @return The adjusted direction
	 */
	private static Direction getBiasedDirection(Direction startingPosition, Direction nearSwitchPosition,
			Direction referenceDirection) {
		if (startingPosition.isNeutral()) {
			return nearSwitchPosition.equals(SCRIPT_BIAS) ? referenceDirection : referenceDirection.getInverse();
		} else if (startingPosition.equals(SCRIPT_BIAS)) {
			return referenceDirection;
		} else {
			return referenceDirection.getInverse();
		}
	}

	/**
	 * This is a built-in unit test function for verifying that we get the correct
	 * output from our expectations
	 * 
	 * @param startingPosition
	 *            The starting placement of the robot, LEFT/CENTER/RIGHT
	 * @param nearSwitchPosition
	 *            The location of our platform on the near switch
	 * @param referenceDirection
	 *            The reference direction form our left hand oriented script
	 * @param expected
	 *            The expected output
	 * @see getBiasedDirection
	 * @see SCRIPT_BIAS
	 */
	private static void verify(Direction startingPosition, Direction goalPosition, Direction original,
			Direction expected) {
		if (getBiasedDirection(startingPosition, goalPosition, original) != expected) {
			throw new IllegalArgumentException(
					"In " + startingPosition + " position, biased " + original + " should have been " + expected + ".");
		}
	}

	/**
	 * Built-in unit test to verify that our inversion logic matches our
	 * expectations.
	 */
	private static void verifyDirections() {
		/** If we start at the left, there's no change... */
		verify(Direction.LEFT, Direction.LEFT, Direction.LEFT, Direction.LEFT);
		verify(Direction.LEFT, Direction.LEFT, Direction.RIGHT, Direction.RIGHT);
		verify(Direction.LEFT, Direction.RIGHT, Direction.LEFT, Direction.LEFT);
		verify(Direction.LEFT, Direction.RIGHT, Direction.RIGHT, Direction.RIGHT);

		/** If we start on the right, we always want the opposite */
		verify(Direction.RIGHT, Direction.LEFT, Direction.LEFT, Direction.RIGHT); /** starting on right, invert logic */
		verify(Direction.RIGHT, Direction.LEFT, Direction.RIGHT, Direction.LEFT); /** starting on right, invert logic */
		verify(Direction.RIGHT, Direction.RIGHT, Direction.LEFT,
				Direction.RIGHT); /** starting on right, invert logic */
		verify(Direction.RIGHT, Direction.RIGHT, Direction.RIGHT,
				Direction.LEFT); /** starting on right, invert logic */

		/**
		 * If we start at the center, we track the near switch and will reverse
		 * direction if the target is on the right
		 */
		verify(Direction.CENTER, Direction.LEFT, Direction.LEFT, Direction.LEFT);
		verify(Direction.CENTER, Direction.LEFT, Direction.RIGHT, Direction.RIGHT);
		verify(Direction.CENTER, Direction.RIGHT, Direction.LEFT, Direction.RIGHT); /** goal on right, invert logic */
		verify(Direction.CENTER, Direction.RIGHT, Direction.RIGHT, Direction.LEFT); /** goal on right, invert logic */
	}

	/** Helper to create a length in inches. */
	private static Length inches(double inches) {
		return LengthUOM.Inches.create(inches);
	}

	/** Helper to create a length in feet. */
	private static Length feet(double feet) {
		return LengthUOM.Feet.create(feet);
	}

	/**
	 * Helpers for using S curves to offset our position. If you travel the same
	 * amount around two circles of opposite rotation, you end up pointed the same
	 * way, but offset diagonally
	 */
	private static double sCurveSideShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (1 - Math.cos(Math.toRadians(degrees)));
	}

	private static double sCurveForwardShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (Math.sin(Math.toRadians(degrees)));
	}

	/**
	 * Prepare to shoot a cube at the switch. This is used as a starting position as
	 * well.
	 */
	private void prepareForSwitchAsync(Direction direction) {
		if (direction == direction.FORWARD) {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_FORWARD_SHOOT);
		} else {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_BACKWARD_SHOOT);
		}
		autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SWITCH);
	}

	/**
	 * Prepare to shoot a cube at the scale. This starts the elevator moving up all
	 * the way! Be careful!!! We will not be very stable.
	 * 
	 * @param direction
	 *            - which direction is the robot facing?
	 */
	protected void prepareForScaleAsync(Direction direction) {
		if (direction == direction.FORWARD) {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_FORWARD_SHOOT);
			autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SCALE_FORWARD);
		} else {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_BACKWARD_SHOOT);
			autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SCALE_BACKWARD);
		}
		autoCmdList.drive.setDriveSpeed(SPEED_ELEVATOR_UP);
	}

	/**
	 * When we're done delivering a cube, we call this routine to switch to cube
	 * grabbing mode.
	 */
	protected void prepareForCubeGrabbingSync() {
		autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SWITCH);
		autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_GRAB_CUBE);
		autoCmdList.elevator.addWaitForTargetPositionSync();
		autoCmdList.arm.addWaitForTargetPositionSync();
		autoCmdList.drive.setDriveSpeed(SPEED_FULL);
	}

	/**
	 * Build a command sequence to be run during the Autonomous 15 second period.
	 *
	 * This code uses the gameData from the driver station and a sendable chooser on
	 * the Smart Dashboard to decide which sequence to run.
	 */
	@SuppressWarnings("unused")
	public AutonomousCommandGroupGenerator() {
		/** Determine our game configuration */
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = Robot.gameData.getNearSwitchPosition();
		Direction farSwitchPosition = Robot.gameData.getFarSwitchPosition();
		Direction scalePosition = Robot.gameData.getScalePosition();

		/** Read user choices about our strategy */
		boolean useCurves = Robot.autonomousSelectorCurves.getSelected() == Direction.ON;

		/**
		 * Determine our biased directions. Assume if you're on a side, it's left and if
		 * you're in the center the switch will be to the left. This reduces the
		 * complexity of our choices.
		 *
		 * NOTE: This only allows us to interact with the switch when we start in the
		 * middle and only with the scale otherwise. If we want to do both, we will have
		 * to split off two directional biases. Us relative to swtich and us relative to
		 * scale.
		 * 
		 * @see getBiasedDirection
		 */
		Direction left = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(),
				Direction.LEFT);
		Direction right = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(),
				Direction.RIGHT);

		/**
		 * We similarly have a biased rotation.
		 */
		Direction clockwise, counterclockwise;
		if (left == Direction.LEFT) {
			clockwise = Direction.CLOCKWISE;
			counterclockwise = Direction.COUNTERCLOCKWISE;
		} else {
			clockwise = Direction.COUNTERCLOCKWISE;
			counterclockwise = Direction.CLOCKWISE;
		}

		verifyDirections(); /** Sanity check our biasing logic */

		/**
		 * Define some handy measurements. Define as double in implied inches. Doing
		 * math with groups of Length is TOO PAINFUL!
		 */

		/**
		 * cubes are placed along switches on the scale side and in pyramid on the other
		 * switch side
		 */
		double cubeSize = 14;

		/** robot dimensions with and without bumpers */
		double bumperThickness = 3.25;
		double robotWheelbaseWidth = 31;
		double robotWheelbaseLength = 20;
		double robotBumperWidth = robotWheelbaseWidth + bumperThickness * 2;
		double robotBumperLength = robotWheelbaseLength + bumperThickness * 2;
		double finalDistanceToTarget = 20; /** how close to get before we shoot cube */

		/** field dimensions from back wall */
		double backWallToSwitch = 140;
		double backWallToCubePyramid = backWallToSwitch - 3 * cubeSize; /** pyramid of cubes on near side of scale */
		double backWallToFarSideOfSwitch = 196;
		double backWallToScaleAlley = backWallToFarSideOfSwitch + cubeSize; /** row of cubes on far side of scale */
		double backWallToScalePlatform = 261.47;
		double backWallToScaleTarget = 299.65;

		/** relative depth dimensions */
		double switchDepth = backWallToFarSideOfSwitch - backWallToSwitch;
		double switchToScalePlatform = backWallToScalePlatform - backWallToFarSideOfSwitch;
		double scaleAlleyWidth = switchToScalePlatform - cubeSize;
		double scalePlatformToTarget = backWallToScaleTarget - backWallToScalePlatform;
		double scaleAlleyToTarget = 71.57;

		/** dimensions side to side */
		double backWallWidth = 264;
		double fieldWidth = 293.69;
		double switchWidth = 152.88;
		double scalePlatformWidth = 132.88;
		double scaleFullWidth = 150.55;
		double scaleTargetWidth = 56;

		/** relative width dimensions */
		double sideWallToFirstRobotStartPosition = (fieldWidth - backWallWidth) / 2;
		double sideWallToFirstRobotEndPosition = sideWallToFirstRobotStartPosition + robotBumperWidth;
		double sideWallToFirstRobotCenter = sideWallToFirstRobotStartPosition + robotBumperWidth / 2;
		double sideWallToScaleTarget = (fieldWidth - scaleTargetWidth) / 2;
		double sideWallToScalePlatform = (fieldWidth - scalePlatformWidth) / 2;
		double sideWallToSwitch = (fieldWidth - switchWidth) / 2;

		/**
		 * Make a note that we are generating the sequence now, and capture the
		 * settings. This is very important because only a robot code reset will
		 * re-initialize the auto sequence and merely disabling and re-enabling the
		 * robot will re-run the sequence without re-initializing.
		 *
		 * WARNING: This means that if you don't reset the robot, you will run with old
		 * game data!
		 *
		 * This log message is a warning so you can catch that mistake instead of
		 * pulling your hair out debugging a phantom bug.
		 */
		Logger.printLabelled(LogType.INFO, this + ": Generating Auto Sequence", "Robot", robotStartingPosition,
				"NearSwitch", nearSwitchPosition, "Scale", scalePosition, "FarSwitch", farSwitchPosition,
				"AdjustedLeft", left, "AdjustedRight", right);

		/** Initialize the robot */
		autoCmdList.resetAndCalibrateSync();

		/**
		 * Keep track of whether we expect to be holding a cube at each step, so we can
		 * choose our speed wisely.
		 */
		autoCmdList.cube.setHaveCube(true);

		/**
		 * If we have failed to read the field setup. The only safe option is to drive
		 * forward and stop. There is a two deep pile of cubes near the switch, stop a
		 * foot short of them.
		 */
		if (!Robot.gameData.isGameDataValid()) {
			Logger.error(this + ": No game data.");
			autoCmdList.drive.addDriveSync(Direction.FORWARD,
					inches(backWallToSwitch - robotBumperLength - 2 * cubeSize - 12), SPEED_STOP);
			return;
		}

		/**
		 * This initial state may not be exactly what we want, but it's safe. We can
		 * change it later.
		 */
		prepareForSwitchAsync(Direction.FORWARD);

		/**
		 * Here begins the autonomous decision tree in which we consider our starting
		 * position and the configurations of switch and scale. We make these decisions
		 * in such a way as to cluster symmetric paths and use variables to handle them
		 * together. We bias to the left, meaning if we start on the left or right, we
		 * write all paths as if we are on the left. If we are in the center, we write
		 * paths as if we are are moving to the left.
		 */
		if (robotStartingPosition.equals(scalePosition)) {
			/**
			 * The robot and the scale are on the same side. Drive forward and approach the
			 * scale from the side.
			 */
			Logger.info(this + ": Robot and Scale are both at the " + robotStartingPosition + " position.");
			if (useCurves) {
				/**
				 * We are backwards on the left side. Working backwards, we will approach the
				 * target at a 45 degree angle to avoid the scale platform. The distance we need
				 * to travel from left to right will be the sine of 45 degrees times the radius
				 * of some circle. From this we find the radius. Now subtract the left->right
				 * offset from the forward distance. Travel that distance straight. Then raise
				 * the elevator. Then follow our curve to the target.
				 */
				
				/** This is the total side offset between us and the scale target */
				double offset = sideWallToScaleTarget - sideWallToFirstRobotCenter;
				double radius = offset - (robotBumperLength/2 + finalDistanceToTarget) / Math.sqrt(2);
				
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(backWallToScaleTarget - offset), SPEED_FULL);
				prepareForScaleAsync(Direction.BACKWARD);
				autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, 45.0, inches(radius), counterclockwise,
						SPEED_STOP);
			} else {
				double distanceToTarget = backWallToScaleTarget - robotBumperLength - finalDistanceToTarget;

				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToTarget), SPEED_TURN);
				autoCmdList.drive.addQuickTurnSync(left, 90); /** right but we're backwards */
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(43), SPEED_TURN);
				prepareForScaleAsync(Direction.BACKWARD);
				autoCmdList.drive.addQuickTurnSync(right, 90); /** left but we're backwards */
			}
		} else if (robotStartingPosition.equals(Direction.CENTER)) {
			/**
			 * If the robot is in the center, we're going to drop a cube into the switch on
			 * the correct side.
			 */
			Logger.info(this + ": Robot is in the " + robotStartingPosition + " position, with the near switch at the "
					+ nearSwitchPosition + " position.");
			if (useCurves) {
				/**
				 * An S curve. counterclockwise 1/4 turn followed by clockwise 1/4 turn leaves
				 * us in the same orientation 2r up and 2r over The distance to the scale less
				 * our length is how far we need to move forward. This S curve does that in 2
				 * arcs, so set radius to half that. TODO: define field dimensions and our
				 * dimensions in variables.
				 */
				double distanceToTarget = backWallToSwitch - robotBumperLength - finalDistanceToTarget;
				double sideShiftToTarget = (switchWidth - robotBumperWidth) / 2
						- 6; /** left bumper 6 inches right of left edge of switch */
				double radius = 63.0; /** found by trial and error */
				double degrees = 54.0;

				autoCmdList.drive.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), counterclockwise,
						SPEED_FULL);
				autoCmdList.drive.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), clockwise,
						SPEED_STOP);
			} else {
				double distanceToTarget = backWallToSwitch - robotBumperLength - finalDistanceToTarget;
				double sideShiftToTarget = (switchWidth - robotBumperWidth) / 2
						- 6; /** left bumper 6 inches right of left edge of switch */
				double turnClearance = 8.0;

				autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(turnClearance),
						SPEED_TURN); /** enough to turn */
				autoCmdList.drive.addQuickTurnSync(left, 90);
				autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(sideShiftToTarget), SPEED_TURN);
				autoCmdList.drive.addQuickTurnSync(right, 90);
				autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(distanceToTarget - turnClearance), SPEED_STOP);
			}
		} else {
			/**
			 * Robot and scale are on opposite side. Drive across the field between scale
			 * and switch. Note that we must avoid driving over the scale platform. We do
			 * this by circling back to face the scale target at 45 degrees. The same as
			 * when we are on the same side.
			 */
			Logger.info(this + ": Robot and Scale on opposite sides.  Robot is at the " + robotStartingPosition
					+ " position and the Scale is at the " + scalePosition + " position.");

			/** distance from center of robot to center of scale alley */
			double distanceToFirstTurn = backWallToScaleAlley + scaleAlleyWidth/2 - robotBumperLength/2;

			/** distance from center of the robot from the center of the field */
			double distanceToCenter = fieldWidth/2 - sideWallToFirstRobotCenter;

			/** distance down scale alley from the center of the field to the target */
			double distanceFromCenter = scaleFullWidth/2 - scaleTargetWidth/2;

			/** project our desired distance to the target onto distance down scale alley */
			double projectedDistToTarget = (finalDistanceToTarget + (robotBumperLength/2)) / Math.sqrt(2) + scaleTargetWidth/2;

			/** make a right turn down scale alley */
			double firstAngle = 90.0;

			/** pass the target and double back. right turn + 45 degrees to avoid hitting scale platform */
			double secondAngle = 135.0;

			if (useCurves) {
				double firstRadius = scaleAlleyWidth;
				
				double secondRadius = (scaleAlleyWidth/2 - projectedDistToTarget) * Math.sqrt(2);
				
				double distanceFromCenterToSecondTurn = distanceFromCenter + scaleTargetWidth/2 - projectedDistToTarget;

				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToFirstTurn - firstRadius), SPEED_FULL);
				autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, firstAngle, inches(firstRadius), counterclockwise, SPEED_FULL);
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToCenter - firstRadius), SPEED_FULL);
				prepareForScaleAsync(Direction.BACKWARD); // half way down scale alley
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceFromCenterToSecondTurn), SPEED_FULL);
				autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, secondAngle, inches(secondRadius), clockwise, SPEED_STOP);
			} else {
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToFirstTurn), SPEED_TURN);
				autoCmdList.drive.addQuickTurnSync(left, 90); /** right but we're backwards */
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToCenter), SPEED_FULL);
				prepareForScaleAsync(Direction.BACKWARD); // half way down scale alley
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceFromCenter + projectedDistToTarget), SPEED_TURN);
				autoCmdList.drive.addQuickTurnSync(right, 135); /** left but we're backwards */

				double finalDistance = projectedDistToTarget * Math.sqrt(2) - finalDistanceToTarget - robotBumperLength/2;
				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(finalDistance), SPEED_STOP);
			}
		}
		/**
		 * NB: DeliverCubeCommandSequence will always wait for Elevator to reach target
		 * height, to avoid crashing
		 */
		autoCmdList.cube.addDeliverSequenceSync();

		/** time to switch to cube grabbing mode */
		prepareForCubeGrabbingSync();

		/** Remember we let go of our cube, we can really fly now... */
		autoCmdList.cube.setHaveCube(false);
	}

	/** Give ourselves a name for debugging */
	public String toString() {
		return "AutonymousCodeGenerator";
	}
}
