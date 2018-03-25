package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.PlacementTargetType;
import org.usfirst.frc2813.Robot2018.Robot;
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
 * Takes into account game data (switch and scale ownership) and robot starting position.
 * 
 * NOTE: If the robot is on the left or right, we assume we are on the left. The variables
 * 'left' and 'right' as well as 'clockwise' and 'counterclockwise' will be reversed if
 * we are on the right. When the robot is in the center starting position, we similarly
 * assume the switch is ours to the left.
 * 
 * WARNING! A full reset is required to re-generate this code. Changing game data or
 * robot position will NOT change autonomous, and commands consumed will NOT be regenerated.
 */
public class AutonomousCommandGroupGenerator {
	/** This is the commandGroup we are populating with our autonomous routine */
	private AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;

	/** This is the bias for invertible scripts that can run on left or right sides.  */
	private static Direction SCRIPT_BIAS = Direction.LEFT;
	/**
	 * To re-use scripts, we write them as if we were on the left, going left, etc. and then
	 * we invert the directions as necessary.
	 * 
	 * Inversion is necessary when we start on the right side, because our script is written for left side.
	 * Inversion is necessary when we start in the center and the switch plate is on the right side, because 
	 * our 'center' script is written for the left side.
	 * 
	 * This is the function that handles the inversion according to the following logic:
	 * 
	 * ROBOT       NEAR_SWITCH GOAL_DIRECTION    RESULT
	 * ----------- ----------- -----------       ------------
	 * 
	 * LEFT        any         any               unchanged
	 * 
	 * RIGHT       any         any               reversed

	 * NEUTRAL     any         LEFT              unchanged        
	 * NEUTRAL     any         RIGHT             reversed         
	 * 
	 * @see SCRIPT_BIAS
	 * @param startingPosition The starting placement of the robot, LEFT/CENTER/RIGHT
	 * @param nearSwitchPosition The location of our platform on the near switch
	 * @param referenceDirection The reference direction form our left hand oriented script
	 * @return The adjusted direction 
	 */
	private static Direction getBiasedDirection(Direction startingPosition, Direction nearSwitchPosition, Direction referenceDirection) {
		if(startingPosition.isNeutral()) {
			return nearSwitchPosition.equals(SCRIPT_BIAS) ? referenceDirection : referenceDirection.getInverse();
		} else if(startingPosition.equals(SCRIPT_BIAS)) {
			return referenceDirection;
		} else {
			return referenceDirection.getInverse();
		}
	}

	/**
	 * This is a built-in unit test function for verifying that we get the correct output from our expectations
	 * @param startingPosition The starting placement of the robot, LEFT/CENTER/RIGHT
	 * @param nearSwitchPosition The location of our platform on the near switch
	 * @param referenceDirection The reference direction form our left hand oriented script
	 * @param expected The expected output
	 * @see getBiasedDirection
	 * @see SCRIPT_BIAS
	 */
	private static void verify(Direction startingPosition, Direction goalPosition, Direction original, Direction expected) {
		if(getBiasedDirection(startingPosition, goalPosition, original) != expected) {
			throw new IllegalArgumentException("In " + startingPosition + " position, biased " + original + " should have been " + expected + ".");
		}
	}

