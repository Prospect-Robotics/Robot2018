package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.PlacementTargetType;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;


/**
 * Generate a series of sequential commands to operate autonomously. Takes into
 * account game data. Used exclusively by AutonomousCommandGroup
 */
public class AutonomousCommandGroupGenerator {
	/**
	 * This is the commandGroup we are populating with our autonomous routine
	 */
	private AutonomousCommandGroup autoCmdList = Robot.autonomousCommand;

	/**
	 * This is the bias for invertible scripts that can run on left or right sides.   
	 */
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
	
	/**
	 * Built-in unit test to verify that our inversion logic matches our expectations 
	 */
	private static void verifyDirections() {		
		// If we start at the left, there's no change...
		verify(Direction.LEFT,   Direction.LEFT,  Direction.LEFT,   Direction.LEFT);
		verify(Direction.LEFT,   Direction.LEFT,  Direction.RIGHT,  Direction.RIGHT);
		verify(Direction.LEFT,   Direction.RIGHT, Direction.LEFT,   Direction.LEFT);
		verify(Direction.LEFT,   Direction.RIGHT, Direction.RIGHT,  Direction.RIGHT);
		
		// If we start on the right, we always want the opposite
		verify(Direction.RIGHT,  Direction.LEFT,  Direction.LEFT,   Direction.RIGHT); // starting on right, invert logic
		verify(Direction.RIGHT,  Direction.LEFT,  Direction.RIGHT,  Direction.LEFT);  // starting on right, invert logic
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.LEFT,   Direction.RIGHT); // starting on right, invert logic
		verify(Direction.RIGHT,  Direction.RIGHT, Direction.RIGHT,  Direction.LEFT);  // starting on right, invert logic

