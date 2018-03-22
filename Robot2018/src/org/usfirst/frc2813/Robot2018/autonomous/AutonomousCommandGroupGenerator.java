package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.PlacementTargetType;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
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
	 * LEFT        <any>       <any>             <unchanged>
	 * 
	 * RIGHT       <any>       <any>             <reversed>

	 * NEUTRAL     <any>       LEFT              <unchanged>        
	 * NEUTRAL     <any>       RIGHT             <reversed>         
	 * 
	 * @see SCRIPT_BIAS
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
	
	/*
	 * Give ourselves a name for debugging
	 */
	public String toString() {
		return "AutonymousCodeGenerator";
	}
	
	/*
	 * Helper to create a length in inches, scaled appropriately
	 */
	Length inches(double inches) {
		return AutonomousCommandGroup.inches(inches);
	}
	
	/*
	 * Helper to create a length in feet, scaled appropriately
	 * Package scoped on purpose
	 */
	Length feet(double feet) {
		return AutonomousCommandGroup.feet(feet);
	}

	/**
	 * Build a command sequence to be run during the Autonomous 15 second period.
	 * 
	 * This code uses the gameData from the driver station and a sendable chooser 
	 * on the Smart Dashboard to decide which sequence to run.
	 */
	public AutonomousCommandGroupGenerator() {
		// Determine our game configuration
		Direction robotStartingPosition = Robot.positionSelector.getSelected();
		Direction nearSwitchPosition = RobotMap.gameData.getNearSwitchPosition();
		Direction farSwitchPosition = RobotMap.gameData.getFarSwitchPosition();
		Direction scalePosition = RobotMap.gameData.getScalePosition();

		/**
		 * Determine our biased directions. 
		 * @see getBiasedDirection 
		 */
		Direction left     = getBiasedDirection(robotStartingPosition, RobotMap.gameData.getNearSwitchPosition(), Direction.LEFT);
		Direction right    = getBiasedDirection(robotStartingPosition, RobotMap.gameData.getNearSwitchPosition(), Direction.RIGHT);

		/*
		 * Sanity check our biasing logic 
		 */
		verifyDirections();
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
		autoCmdList.addElevatorCalibrateSequenceSync(); // best effort attempt to calibrate the elevator sensor
		autoCmdList.addArmCalibrateSequenceSync();      // best effort attempt to calibrate the arm sensor, will wait for completion

		// Keep track of whether we expect to be holding a cube at each step, so we can choose our speed wisely.
		autoCmdList.setHaveCube(true);
		
		// Tell the Elevator to go to the switch height, but don't wat for it.  It's low enough it's not a problem to start moving.
		autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SWITCH);

		// Move the Arm down to the high position 
		autoCmdList.addArmMoveToHighPositionAsync();

		if (!RobotMap.gameData.isGameDataValid()) {
			// Make a note of our lack of configuration 
			Logger.error(this + ": No game data.");
			// Just cross the auto line
			autoCmdList.addDriveForwardSync(feet(5), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			// NB: We still have a cube, so don't call setHaveCube here.
		} 
		else if (robotStartingPosition.equals(scalePosition)) {
			/*
    		 * If the robot and the scale are on the same side, 
    		 * drive forward and drop a cube into the scale from the end.
    		 * 
			 * NB: We write this script as if the robot and scale are both on the left,
			 * it will also be used inverted for when the robot and scale are both on the right.
			 */
			Logger.info(this + ": Robot and Scale are both at the " + robotStartingPosition + " position.");
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
			Logger.info(this + ": Robot and Scale on opposite sides.  Robot is at the " + robotStartingPosition + " position and the Scale is at the " + scalePosition + " position.");
			autoCmdList.addDriveForwardSync(feet(14), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
			autoCmdList.addQuickTurnSync(right, 90);
			autoCmdList.addDriveForwardSync(feet(15), AutonomousCommandGroup.TRANSITION_SPEED_FLUID);
			autoCmdList.addQuickTurnSync(left, 90);
			autoCmdList.addDriveForwardSync(feet(8), AutonomousCommandGroup.TRANSITION_SPEED_STOP);
			autoCmdList.addElevatorMoveToPlacementHeightAsync(PlacementTargetType.SCALE);
			autoCmdList.addQuickTurnSync(left, 90);
			// NB: DeliverCubeCommandSequence will wait for Elevator to reach target height
			autoCmdList.addDeliverCubeSequenceSync(PlacementTargetType.SCALE, true /* return to switch place position */);
			// Remember we let go of our cube, we can really fly now...
			autoCmdList.setHaveCube(false);
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
			Logger.info(this + ": Robot is in the " + robotStartingPosition + " position, with the near switch at the " + nearSwitchPosition + " position.");
			autoCmdList.addDriveForwardSync(inches(8), AutonomousCommandGroup.TRANSITION_SPEED_FLUID); // enough to turn
			autoCmdList.addQuickTurnSync(left, 45);
			autoCmdList.addDriveForwardSync(feet(6), AutonomousCommandGroup.TRANSITION_SPEED_FLUID); // diagonally from start to far side of near switch
			autoCmdList.addQuickTurnSync(right, 45);
			// NB: DeliverCubeCommandSequence will always wait for Elevator to reach target height, to avoid crashing
			autoCmdList.addDeliverCubeSequenceSync(PlacementTargetType.SWITCH, true /* return to switch place position */);
			// Remember we let go of our cube, we can really fly now...
			autoCmdList.setHaveCube(false);
		}
	}
}