	/** Built-in unit test to verify that our inversion logic matches our expectations. */
	private static void verifyDirections() {		
		/**  If we start at the left, there's no change... */
		verify(Direction.LEFT,   Direction.LEFT,  Direction.LEFT,   Direction.LEFT);
		verify(Direction.LEFT,   Direction.LEFT,  Direction.RIGHT,  Direction.RIGHT);
		verify(Direction.LEFT,   Direction.RIGHT, Direction.LEFT,   Direction.LEFT);
		verify(Direction.LEFT,   Direction.RIGHT, Direction.RIGHT,  Direction.RIGHT);
		
		/**  If we start on the right, we always want the opposite */
		verify(Direction.RIGHT,  Direction.LEFT,  Direction.LEFT,   Direction.RIGHT); /**  starting on right, invert logic */
		verify(Direction.RIGHT,  Direction.LEFT,  Direction.RIGHT,  Direction.LEFT);  /**  starting on right, invert logic */
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.LEFT,   Direction.RIGHT); /**  starting on right, invert logic */
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.RIGHT,  Direction.LEFT);  /**  starting on right, invert logic */

		/**  If we start at the center, we track the near switch and will reverse direction if the target is on the right */
		verify(Direction.CENTER, Direction.LEFT,  Direction.LEFT,   Direction.LEFT);
		verify(Direction.CENTER, Direction.LEFT,  Direction.RIGHT,  Direction.RIGHT);
		verify(Direction.CENTER, Direction.RIGHT, Direction.LEFT,   Direction.RIGHT); /**  goal on right, invert logic */
		verify(Direction.CENTER, Direction.RIGHT, Direction.RIGHT,  Direction.LEFT);  /**  goal on right, invert logic */
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
	 * Helpers for using S curves to offset our position. If you travel the same amount around two circles
	 * of opposite rotation, you end up pointed the same way, but offset diagonally
	 */
	private static double sCurveSideShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (1 - Math.cos(Math.toRadians(degrees)));
	}
	private static double sCurveForwardShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (Math.sin(Math.toRadians(degrees)));
	}
	/**
	 * Build a command sequence to be run during the Autonomous 15 second period.
	 * 
	 * This code uses the gameData from the driver station and a sendable chooser 
	 * on the Smart Dashboard to decide which sequence to run.
	 */
	@SuppressWarnings("unused")
	public AutonomousCommandGroupGenerator() {
		/**  Determine our game configuration */
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = Robot.gameData.getNearSwitchPosition();
		Direction farSwitchPosition = Robot.gameData.getFarSwitchPosition();
		Direction scalePosition = Robot.gameData.getScalePosition();
		
		/**  Read user choices about our strategy */
		boolean useCurves = Robot.autonomousSelectorCurves.getSelected() == Direction.ON;

		/**
		 * Determine our biased directions. Assume if you're on a side, it's left and if you're in the center the
		 * switch will be to the left. This reduces the complexity of our choices.
		 * 
		 * NOTE: This only allows us to interact with the switch when we start in the middle and only with the
		 * scale otherwise. If we want to do both, we will have to split off two directional biases. Us relative
		 * to swtich and us relative to scale.
		 * @see getBiasedDirection 
		 */
		Direction left  = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(), Direction.LEFT);
		Direction right = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(), Direction.RIGHT);

		/**
		 * We similarly have a biased rotation.
		 */
		Direction clockwise, counterclockwise;
		if (left == Direction.LEFT) {
			clockwise = Direction.CLOCKWISE;
			counterclockwise = Direction.COUNTERCLOCKWISE;
		}
		else {
			clockwise = Direction.COUNTERCLOCKWISE;
			counterclockwise = Direction.CLOCKWISE;			
		}

		verifyDirections();  /** Sanity check our biasing logic  */

		/** Define some handy measurements. Define as double in implied inches. Doing math with groups of Length is TOO PAINFUL! */

		/**  cubes are placed along switches on the scale side and in pyramid on the other switch side */
		double cubeSize = 14;

		/**  robot dimensions with and without bumpers */
		double bumperThickness = 3.25;
		double robotWheelbaseWidth = 31;
		double robotWheelbaseLength = 20;
		double robotBumperWidth = robotWheelbaseWidth + bumperThickness * 2;
		double robotBumperLength = robotWheelbaseLength + bumperThickness * 2;
		double finalDistanceToTarget = 12; /**  how close to get before we shoot cube */

		/**  field dimensions from back wall */
		double backWallToSwitch = 140;
		double backWallToCubePyramid = backWallToSwitch - 3 * cubeSize; /**  pyramid of cubes on near side of scale */
		double backWallToFarSideOfSwitch = 196;
		double backWallToScaleAlley = backWallToFarSideOfSwitch + cubeSize; /**  row of cubes on far side of scale */
		double backWallToScalePlatform = 261.47;
		double backWallToScaleTarget = 299.65;

		/**  relative depth dimensions */
		double switchDepth = backWallToFarSideOfSwitch - backWallToSwitch;
		double switchToScalePlatform = backWallToScalePlatform - backWallToFarSideOfSwitch;
		double scaleAlleyWidth = switchToScalePlatform - cubeSize;
		double scalePlatformToTarget = backWallToScaleTarget - backWallToScalePlatform;
		double scaleAlleyToTarget = 71.57;

		/**  dimensions side to side */
		double backWallWidth = 264;
		double fieldWidth = 293.69;
		double switchWidth = 152.88;
		double scalePlatformWidth = 132.88;
		double scaleFullWidth = 150.55;
		double scaleTargetWidth = 56;

		/**  relative width dimensions */
		double sideWallToFirstRobotStartPosition = (fieldWidth - backWallWidth) / 2;
		double sideWallToFirstRobotEndPosition = sideWallToFirstRobotStartPosition + robotBumperWidth;
		double sideWallToScaleTarget = (fieldWidth - scaleTargetWidth) / 2;
		double sideWallToScalePlatform = (fieldWidth - scalePlatformWidth) / 2;
		double sideWallToSwitch = (fieldWidth - switchWidth) / 2;

		/**
		 * Make a note that we are generating the sequence now, and capture the settings.
		 * This is very important because only a robot code reset will re-initialize the auto sequence and merely 
		 * disabling and re-enabling the robot will re-run the sequence without re-initializing.
		 * 
		 * WARNING: This means that if you don't reset the robot, you will run with old game data!
		 * 
		 * This log message is a warning so you can catch that mistake instead of pulling your hair out debugging a phantom bug.
		 */
		Logger.printLabelled(LogType.INFO, this + ": Generating Auto Sequence",
							"Robot", robotStartingPosition,
							"NearSwitch", nearSwitchPosition,
							"Scale", scalePosition,
							"FarSwitch", farSwitchPosition,
							"AdjustedLeft", left,
							"AdjustedRight", right);

		/* ------------------------------------------------------------------------------------------------------
		 * Initialization
		 * ------------------------------------------------------------------------------------------------------ */

		/**  First, reset the gyro and the wheel encoders. */
		autoCmdList.addDriveTrainSensorResetSequenceSync();

		/**
		 * Next, return the elevator and the arm to the home position.  In competition, we will start in this
		 * position and the auto-reset logic will calibrate them automatically - making this step instant and
		 * unnecessary.  However, if we are testing, we want to make sure that we home the arm and the 
		 * elevator and get those counters reset or we may damage the robot.  So keeping these commands in
		 * the auto script is a robot-safety critical feature. 
		 */
		autoCmdList.addElevatorCalibrateSequenceSync(); /**  best effort attempt to calibrate the elevator sensor */
		autoCmdList.addArmCalibrateSequenceSync();      /**  best effort attempt to calibrate the arm sensor, will wait for completion */

		PlacementTargetType.SWITCH.moveAsync();

		/**  Keep track of whether we expect to be holding a cube at each step, so we can choose our speed wisely. */
		autoCmdList.setHaveCube(true);

		/**
		 * If we have failed to read the field setup. The only safe option is to drive forward and stop.
		 * There is a two deep pile of cubes near the switch, stop a foot short of them.
		 */
		if (!Robot.gameData.isGameDataValid()) {
			Logger.error(this + ": No game data.");
			autoCmdList.addDriveSync(Direction.FORWARD, inches(backWallToSwitch - robotBumperLength - 2 * cubeSize - 12), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			return;
		}

		/**
		 * Here begins the autonomous decision tree in which we consider our starting position and the configurations
		 * of switch and scale. We make these decisions in such a way as to cluster symmetric paths and use variables
		 * to handle them together. We bias to the left, meaning if we start on the left or right, we write all paths
		 * as if we are on the left. If we are in the center, we write paths as if we are are moving to the left.
		 */
		if (robotStartingPosition.equals(scalePosition)) {
			/**
			 * The robot and the scale are on the same side. Drive forward and approach the scale from the side.
			 */
			Logger.info(this + ": Robot and Scale are both at the " + robotStartingPosition + " position.");
			autoCmdList.addArmMoveToOverHeadShootingPositionAsync();
			if (useCurves) {
				/**
				 *  We are backwards in the end position
				 *  1. drive forward until our far end aligns with far edge of switch
				 *  2. slow S curve to align with scale front
				 *  3. 6 feet before we get there, raise the elevator
				 *  4. proceed more slowly to the target
				 *  5. deliver cube backwards
				 */
				double distanceToTarget = backWallToScaleTarget - robotBumperLength - finalDistanceToTarget;
				double firstLeg = backWallToFarSideOfSwitch;
				double sRadius = 10 * 12;
				double sDegrees = 35;
				double sLength = sCurveForwardShift(sRadius, sDegrees);
				double sWidth = sCurveSideShift(sRadius, sDegrees);
				double secondLeg = distanceToTarget - firstLeg - sLength;

				autoCmdList.addDriveSync(Direction.BACKWARD, inches(firstLeg), AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				autoCmdList.addCurveDegreesSync(Direction.BACKWARD, 35.0, inches(sRadius), Direction.COUNTERCLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FULL); 
				autoCmdList.addCurveDegreesSync(Direction.BACKWARD, 35.0, inches(sRadius), Direction.CLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FULL); 
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(secondLeg / 2), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				PlacementTargetType.SCALE_INVERTED.moveAsync();
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(secondLeg / 2), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			}
			else {
				double distanceToTarget = backWallToScaleTarget - robotBumperLength - finalDistanceToTarget;
				
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(distanceToTarget), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				PlacementTargetType.SCALE_INVERTED.moveAsync();
				autoCmdList.addQuickTurnSync(left, 90); /**  right but we're backwards */
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(43), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
				autoCmdList.addQuickTurnSync(right, 90); /**  left but we're backwards */
			}
		}
		else if (robotStartingPosition.equals(Direction.CENTER)) {
			/**
			 * If the robot is in the center, we're going to drop a cube into the switch
			 * on the correct side.  
			 */
			Logger.info(this + ": Robot is in the " + robotStartingPosition + " position, with the near switch at the " + nearSwitchPosition + " position.");
			autoCmdList.addArmMoveToShootingPositionAsync();
			if (useCurves) {
				/**
				 * An S curve. counterclockwise 1/4 turn followed by clockwise 1/4 turn leaves us in the same orientation 2r up and 2r over
				 * The distance to the scale less our length is how far we need to move forward. This S curve does that in 2 arcs, so set
				 * radius to half that. TODO: define field dimensions and our dimensions in variables.
				 */
				double distanceToTarget = backWallToSwitch - robotBumperLength - finalDistanceToTarget;
				double sideShiftToTarget = (switchWidth - robotBumperWidth) / 2 - 6; /**  left bumper 6 inches right of left edge of switch */
				double radius = 63.0;  /**  found by trial and error */
				double degrees = 54.0;
				
				autoCmdList.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), Direction.COUNTERCLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FLUID); 
				autoCmdList.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), Direction.CLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FLUID); 
			}
			else {
				double distanceToTarget = backWallToSwitch - robotBumperLength - finalDistanceToTarget;
				double sideShiftToTarget = (switchWidth - robotBumperWidth) / 2 - 6; /**  left bumper 6 inches right of left edge of switch */
				double turnClearance = 8.0;

				autoCmdList.addDriveSync(Direction.FORWARD, inches(turnClearance), AutonomousCommandGroup.TRANSITION_SPEED_FLUID); /**  enough to turn */
				autoCmdList.addQuickTurnSync(left, 90);
				autoCmdList.addDriveSync(Direction.FORWARD, inches(sideShiftToTarget), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addQuickTurnSync(right, 90);
				autoCmdList.addDriveSync(Direction.FORWARD, inches(distanceToTarget - turnClearance), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			}
		} else {
			/**
			 * Robot and  scale are on opposite side. Drive across the field between scale and switch.
			 * from far side we cross over between switch and scale and place block on scale
			 */
			Logger.info(this + ": Robot and Scale on opposite sides.  Robot is at the " + robotStartingPosition + " position and the Scale is at the " + scalePosition + " position.");
			autoCmdList.addArmMoveToOverHeadShootingPositionAsync();
			if (useCurves) {
				/**
				 *  Start backwards at ends. This branch has scale on opposite side.
				 *  1. drive forward until our far end aligns with far edge of switch
				 *  2. wide 90 degree turn to middle of alley (accounting for cubes)
				 *  3. 6 feet before we get there, raise the elevator
				 *  4. proceed more slowly to the target
				 *  5. deliver cube backwards
				 */
				double firstLeg = backWallToSwitch + switchDepth - robotBumperLength; /**  drive straight until aligned with end of switch */
				double firstCurveRadius = backWallToScaleAlley + scaleAlleyWidth / 2 - firstLeg; /**  turn from here into center of scaleAlley */
				
				/**  This is a bit complex. Distance to center from end, plus where we start takes us to the middle. Then go to the end of the scale target less our length and 6 inches from the end */
				double crossFieldDistance = fieldWidth / 2 - sideWallToFirstRobotStartPosition + scaleTargetWidth / 2 - robotBumperLength - 6;
				double secondCurveRadius = 24; /**  small so we don't clip the scale platform */
				double alleyStraightDistance = crossFieldDistance - firstCurveRadius - secondCurveRadius;

				autoCmdList.addDriveSync(Direction.BACKWARD, inches(firstLeg), AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				autoCmdList.addCurveDegreesSync(Direction.BACKWARD, 90.0, feet(firstCurveRadius), Direction.COUNTERCLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FULL); 
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(alleyStraightDistance / 2), AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				PlacementTargetType.SCALE_INVERTED.moveAsync();
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(alleyStraightDistance / 2), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addCurveDegreesSync(Direction.BACKWARD, 90.0, inches(secondCurveRadius), Direction.CLOCKWISE, AutonomousCommandGroup.TRANSITION_SPEED_FULL); 
			}
			else {
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(backWallToScaleAlley + scaleAlleyWidth / 2), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addQuickTurnSync(left, 90); /**  right but we're backwards */
				
				/**  This is a bit complex. Distance to center from end, plus where we start takes us to the middle. Then go to the end of the scale target less our length and 6 inches from the end */
				double crossFieldDistance = fieldWidth / 2 - sideWallToFirstRobotStartPosition + scaleTargetWidth / 2 - robotBumperLength - 6;

				autoCmdList.addDriveSync(Direction.BACKWARD, inches(crossFieldDistance / 2), AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				PlacementTargetType.SCALE_INVERTED.moveAsync();
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(crossFieldDistance / 2), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addQuickTurnSync(right, 90); /**  left but we're backwards */
				autoCmdList.addDriveSync(Direction.BACKWARD, inches(scaleAlleyWidth / 2 + scaleAlleyToTarget), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
			}
		}
		/**  NB: DeliverCubeCommandSequence will always wait for Elevator to reach target height, to avoid crashing */
		autoCmdList.addDeliverCubeSequenceSync();

		/**  Remember we let go of our cube, we can really fly now... */
		autoCmdList.setHaveCube(false);
	}

	/** Give ourselves a name for debugging */
	public String toString() {
		return "AutonymousCodeGenerator";
	}
}
