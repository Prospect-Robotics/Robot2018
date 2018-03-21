package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.PlacementTargetType;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.auto.PIDAutoDrive;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoCurveSync;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainQuickTurnSync;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainResetEncodersInstant;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainResetGyroInstant;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSpinAsync;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeStopInstant;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorCalibrateSensorAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePositionAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForHardLimitSwitchSync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForTargetPositionSync;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetStateInstant;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;


/**
 * AutonomousCommandGroup is a subclass of {@link CommandGroup} with helper methods and a little state
 * so that we can more easily build scripts.  This class is used by {@link AutonomousCommandGroupGenerator}
 * to build the autonomous command sequences.
 * 
 * Naming Conventions For adding individual Commands:
 * 
 * add<device><action><Async|Sync>
 * 
 * Naming Conventions for adding command sequences:
 *  
 * add<Goal>Sequence<Sync|Async>:
 * 
 *     where *Async means that the command doesn't finish what it started and you better make sure it's 
 *     done later in your script.
 *     where *Sync means the command is complete before it returns.
 *      
 */
public class AutonomousCommandGroup extends CommandGroup {

	/* ------------------------------------------------------------------------------------------------------
	 * Configuration Settings
	 * ------------------------------------------------------------------------------------------------------ */
	
	/**
	 * This is the sticky setting to be used for all subsequent commands created for driving forwards. 
	 */
	private double driveSpeed = 1;
	/**
	 * This is the sticky setting to be used for all subsequent commands created for turning in place. 
	 */
	private double turnSpeed = 0.25;
	/**
	 * This is the sticky setting to be used for all subsequent commands created for driving in an arc. 
	 */
	private double curveSpeed = 0.4;
	/**
	 * This is the sticky setting for the last speed as we came out of a move.
	 * TODO: This is not used. 
	 */
	private double currentSpeed = 0.0;	
	/**
	 * Keep track of whether we think we should have a cube at this point in the sequence, so we can
	 * scale back movements if necessary.
	 */
	private boolean haveCube = true;

