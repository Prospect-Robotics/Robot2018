package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.util.Formatter;

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
	 * Define some handy measurements. Define as double in implied inches. Doing
	 * math with groups of Length is TOO PAINFUL!
	 */

	/**
	 * cubes are placed along switches on the scale side and in pyramid on the other
	 * switch side
	 */
	private static final double cubeSize = 14;

	/** robot dimensions with and without bumpers */
	private static final double bumperThickness = 3.25;
	private static final double robotWheelbaseWidth = 31;
	private static final double robotWheelbaseLength = 20;
	private static final double robotBumperWidth = robotWheelbaseWidth + bumperThickness * 2;
	private static final double robotBumperLength = robotWheelbaseLength + bumperThickness * 2 + 2 * 1.125;
	private static final double finalDistanceToTarget = 20; // how close to get before we shoot cube
	private static final double jawProtrusion = 10; // overhang of jaws in front of bot. FIXME! measure this!
	private static final double cubeIntakeOverlap = 5; // aim jaws this far past edge of cube. FIXME! measure this!
	private static final double robotLengthIncJaws = robotBumperLength + jawProtrusion;

	/** absolute dimensions side to side */
	private static final double backWallWidth = 264;
	private static final double fieldWidth = 323.38;
	private static final double switchWidth = 152.88;
	private static final double scalePlatformWidth = 132.88;
	private static final double scaleFullWidth = 150.55;
	private static final double scaleTargetWidth = 56;

	/** field dimensions from back wall */
	private static final double fieldDepth = 648;
	private static final double scaleTargetDepth = 48.7;
	private static final double backWallToSwitch = 140;
	private static final double backWallToCubePyramid = backWallToSwitch - 3 * cubeSize; // pyramid of cubes on near side of switch
	private static final double backWallToFarSideOfSwitch = 196;
	private static final double backWallToScaleAlley = backWallToFarSideOfSwitch + cubeSize; // row of cubes on far side of switch
	private static final double backWallToScalePlatform = 261.47;
	private static final double backWallToScaleTarget = 299.65;
	private static final double distanceToCrossLine = 130;		// latest value per Jack - not calculated
	
	/** field dimensions from side wall */
	private static final double sideWallToFirstRobotStartPosition = 29.69;
	private static final double sideWallToFirstRobotEndPosition = sideWallToFirstRobotStartPosition + robotBumperWidth;
	private static final double sideWallToFirstRobotCenter = sideWallToFirstRobotStartPosition + robotBumperWidth / 2;
	private static final double sideWallToScaleTarget = 71.57;
	private static final double sideWallToScalePlatform = 95.25;
	private static final double sideWallToSwitch = 85.25;

	/** relative depth dimensions */
	private static final double switchDepth = backWallToFarSideOfSwitch - backWallToSwitch;
	private static final double switchToScalePlatform = backWallToScalePlatform - backWallToFarSideOfSwitch;
	private static final double scaleAlleyWidth = switchToScalePlatform - cubeSize;
	private static final double scalePlatformToTarget = backWallToScaleTarget - backWallToScalePlatform;

	private boolean useCurves;
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
	private static final double SPEED_TURN = 0.3;

	/** Elevator heights for placing cubes on the switch. */
	private static final Length ELEVATOR_HEIGHT_SWITCH = LengthUOM.Inches.create(27);

	/** Elevator heights for picking up a cube on from the ground. */
	private static final Length ELEVATOR_HEIGHT_GRAB_CUBE = LengthUOM.Inches.create(2);

	/** Elevator heights for picking up a cube on another cube. */
	private static final Length ELEVATOR_HEIGHT_GRAB_SECOND_CUBE = LengthUOM.Inches.create(14);

	/** Elevator height for placing cubes on the scale based on robot direction. */
	private static final Length ELEVATOR_HEIGHT_SCALE_FORWARD = LengthUOM.Inches.create(66);
	private static final Length ELEVATOR_HEIGHT_SCALE_BACKWARD = LengthUOM.Inches.create(66);

	/** Arm Position for Level extension. Use to grab and drop cube. */
	private static final Length ARM_POSITION_LEVEL = ArmConfiguration.ArmDegrees.create(150); // probably OK

	/** Arm Position below Level extension to avoid hitting hooks with elevator down. Use to grab and drop cube. */
	private static final Length ARM_POSITION_LOW = ArmConfiguration.ArmDegrees.create(150); // 150 on OI

	/** Arm Position for shooting over head */
	private static final Length ARM_POSITION_BACKWARD_SHOOT = ArmConfiguration.ArmDegrees.create(15);
	private static final Length ARM_POSITION_FORWARD_SHOOT = ArmConfiguration.ArmDegrees.create(120); // 120 on OI

	/** This is the commandGroup we are populating with our autonomous routine */
	private AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;

	/**
	 * This is the bias for invertible scripts that can run on left or right sides.
	 */
	private static Direction SCRIPT_BIAS = Direction.LEFT;

	private Direction left, right, clockwise, counterclockwise;

	/*
	 * We tried having the robot start on the sides facing backwards so it could throw the cube on to the switch
	 * over its shoulder and more quickly grab and place another cube.  When the Elevator binds, it can block the
	 * over-the-shoulder throw.  For now, revert to everything forwards at start.  Post season, fix over-the-shoulder
	 * because it is cool.
	 */