		// If we start at the center, we track the near switch and will reverse direction if the target is on the right
		verify(Direction.CENTER, Direction.LEFT,  Direction.LEFT,   Direction.LEFT);
		verify(Direction.CENTER, Direction.LEFT,  Direction.RIGHT,  Direction.RIGHT);
		verify(Direction.CENTER, Direction.RIGHT, Direction.LEFT,   Direction.RIGHT); // goal on right, invert logic
		verify(Direction.CENTER, Direction.RIGHT, Direction.RIGHT,  Direction.LEFT);  // goal on right, invert logic
	}
	/**
	 * Give ourselves a name for debugging
	 */
	public String toString() {
		return "AutonymousCodeGenerator";
	}
	/**
	 * Helper to create a length in inches, scaled appropriately
	 * @param inches A value in inches in double to wrap in a Length
	 * @return A length
	 */
	Length inches(double inches) {
		return AutonomousCommandGroup.inches(inches);
	}
	/**
	 * Helper to create a length in feet, scaled appropriately
	 * Package scoped on purpose
	 * @param A value in feet to wrap in a Length
	 * @return A length
	 */
	Length feet(double feet) {
		return AutonomousCommandGroup.feet(feet);
	}
	/**
	 * Helpers for using S curves to offset our position. If you travel the same amount around two circles
	 * of opposite rotation, you end up pointed the same way, but offset diagonally
	 */
	double sCurveSideShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (1 - Math.cos(Math.toRadians(degrees)));
	}
	double sCurveForwardShift(double curveRadius, double degrees) {
		return 2 * curveRadius * (1 - Math.sin(Math.toRadians(degrees)));
	}
	/**
	 * Build a command sequence to be run during the Autonomous 15 second period.
	 * 
	 * This code uses the gameData from the driver station and a sendable chooser 
	 * on the Smart Dashboard to decide which sequence to run.
	 */
	@SuppressWarnings("unused")
	public AutonomousCommandGroupGenerator() {
		Length radius;  // scratch pad for curves
		// Determine our game configuration
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = Robot.gameData.getNearSwitchPosition();
		Direction farSwitchPosition = Robot.gameData.getFarSwitchPosition();
		Direction scalePosition = Robot.gameData.getScalePosition();
		
		// Read user choices about our strategy
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

		/*
		 * Sanity check our biasing logic 
		 */
		verifyDirections();
		
		/**
		 * Define some handy measurements.
		 */
		// robot dimensions with and without bumpers
		Length robotWheelbaseWidth = inches(33);
		Length robotWheelbaseLength = inches(46);
		Length robotBumperWidth = robotWheelbaseWidth.add(4);
		Length robotBumperLength = robotWheelbaseLength.add(4);

		// field dimensions near to far
		Length backWallToSwitch = inches(140);
		Length switchDeth = inches(56);
		Length switchToScake = inches(65);
		Length scalePlatformDepth = inches(125);
		
		// dimensions side to side
		Length sideWallToFirstRobotStartPosition = inches(29.69);
		Length backWallWidth = inches(264);
		Length switchWidth = inches(152.88);
		
		/*
		 * Make a note that we are generating the sequence now, and capture the settings.
		 * This is very important because only a robot code reset will re-initialize the auto sequence and merely 
		 * disabling and re-enabling the robot will re-run the sequence without re-initializing.
		 * 
		 * WARNING: This means that if you don't reset the robot, you will run with old game data!
		 * 
		 * This log message is a warning so you can catch that mistake instead of pulling your hair out debugging a phantom bug.
		 */
		Logger.info(this + ": Generating Auto Sequence.  Robot=" + robotStartingPosition + " NearSwitch=" + nearSwitchPosition + " Scale=" + scalePosition + " FarSwitch=" + farSwitchPosition + " AdjustedLeft=" + left + " AdjustedRight=" + right + ".");

		/* ------------------------------------------------------------------------------------------------------
		 * Initialization
		 * ------------------------------------------------------------------------------------------------------ */

		// First, reset the gyro and the wheel encoders.
		autoCmdList.addDriveTrainSensorResetSequenceSync();

		/*
		 * Next, return the elevator and the arm to the home position.  In competition, we will start in this
		 * position and the auto-reset logic will calibrate them automatically - making this step instant and
		 * unnecessary.  However, if we are testing, we want to make sure that we home the arm and the 
		 * elevator and get those counters reset or we may damage the robot.  So keeping these commands in
		 * the auto script is a robot-safety critical feature. 
		 */
//		autoCmdList.addElevatorCalibrateSequenceSync(); // best effort attempt to calibrate the elevator sensor
//		autoCmdList.addArmCalibrateSequenceSync();      // best effort attempt to calibrate the arm sensor, will wait for completion

		// Keep track of whether we expect to be holding a cube at each step, so we can choose our speed wisely.
		autoCmdList.setHaveCube(true);

		/**
		 * Here begins the autonomous decision tree in which we consider our starting position and the configurations
		 * of switch and scale. We make these decisions in such a way as to cluster symmetric paths and use variables
		 * to handle them together. We bias to the left, meaning if we start on the left or right, we write all paths
		 * as if we are on the left. If we are in the center, we write paths as if we are are moving to the left.
		 */
		if (!Robot.gameData.isGameDataValid()) {
			/**
			 * We have failed to read the field setup. The only safe option is to drive forward 5 feet and stop.
			 */
			Logger.error(this + ": No game data.");
			autoCmdList.addDriveForwardSync(feet(5), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			// NB: We still have a cube, so don't call setHaveCube here.
		} 
		else if (robotStartingPosition.equals(scalePosition)) {
			/**
			 * The robot and the scale are on the same side. Drive forward and approach the scale from the side.
			 */
			Logger.info(this + ": Robot and Scale are both at the " + robotStartingPosition + " position.");
			if (useCurves) {
				/**
				 * The scale is straight ahead, but we want to approach from the side. Deflect towards the wall and
				 * back so we end up straight, but closer to the wall so that we can approach the scale from the side.
				 * WARNING! We are less than 30 inches from the wall
				 */
				radius = feet(5);
			}
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.addDriveForwardSync(feet(24), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			// Start raising elevator while we are turning...
			autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SCALE);
			// Turn to face the scale
			autoCmdList.addQuickTurnSync(right, 90);
			// NB: DeliverCubeCommandSequence will wait for Elevator to reach target height
			autoCmdList.addDeliverCubeSequenceSync(PlacementTargetType.SCALE, true /* return to switch place position */);
			// Remember we let go of our cube, we can really fly now...
			autoCmdList.setHaveCube(false);
		}
		else if (robotStartingPosition.equals(Direction.CENTER)) {
			/*
			 * If the robot is in the center, we're going to drop a cube into the switch
			 * on the correct side.  
			 * NB: We write this script as if the our switch is active to the left of the  
			 * robot.  If this isn't the case, the script will be run inverted.
			 */
			Logger.info(this + ": Robot is in the " + robotStartingPosition + " position, with the near switch at the " + nearSwitchPosition + " position.");
			if (useCurves) {
				/**
				 * An S curve. counterclockwise 1/4 turn followed by clockwise 1/4 turn leaves us in the same orientation 2r up and 2r over
				 * The distance to the scale less our length is how far we need to move forward. This S curve does that in 2 arcs, so set
				 * radius to half that. TODO: define field dimensions and our dimensions in variables.
				 */
				radius = inches((140-46)/2);
				autoCmdList.addCurveForwardSync(radius.multiply(Math.PI/2), radius, counterclockwise, AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				autoCmdList.addCurveForwardSync(radius.multiply(Math.PI/2), radius, clockwise, AutonomousCommandGroup.TRANSITION_SPEED_STOP);
/*
This is what Jack was working on Thursday, and it placed the cube on the right side of the switch, which I think is wrong...
but I wanted to capture it in case we wanted to refer to it.  Once this routine is tested and finished I expect this comment
and all the non-curve paths will go away.

We do need to decide if fallback non-curves is something to maintain in case an encoder fails.
  
autoCmdList.addCurveForwardSync(LengthUOM.Feet.create(5), LengthUOM.Feet.create(5.00), true,  1.0);
autoCmdList.addCurveForwardSync(LengthUOM.Feet.create(5), LengthUOM.Feet.create(5.00), false, 0.0);
autoCmdList.addArmMoveToHighPositionAsync();
autoCmdList.addArmWaitForTargetPositionSync();
autoCmdList.addArmMoveToShootingPositionAsync();
autoCmdList.addArmWaitForTargetPositionSync();
autoCmdList.addShootCubeSequence();
 */
			}
			else {
				autoCmdList.addDriveForwardSync(inches(8), AutonomousCommandGroup.TRANSITION_SPEED_FLUID); // enough to turn
				autoCmdList.addQuickTurnSync(left, 45);
				autoCmdList.addDriveForwardSync(inches(121), AutonomousCommandGroup.TRANSITION_SPEED_FLUID); // diagonally from start to far side of near switch
				autoCmdList.addQuickTurnSync(right, 45);
			}
			// NB: DeliverCubeCommandSequence will always wait for Elevator to reach target height, to avoid crashing
			autoCmdList.addDeliverCubeSequenceSync(PlacementTargetType.SWITCH, true /* return to switch place position */);
			// Remember we let go of our cube, we can really fly now...
			autoCmdList.setHaveCube(false);
		} else {
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
			Logger.info(this + ": Robot and Scale on opposite sides.  Robot is at the " + robotStartingPosition + " position and the Scale is at the " + scalePosition + " position.");
			// Tell the Elevator to go to the switch height, but don't wat for it.  It's low enough it's not a problem to start moving.
			autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SWITCH);
			// Move the Arm down to the high position 
			autoCmdList.addArmMoveToHighPositionAsync();
			if (useCurves) {
				// An S curve. counterclockwise 1/8 turn followed by clockwise 3/8 turn leaves us
				// clear of the switch and pointing down the alley between switch and scale
				radius = feet(5);
				autoCmdList.addCurveForwardSync(radius.multiply(Math.PI/4), radius, clockwise, AutonomousCommandGroup.TRANSITION_SPEED_FULL);				
				autoCmdList.addCurveForwardSync(radius.multiply(3*Math.PI/4), radius, counterclockwise, AutonomousCommandGroup.TRANSITION_SPEED_FULL);
				// now down the alley half way, then slow down and raise the elevator as we continue
				autoCmdList.addDriveForwardSync(feet(14), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SCALE);
				// a bit more forward and then counterclockwise toward the scale
				autoCmdList.addDriveForwardSync(feet(5), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				radius = feet(3);
				autoCmdList.addCurveForwardSync(radius.multiply(Math.PI/2), radius, counterclockwise, AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			}
			else {
				autoCmdList.addDriveForwardSync(feet(14), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addQuickTurnSync(right, 90);
				autoCmdList.addDriveForwardSync(feet(15), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
				autoCmdList.addQuickTurnSync(left, 90);
				autoCmdList.addDriveForwardSync(feet(8), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
				autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SCALE);
				autoCmdList.addQuickTurnSync(left, 90);
			}
			// NB: DeliverCubeCommandSequence will wait for Elevator to reach target height
			autoCmdList.addDeliverCubeSequenceSync(PlacementTargetType.SCALE, true /* return to switch place position */);
			// Remember we let go of our cube, we can really fly now...
			autoCmdList.setHaveCube(false);
		}
	}
}