	/* ------------------------------------------------------------------------------------------------------
	 * Constants
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 *  FIXME! ideally this should be scaled by the projection of the upcoming
	 *  angle if we are about to turn. Do this by scaling by the cosine of the
	 *  angle and clamping at +-90 degrees
	 *  Package scoped on purpose
	 */
	static final double TRANSITION_SPEED_FLUID = 0.2;
	/**
	 * Speed zero for stopping after a move.
	 */
	static final double TRANSITION_SPEED_STOP = 0.0;
	/**
	We needed the ability to scale the distances involved for "mini testing" 
	when we don't have sufficient surface area for testing.  
	This should really be coming from a sendable chooser.
	*/
	private static double DISTANCE_SCALING_MULTIPLIER = 0.2;
	/**
	 * Elevator height for placing cubes on the scale
	 */
	static final Length ELEVATOR_HEIGHT_FOR_SCALE_CUBE_PLACEMENT = inches(60);
	/**
	 * Elevator height for placing cubes on the switch
	 */
	static final Length ELEVATOR_HEIGHT_FOR_SWITCH_CUBE_PLACEMENT = inches(24);
	/**
	 * Arm Position for Level extension
	 * TODO: This should be in ArmDegrees
	 */
	static final Length ARM_POSITION_FOR_LEVEL = inches(12);
	/**
	 * Arm Position for holding high
	 * TODO: This should be in ArmDegrees
	 */
	static final Length ARM_POSITION_HIGH = inches(20);

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for changing sticky settings values used by add command helpers 
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Set the sticky setting for the speed for drive speed.  This value will be used for all subsequent commands created for driving forwards.
	 */
	public void setDriveSpeed(double driveSpeed) { 
		this.driveSpeed = driveSpeed; 
	}
	/**
	 * Set the sticky setting for the speed for turning.  This value will be used for all subsequent commands created for turning.
	 */
	public void setTurnSpeed(double turnSpeed) { 
		this.turnSpeed = turnSpeed;
	}
	/**
	 * Set the sticky setting for the speed for taking curves.  This value will be used for all subsequent commands created for curved movement.
	 */
	public void setCurveSpeed(double curveSpeed) { 
		this.curveSpeed = curveSpeed; 
	}
	/**
	 * Set the sticky hint about whether we have a cube or not, which can help us scale back our speed to keep it.
	 */
	public void setHaveCube(boolean haveCube) { 
		this.haveCube = haveCube; 
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for calculating scaled distances.
	 * ------------------------------------------------------------------------------------------------------ */

	/*
	 * Helper to create a distance in inches, scaled appropriately
	 */
	static Length inches(double inches) {
		return LengthUOM.Inches.create(inches).multiply(DISTANCE_SCALING_MULTIPLIER);
	}

	/*
	 * Helper to create a distance in feet, scaled appropriately
	 * Package scoped on purpose
	 */
	static Length feet(double feet) {
		return LengthUOM.Feet.create(feet).multiply(DISTANCE_SCALING_MULTIPLIER);
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for keeping the code clean
	 * ------------------------------------------------------------------------------------------------------ */

	/*
	 * Helper to get the placement height of a target
	 */
	public Length getPlacementHeight(PlacementTargetType target) {
		switch(target) {
		case SCALE:
			return ELEVATOR_HEIGHT_FOR_SCALE_CUBE_PLACEMENT;
		case SWITCH:
			return ELEVATOR_HEIGHT_FOR_SWITCH_CUBE_PLACEMENT;
		default:
			throw new IllegalArgumentException("Unsuported target: " + target);
		}
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding Drive Train commands
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Add commands to reset the drive train encoders and gyros (typically called at the start of a match)
	 */
	public void addDriveTrainSensorResetSequenceSync() {
		addSequential(new DriveTrainResetEncodersInstant(Robot.driveTrain));
		addSequential(new DriveTrainResetGyroInstant(Robot.driveTrain));
	}
	/**
	 * Add a command for driving forward for a set distance, with a desired speed at the end of the movement.
	 * TODO: This is going to be the new default.
	 */
	private void addDriveCommandSync(Direction direction, Length distance, double endSpeed) {
		addSequential(new PIDAutoDrive(driveSpeed, direction, distance.convertTo(LengthUOM.Inches).getValue(), currentSpeed, endSpeed));
		currentSpeed = endSpeed;
	}
	/**
	 * Add a command for driving forward for the indicated distance, with a desired speed at the end of the movement.
	 * @see addDriveCommand
	 */
	public void addDriveForwardSync(Length distance, double endSpeed) {
		addDriveCommandSync(Direction.FORWARD, distance, endSpeed);
	}
	/**
	 * Add a command for driving reverse for the indicated distance, with a desired speed at the end of the movement.
	 * @see addDriveCommand
	 */
	public void addDriveBackwardSync(Length distance, double endSpeed) {
		addDriveCommandSync(Direction.BACKWARD, distance, endSpeed);
	}
	/**
	 * Create a command to spin in place, until we reach a specific *relative* angle.  Will turn in either direction
	 * until that relative angle is hit, accounting for any overshoot by reversing direction for smaller and smaller
	 * moves until the target is it.  Right now QuickTurnCommand doesn't have PID, but continues until it gets close
	 * enough.
	 */
	public void addQuickTurnSync(Direction direction, double relativeAgle) {
		addSequential(new DriveTrainQuickTurnSync(Robot.driveTrain, direction, relativeAgle, turnSpeed));
	}
	/**
	 * Create a new command to turn in an arc
	 */
	private void curve(Direction direction, double angle, double radius, boolean clockwise) {
		double speed = curveSpeed;
		if (direction == Direction.BACKWARD) {
			speed = -speed;
			angle = -angle;
		}
		if (!clockwise) {
			radius = -radius;
			angle = -angle;
		}
		addSequential(new DriveTrainAutoCurveSync(Robot.driveTrain, speed, angle, radius));		
	}
	/**
	 * Create a new command to drive forwards and turn clockwise.  How far?  How fast? How long?
	 * TODO: This needs work.
	 */
	public void addCurveClockForwardSync(double angle, double radius) {
		curve(Direction.FORWARD, angle, radius, true);
	}
	/**
	 * Create a new command to drive forwards and turn counter-clockwise.  How far?  How fast? How long?
	 * TODO: This needs work.
	 */
	public void addCurveCounterForwardSync(double angle, double radius) {
		curve(Direction.FORWARD, angle, radius, false);
	}
	/**
	 * Create a new command to drive backwards and turn clockwise.  How far?  How fast? How long?
	 * TODO: This needs work.
	 */
	public void addCurveClockBackwardSync(double angle, double radius) {
		curve(Direction.BACKWARD, angle, radius, true);
	}
	/**
	 * Create a new command to drive backwards and turn counter-clockwise.  How far?  How fast? How long?
	 * TODO: This needs work.
	 */
	public void addCurveCounterBackwardSync(double angle, double radius) {
		curve(Direction.BACKWARD, angle, radius, false);
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding Elevator commands
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Calibrate the elevator (move down), but don't wait for completion.
	 * To check it you would have to wait for a limit switch. 
	 */
	public void addElevatorCalibrateAsync() {
		addSequential(new MotorCalibrateSensorAsync(Robot.elevator, Direction.DOWN));
	}
	/**
	 * Wait for the Elevator to hit the hard reset limit
	 */
	public void addElevatorWaitForHardLimitSwitchSync() {
		addSequential(new MotorWaitForHardLimitSwitchSync(Robot.elevator, Direction.DOWN));
	}

	/**
	 * Calibrate the elevator (move down) and wait for the limit switch to be reached, so we know that 
	 * our sensor has been set the value of the lower limit (zero).
	 */
	public void addElevatorCalibrateSequenceSync() {
		addElevatorCalibrateAsync();
		addElevatorWaitForHardLimitSwitchSync();
	}

	/**
	 * Move the elevator to the indicated position.  Does not wait for completion.
	 */
	public void addElevatorMoveToPositionAsync(Length position) {
		/**
		 * TODO When Necessary: 
		 * Allow overriding maximum rate for PID move to position, 
		 * go slower when we have a cube in the jaws!
		 */
		addSequential(new MotorMoveToAbsolutePositionAsync(Robot.elevator, position));
	}
	/*
	 * Start the elevator moving in the background
	 */
	public void addElevatorMoveToPlacementHeightAsync(PlacementTargetType target) {
		addElevatorMoveToPositionAsync(getPlacementHeight(target));
	}
	/**
	 * Lower the Elevator to the bottom 
	 */
	public void addElevatorLowerAsync() {
		addElevatorMoveToPositionAsync(LengthUOM.Inches.create(0));
	}
	/**
	 * Wait for the Elevator to reach a target position. 
	 */
	public void addElevatorWaitForTargetPositionSync() {
		// Wait for Elevator to reach it's destination to within +/- one inch.
		addSequential(new MotorWaitForTargetPositionSync(Robot.elevator, LengthUOM.Inches.create(1)));
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding Arm commands
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Calibrate the arm (move down), but don't wait for completion.
	 * To check it you would have to wait for a limit switch. 
	 */
	public void addArmCalibrateAsync() {
		addSequential(new MotorCalibrateSensorAsync(Robot.arm, Direction.IN));
	}
	/**
	 * Wait for the Arm to hit the hard reset limit
	 */
	public void addArmWaitForHardLimitSwitchSync() {
		addSequential(new MotorWaitForHardLimitSwitchSync(Robot.arm, Direction.IN));
	}

	/**
	 * Calibrate the elevator (move down) and wait for the limit switch to be reached, so we know that 
	 * our sensor has been set the value of the lower limit (zero).
	 */
	public void addArmCalibrateSequenceSync() {
		addArmCalibrateAsync();
		addArmWaitForHardLimitSwitchSync();
	}

	/**
	 * Move the arm to the indicated position.  Does not wait for completion.
	 */
	public void addArmMoveToPositionAsync(Length position) {
		addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, position));
	}

	/**
	 * Move the arm In. 
	 */
	public void addArmMoveInAsync() {
		addArmMoveToPositionAsync(LengthUOM.Inches.create(0));
	}

	/**
	 * Wait for the Arm to reach a target position.
	 * TODO: This should be moving to within a tolerance in ArmDegrees, not inches.
	 */
	public void addArmWaitForTargetPositionSync() {
		// Wait for Arm to reach it's destination to within +/- one half inch.
		addSequential(new MotorWaitForTargetPositionSync(Robot.arm, LengthUOM.Inches.create(0.5)));
	}

	/**
	 * Move the arm to the "level" position, but do not wait.
	 */
	public void addArmMoveToLevelPositionAsync() {
		addArmMoveToPositionAsync(ARM_POSITION_FOR_LEVEL);
	}
	
	/**
	 * Move the arm to the "high" position, but do not wait.
	 */
	public void addArmMoveToHighPositionAsync() {
		addArmMoveToPositionAsync(ARM_POSITION_HIGH);
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding Jaws commands
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Add a synchronous command to open the jaws
	 */
	private void addJawsOpenSync() {
		addSequential(new SolenoidSetStateInstant(Robot.jaws, Direction.OPEN));		
	}

	/**
	 * Add a synchronous command to close the jaws
	 */
	private void addJawsCloseSync() {
		addSequential(new SolenoidSetStateInstant(Robot.jaws, Direction.CLOSE));		
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding intake commands
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Add a command to start the intake spinning inwards
	 */
	private void addIntakeInAsync() {
		addSequential(new IntakeSpinAsync(Robot.intake, Direction.IN));		
	}

	/**
	 * Add a command to start the intake spinning outwards
	 */
	private void addIntakeOutAsync() {
		addSequential(new IntakeSpinAsync(Robot.intake, Direction.OUT));		
	}

	/**
	 * Add a command to stop the intake spinning
	 */
	private void addIntakeStopSync() {
		addSequential(new IntakeStopInstant(Robot.intake));		
	}

	/* ------------------------------------------------------------------------------------------------------
	 * Helpers for adding complex command sequences
	 * ------------------------------------------------------------------------------------------------------ */

	/**
	 * Add a delay in seconds
	 */
	public void addDelayInSecondsSync(double seconds) {
		addSequential(new TimedCommand(seconds));
	}

	/**
	 * Add a drop cube sequence.
	 */
	public void addDropCubeSequenceSync() {
		addJawsOpenSync();
		// NB: We expect to have a cube... but since the jaws were open I assume we're trying to shake it loose so go fast...
		addArmMoveToLevelPositionAsync(); 
		addArmWaitForTargetPositionSync();
	}
	
	/**
	 * Add a "shoot" sequence
	 */
	public void addShootCubeSequence() {
		addIntakeOutAsync();
		addJawsOpenSync();
		addIntakeStopSync();
	}
	
	/**
	 * Add a "grab" sequence
	 */
	public void addGrabCubeSequence() {
		addIntakeInAsync();
		addJawsCloseSync();
		addDelayInSecondsSync(0.2);
		addIntakeStopSync();
	}

	/**
	 * Add a "deliver" sequence tailored towards target.
	 */
	public void addDeliverCubeSequenceSync(PlacementTargetType target) {
		/*
		 * NB: We will always 'move to placement height' here even though we have probably optimized
		 * by doing this in advance.  This prevents us from slamming the arm into the field if somehow
		 * we have forgotten that step, and makes this command a little more flexible.
		 */
		addElevatorMoveToPlacementHeightAsync(target);
		/*
		 * Always wait for the target position to be reached before continuing, to avoid slamming into
		 * the field.
		 */
		addElevatorWaitForTargetPositionSync();
		addDriveForwardSync(feet(2), TRANSITION_SPEED_STOP);
		addDropCubeSequenceSync();
		addDriveBackwardSync(feet(2), TRANSITION_SPEED_FLUID);
		addElevatorMoveToPlacementHeightAsync(target);
	}
	
}