//	private Direction robotOnSideStartsFacing = Direction.BACKWARD;
	private Direction robotStartFacingDirectionOnSide = Direction.FORWARD;
	private Direction robotStartFacingDirectionInCenter = Direction.FORWARD;

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
			throw new IllegalArgumentException(Formatter.concat(
					"In ", startingPosition, " position, biased ", original, " should have been ", expected, "."));
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
	private void prepareForSwitchSync() {
		autoCmdList.elevator.addMoveToPositionSync(ELEVATOR_HEIGHT_SWITCH);
		autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_LEVEL);

	}

	/** Prepare arm for shooting */
	protected void prepareArmForShootingAsync(Direction direction) {
		if (direction == Direction.FORWARD) {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_FORWARD_SHOOT);
		} else {
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_BACKWARD_SHOOT);
		}
	}
	/** Prepare elevator for scale */
	protected void prepareElevatorForScaleAsync(Direction direction) {
		if (direction == Direction.FORWARD) {
			autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SCALE_FORWARD);
		} else {
			autoCmdList.elevator.addMoveToPositionAsync(ELEVATOR_HEIGHT_SCALE_BACKWARD);
		}
		autoCmdList.drive.setDriveSpeed(SPEED_ELEVATOR_UP);
	}

	/**
	 * When we're done delivering a cube, we call this routine to switch to cube
	 * grabbing mode.
	 */
	private void prepareForCubeGrabbingSync(Length elevatorHeight) {
		autoCmdList.elevator.addMoveToPositionAsync(elevatorHeight);
		autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_LOW);
		autoCmdList.elevator.addWaitForTargetPositionSync();
//		autoCmdList.arm.addWaitForTargetPositionSync();
		autoCmdList.drive.setDriveSpeed(SPEED_FULL);
	}
	protected void prepareForCubeGrabbingSync() {
		prepareForCubeGrabbingSync(ELEVATOR_HEIGHT_GRAB_CUBE);
	}
	protected void prepareForCubeGrabbingSecondSync() {
		prepareForCubeGrabbingSync(ELEVATOR_HEIGHT_GRAB_SECOND_CUBE);
	}

	/** Helper routine for center position. Deliver cube and return */
	private void deliverCenterCube(Length nextCubeHeight) {

		useCurves = true;		// TODO:  testing curves FOR CENTER ONLY, overriding the sendable chooser in the drive station for now - FIX
		
		if (useCurves) {
			double radius = 63; // found by trial and error
			double degrees = 54.0;

			autoCmdList.drive.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), counterclockwise, SPEED_FULL);
			autoCmdList.drive.addCurveDegreesSync(Direction.FORWARD, degrees, inches(radius), clockwise, SPEED_STOP);
			autoCmdList.cube.addDeliverSequenceSync();
			autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, degrees, inches(radius), clockwise, SPEED_FULL);
			autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, degrees, inches(radius), counterclockwise, SPEED_STOP);
			prepareForCubeGrabbingSync(nextCubeHeight);
		}
		else {
			double distanceToTarget = backWallToSwitch - robotBumperLength;
			double sideShiftToTarget = (switchWidth - robotBumperWidth) / 3;
			double straightEnds = (distanceToTarget - sideShiftToTarget - 12) / 3;
			autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(straightEnds), SPEED_TURN);
			autoCmdList.drive.addQuickTurnSync(left, 55);
			autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(sideShiftToTarget * Math.sqrt(2)), SPEED_TURN);
			autoCmdList.elevator.addMoveToPositionSync(ELEVATOR_HEIGHT_SWITCH);
			autoCmdList.arm.addMoveToPositionAsync(ARM_POSITION_LEVEL);
			autoCmdList.drive.addQuickTurnSync(right, 55);
			autoCmdList.elevator.addWaitForTargetPositionSync();
			autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(straightEnds+10), SPEED_STOP);
			autoCmdList.cube.addShootSequenceSync();
			
			//autoCmdList.cube.addDeliverSequenceSync();
			//autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(straightEnds), SPEED_TURN);
			//prepareForCubeGrabbingSync(nextCubeHeight);
			//autoCmdList.drive.addQuickTurnSync(left, 45);
			//autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(sideShiftToTarget * Math.sqrt(2)), SPEED_TURN);
			//autoCmdList.drive.addQuickTurnSync(right, 45);
			//autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(straightEnds), SPEED_STOP);
		}
	}

	private void getCenterCube(double distance) {
		autoCmdList.cube.addIntakeInAsync();
		autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(distance), SPEED_STOP);
//		prepareForSwitchAsync();
		autoCmdList.cube.addGrabSequenceSync();
		autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distance), SPEED_STOP);
	}
	/** Helper routine for center position. grab first cube in pyramid and return */
	private void getCenterFirstCube() {
		double backWallToFirstCube = backWallToCubePyramid - robotLengthIncJaws - cubeIntakeOverlap-4;
		getCenterCube(backWallToFirstCube);
	}
	/** Helper routine for center position. grab first cube in pyramid and return */
	private void getCenterSecondCube() {
		double backWallToSecondCube = backWallToCubePyramid + cubeSize - robotLengthIncJaws - cubeIntakeOverlap-8;
		getCenterCube(backWallToSecondCube);
	}
	/**
	 * Build a command sequence to be run during the Autonomous 15 second period.
	 *
	 * This code uses the gameData from the driver station and a sendable chooser on
	 * the Smart Dashboard to decide which sequence to run.
	 */
	@SuppressWarnings("unused")
	public AutonomousCommandGroupGenerator() {
		// Determine our game configuration
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = Robot.gameData.getNearSwitchPosition();
		Direction farSwitchPosition = Robot.gameData.getFarSwitchPosition();
		Direction scalePosition = Robot.gameData.getScalePosition();

		// Read user choices about our strategy
		useCurves = Robot.autonomousSelectorCurves.getSelected() == Direction.ON;
		/**
		 * Determine our biased directions. Assume if you're on a side, it's left and if
		 * you're in the center the switch will be to the left. This reduces the
		 * complexity of our choices.
		 *
		 * NOTE: This only allows us to interact with the switch when we start in the
		 * middle and only with the scale otherwise. If we want to do both, we will have
		 * to split off two directional biases. Us relative to switch and us relative to
		 * scale.
		 *
		 * @see getBiasedDirection
		 */
		left = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(), Direction.LEFT);
		right = getBiasedDirection(robotStartingPosition, Robot.gameData.getNearSwitchPosition(), Direction.RIGHT);

		// We similarly have a biased rotation.
		if (left == Direction.LEFT) {
			clockwise = Direction.CLOCKWISE;
			counterclockwise = Direction.COUNTERCLOCKWISE;
		} else {
			clockwise = Direction.COUNTERCLOCKWISE;
			counterclockwise = Direction.CLOCKWISE;
		}

		verifyDirections(); // Sanity check our biasing logic

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
		Logger.print(LogType.INFO, this, ": Generating Auto Sequence", "Robot", robotStartingPosition,
				"NearSwitch", nearSwitchPosition, "Scale", scalePosition, "FarSwitch", farSwitchPosition,
				"AdjustedLeft", left, "AdjustedRight", right);

		// Initialize the robot
		autoCmdList.resetAndCalibrateSync();

		/**
		 * Keep track of whether we expect to be holding a cube at each step, so we can
		 * choose our speed wisely.
		 */
		autoCmdList.cube.setHaveCube(true);

		// FIXME! bypass autonomous for competition - make this a smart dashboard flag
		if (true) {
			autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(distanceToCrossLine), SPEED_STOP);
			return;
		}

		/**
		 * If we have failed to read the field setup. The only safe option is to drive
		 * forward and stop. There is a two deep pile of cubes near the switch, stop a
		 * foot short of them.
		 */
		if (!Robot.gameData.isGameDataValid()) {
			Direction direction;
			Logger.error(this, ": No game data.");
//			direction = robotStartingPosition.equals(Direction.CENTER) ? Direction.FORWARD : Direction.BACKWARD;	// for when L and R go backwards
//			direction = Direction.FORWARD;
			direction = robotStartingPosition.equals(Direction.CENTER) ? robotStartFacingDirectionInCenter : robotStartFacingDirectionOnSide;	// for when L and R go backwards
//			autoCmdList.drive.addDriveSync(direction, inches(backWallToCubePyramid), SPEED_STOP);
			autoCmdList.drive.addDriveSync(direction, inches(distanceToCrossLine), SPEED_STOP);
		}

		/**
		 * This initial state may not be exactly what we want, but it's safe. We can
		 * change it later.
		 */
		prepareForSwitchSync();

		/**
		 * Here begins the autonomous decision tree in which we consider our starting
		 * position and the configurations of switch and scale. We make these decisions
		 * in such a way as to cluster symmetric paths and use variables to handle them
		 * together. We bias to the left, meaning if we start on the left or right, we
		 * write all paths as if we are on the left. If we are in the center, we write
		 * paths as if we are are moving to the left.
		 */
		if (robotStartingPosition.equals(scalePosition)) {
			prepareArmForShootingAsync(robotStartFacingDirectionOnSide);
			/**
			 * The robot and the scale are on the same side. Drive forward and approach the
			 * scale from the side.
			 */
			Logger.printFormat(LogType.INFO, "%s: Robot and scale are in the %s position.", this, scalePosition);

			/** This is the total side offset between us and the scale target center */
			double totalDistanceSide = sideWallToScaleTarget - sideWallToFirstRobotCenter;
			double totalDistanceAhead = (17*12);//backWallToScaleTarget + scaleTargetDepth/2;
			double distanceFromTargetEdge = (robotBumperLength/2 + finalDistanceToTarget);
			double diagDistFromTargetProjected = distanceFromTargetEdge / Math.sqrt(2);
			double radius = totalDistanceSide - diagDistFromTargetProjected;
			double distanceToDriveStraight = backWallToScaleTarget - (radius + diagDistFromTargetProjected);
			if (useCurves) {
				/**
				 * We are backwards on the left side. Working backwards, we will approach the
				 * target at a 45 degree angle to avoid the scale platform. The distance we need
				 * to travel from left to right will be the sine of 45 degrees times the radius
				 * of some circle. From this we find the radius. Now subtract the left->right
				 * offset from the forward distance. Travel that distance straight. Then raise
				 * the elevator. Then follow our curve to the target.
				 */
				autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(distanceToDriveStraight), SPEED_FULL);
				prepareElevatorForScaleAsync(robotStartFacingDirectionOnSide);
				autoCmdList.drive.addCurveDegreesSync(robotStartFacingDirectionOnSide, 45.0, inches(radius), counterclockwise, SPEED_STOP);	// direction of turn is counterclockwise whether we are traveling forwards or backwards
				autoCmdList.cube.addShootSequenceSync();
			} else {
				autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(totalDistanceAhead), SPEED_TURN);
				prepareElevatorForScaleAsync(robotStartFacingDirectionOnSide);
				
				// Values from practice bot in our lab
//				autoCmdList.arm.addMoveToPositionAsync(ArmConfiguration.ArmDegrees.create(60));
//				autoCmdList.drive.addQuickTurnSync(right, 30);
//				autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(30), SPEED_TURN);
				
				// Values tuned by Jack at CVR
				autoCmdList.arm.addMoveToPositionAsync(ArmConfiguration.ArmDegrees.create(120));
				autoCmdList.drive.addQuickTurnSync(right, 40);
				autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(36), SPEED_TURN);
				
//				autoCmdList.cube.addJawsOpenSync();			// Just dropping it hasn't worked well in practice - throw it instead
				autoCmdList.cube.addShootSequenceSync();

				boolean enableSecondCubeGrab;		// TODO:  Fix autonomous and then nail down the code
				if (enableSecondCubeGrab = false) { 
					autoCmdList.time.addDelayInSecondsSync(0.5);
					autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(12), SPEED_TURN);
					autoCmdList.drive.addQuickTurnSync(left, 30);
					autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(24), SPEED_TURN);
					autoCmdList.resetAndCalibrateSync();
					autoCmdList.drive.addQuickTurnSync(right, 150);
					autoCmdList.drive.addDriveSync(Direction.FORWARD, inches(24), SPEED_STOP); // was SPEED_TURN
				}
			}
			
		} else if (robotStartingPosition.equals(Direction.CENTER)) {
			/** If the robot is in the center, we're going to drop a cube into the switch on the correct side. */
			Logger.printFormat(LogType.INFO, "%s: Robot is in the center position, with the near switch at the %s position.", this, nearSwitchPosition);
			
			deliverCenterCube(ELEVATOR_HEIGHT_GRAB_CUBE);
//			getCenterFirstCube();
//			deliverCenterCube(ELEVATOR_HEIGHT_GRAB_SECOND_CUBE);
//			getCenterSecondCube();
//			deliverCenterCube(ELEVATOR_HEIGHT_GRAB_CUBE);
			
		} else {
			prepareArmForShootingAsync(robotStartFacingDirectionOnSide);
			/**
			 * Robot and scale are on opposite side. 
			 * Driving down the alley is not working.
			 * Instead, drive forward to the switch and stop.
			 * If we are on our side of the switch, place a block
			 */
			Logger.printFormat(LogType.INFO, "%s: Robot is in the %s position. Opposite the scale in the %s position.", this, robotStartingPosition, scalePosition);

			//  This code assumes that we can rotate 90.  We can't do that with the competition bot on carpet.
			//  Try just going forward to the switch straight and deliver a cube if it is on the correct side.
//			autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(101), SPEED_STOP);
//			if(robotStartingPosition.equals(nearSwitchPosition)) {
//				autoCmdList.drive.addQuickTurnSync(right, 90);
//				autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(12), SPEED_STOP);
//				autoCmdList.cube.addShootSequenceSync();
//			}
			
			 
			autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(backWallToSwitch - robotBumperLength), SPEED_STOP);
			if(robotStartingPosition.equals(nearSwitchPosition)) {
				autoCmdList.drive.addDriveSync(robotStartFacingDirectionOnSide, inches(4), SPEED_STOP);	// make sure we are touching the switch, but slowly - will stop if stalled
				autoCmdList.cube.addShootSequenceSync();
			}

			/**
			 * Robot and scale are on opposite side. Drive across the field between scale
			 * and switch. Note that we must avoid driving over the scale platform. We do
			 * this by circling back to face the scale target at 45 degrees. The same as
			 * when we are on the same side.
			 */
//			/** distance from center of robot to center of scale alley */
//			double distanceToFirstTurn = backWallToScaleAlley + scaleAlleyWidth/2 - robotBumperLength/2;
//
//			/** distance from center of the robot from the center of the field */
//			double distanceToCenter = fieldWidth/2 - sideWallToFirstRobotCenter;
//
//			/** distance down scale alley from the center of the field to the target */
//			double distanceFromCenter = scaleFullWidth/2 - scaleTargetWidth/2;
//
//			/** project our desired distance to the target onto distance down scale alley */
//			double projectedDistToTarget = (finalDistanceToTarget + (robotBumperLength/2)) / Math.sqrt(2) + scaleTargetWidth/2;
//
//			/** make a right turn down scale alley */
//			double firstAngle = 90.0;
//
//			/** pass the target and double back. right turn + 45 degrees to avoid hitting scale platform */
//			double secondAngle = 135.0;
//
//			if (useCurves) {
//				double firstRadius = scaleAlleyWidth;
//
//				double secondRadius = (scaleAlleyWidth/2 - projectedDistToTarget) * Math.sqrt(2);
//
//				double distanceFromCenterToSecondTurn = distanceFromCenter - projectedDistToTarget;
//
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToFirstTurn - firstRadius), SPEED_FULL);
//				autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, firstAngle, inches(firstRadius), counterclockwise, SPEED_FULL);
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToCenter - firstRadius), SPEED_FULL);
//				prepareElevatorForScaleAsync(Direction.BACKWARD); // half way down scale alley
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceFromCenterToSecondTurn), SPEED_FULL);
//				autoCmdList.drive.addCurveDegreesSync(Direction.BACKWARD, secondAngle, inches(secondRadius), clockwise, SPEED_STOP);
//			} else {
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToFirstTurn), SPEED_TURN);
//				autoCmdList.drive.addQuickTurnSync(left, 90); /** right but we're backwards */
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceToCenter), SPEED_FULL);
//				prepareElevatorForScaleAsync(Direction.BACKWARD); // half way down scale alley
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(distanceFromCenter + projectedDistToTarget), SPEED_TURN);
//				autoCmdList.drive.addQuickTurnSync(right, 135); /** left but we're backwards */
//
//				double finalDistance = projectedDistToTarget * Math.sqrt(2) - finalDistanceToTarget - robotBumperLength/2;
//				autoCmdList.drive.addDriveSync(Direction.BACKWARD, inches(finalDistance), SPEED_STOP);
//			}
//			autoCmdList.cube.addShootSequenceSync();
		}

		/** time to switch to cube grabbing mode */

		/** Remember we let go of our cube, we can really fly now... */
		autoCmdList.cube.setHaveCube(false);
	}

	/** Give ourselves a name for debugging */
	public String toString() {
		return "AutonymousCodeGenerator";
	}
}
