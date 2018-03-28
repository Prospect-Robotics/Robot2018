package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroupGenerator;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.AutoDriveSync;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainQuickTurnSync;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainResetEncodersInstant;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainResetGyroInstant;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainStop;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSpinAsync;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeStopInstant;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorCalibrateSensorAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePositionAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForHardLimitSwitchSync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForTargetPositionSync;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetStateInstant;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.logging.LogType;
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
 * {@literal add<device><action><Async|Sync>}
 *
 * Naming Conventions for adding command sequences:
 *
 * {@literal add<Goal>Sequence<Sync|Async>}
 *
 *     where *Async means that the command doesn't finish what it started and you better make sure it's
 *     done later in your script.
 *     where *Sync means the command is complete before it returns.
 *
 */
public class AutonomousCommandGroup extends CommandGroup {
	public Drive drive;
	public Cube cube;
	public Arm arm;
	public Elevator elevator;
	public Time time;

	public AutonomousCommandGroup() {
		drive = new Drive();
		cube = new Cube();
		arm = new Arm();
		elevator = new Elevator();
		time = new Time();
	}

	/** global routine to reset and calibrate the robot systems. MUST BE CALLED FIRST! */
	public void resetAndCalibrateSync() {
		/**  First, reset the gyro and the wheel encoders. */
		drive.addSensorResetSequenceSync();

		/**
		 * Next, return the elevator and the arm to the home position.  In competition, we will start in this
		 * position and the auto-reset logic will calibrate them automatically - making this step instant and
		 * unnecessary.  However, if we are testing, we want to make sure that we home the arm and the
		 * elevator and get those counters reset or we may damage the robot.  So keeping these commands in
		 * the auto script is a robot-safety critical feature.
		 */
		/** WARNING! MUST CALIBRATE ARM BEFORE ELEVATOR */
		arm.addCalibrateSequenceSync();
		elevator.addCalibrateSequenceSync();
	}

	public class Drive {

		/**
		 * This is the sticky setting for the last speed as we came out of a move.
		 * TODO: This is not used.
		 */
		private double currentSpeed = 0.0;
		/**
		 * This is the sticky setting to be used for all subsequent commands created for driving in an arc.
		 */
		private double curveSpeed = 0.4;

		/**
		 * This is the sticky setting to be used for all subsequent commands created for driving forwards.
		 */
		private double driveSpeed = 1;
		/**
		 * This is the sticky setting to be used for all subsequent commands created for turning in place.
		 */
		private double turnSpeed = 0.25;

		/**
		 * Track the current speed sent by use. If 0, we need to come to a complete stop before anything
		 * else can happen.
		 * @param speed
		 */
		private void trackSpeed(double speed) {
			currentSpeed = speed;
			if (speed == 0.0) {
				addSequential(new DriveTrainStop(Robot.driveTrain));
			}
		}
		/**
		 * Set the sticky setting for the speed for taking curves.  This value will be used for all subsequent commands created for curved movement.
		 * @param curveSpeed Percentage of output power {-1.0..1.0}
		 */
		public void setCurveSpeed(double curveSpeed) {
			this.curveSpeed = curveSpeed;
		}

		/**
		 * Set the sticky setting for the speed for drive speed.  This value will be used for all subsequent commands created for driving forwards.
		 * @param driveSpeed Percentage of output power {-1.0..1.0}
		 */
		public void setDriveSpeed(double driveSpeed) {
			this.driveSpeed = driveSpeed;
		}

		/**
		 * Set the sticky setting for the speed for turning.  This value will be used for all subsequent commands created for turning.
		 * @param turnSpeed Percentage of output power {-1.0..1.0}
		 */
		public void setTurnSpeed(double turnSpeed) {
			this.turnSpeed = turnSpeed;
		}

		/** Scale the acceleration/deceleration in the auto drive system. THIS IS GLOBAL AND INSTANT! */
		public void setRampSpeed(double rampSpeed) {
			AutoDriveSync.scaleRamps(rampSpeed);
		}

		/**
		 * Add a command for driving on a circular path for a set number of degrees, with a desired speed at the end of the movement.
		 * @param direction - forward or backward
		 * @param degrees - along the curve.
		 * @param radius - radius of circular path
		 * @param rotation - clockwise or counterclockwise
		 * @param endSpeed - speed coming out this command
		 */
		public void addCurveDegreesSync(Direction direction, double degrees, Length radius, Direction rotation, double endSpeed) {
			addCurveSync(direction, radius.multiply(2*Math.PI*degrees/360), radius, rotation, endSpeed);
		}

		/**
		 * Add a command for driving on a circular path for a set distance, with a desired speed at the end of the movement.
		 * @param direction - forward or backward
		 * @param distance - along the curve.
		 * @param radius - radius of circular path
		 * @param rotation - clockwise or counterclockwise
		 * @param endSpeed - speed coming out this command
		 */
		public void addCurveSync(Direction direction, Length distance, Length radius, Direction rotation, double endSpeed) {
			Logger.printLabelled(LogType.DEBUG, "AUTO ADD CURVE", "direction", direction, "distance", distance, "radius", radius, "rotation", rotation, "endSpeed", endSpeed);
			addSequential(new AutoDriveSync(Robot.driveTrain, driveSpeed, direction, distance.convertTo(LengthUOM.Inches).getValue(), currentSpeed,
					endSpeed, radius.convertTo(LengthUOM.Inches).getValue(), rotation == Direction.CLOCKWISE));
			trackSpeed(endSpeed);
		}

		/**
		 * Add a command for driving forward for a set distance, with a desired speed at the end of the movement.
		 * @param direction The direction to drive
		 * @param distance The distance to drive
		 * @param endSpeed The end speed as a percentage of output.  Range is {-1.0..1.0}.
		 */
		public void addDriveSync(Direction direction, Length distance, double endSpeed) {
			addSequential(new AutoDriveSync(Robot.driveTrain, driveSpeed, direction, distance.convertTo(LengthUOM.Inches).getValue(), currentSpeed, endSpeed));
			trackSpeed(endSpeed);
		}

		/** Add commands to reset the drive train encoders and gyros (typically called at the start of a match) */
		public void addSensorResetSequenceSync() {
			addSequential(new DriveTrainResetEncodersInstant(Robot.driveTrain));
			addSequential(new DriveTrainResetGyroInstant(Robot.driveTrain));
		}

		/**
		 * Create a command to spin in place, until we reach a specific *relative* angle.  Will turn in either direction
		 * until that relative angle is hit, accounting for any overshoot by reversing direction for smaller and smaller
		 * moves until the target is it.  Right now QuickTurnCommand doesn't have PID, but continues until it gets close
		 * enough.
		 * @param direction - left or right
		 * @param relativeAngle - how many degrees to turn
		 */
		public void addQuickTurnSync(Direction direction, double relativeAngle) {
			addSequential(new DriveTrainQuickTurnSync(Robot.driveTrain, direction, relativeAngle, turnSpeed));
		}
	}

	public class Elevator {
		/** Calibrate the elevator (move down), but don't wait for completion. */
		private void addCalibrateAsync() {
			if(!Robot.elevator.isDisconnected()) {
				addSequential(new MotorCalibrateSensorAsync(Robot.elevator, Direction.DOWN));
			}
		}
		/**
		 * Calibrate the elevator (move down) and wait for the limit switch to be reached, so we know that
		 * our sensor has been set the value of the lower limit (zero).
		 */
		private void addCalibrateSequenceSync() {
			addCalibrateAsync();
			addWaitForHardLimitSwitchSync();
		}
		/** Lower the Elevator to the bottom  */
		public void addLowerAsync() {
			addMoveToPositionAsync(LengthUOM.Inches.create(0));
		}
		/**
		 * Move the elevator to the indicated position.  Does not wait for completion.
		 * @param position The absolute position to move the elevator to, relative to the lower limit switch.
		 */
		public void addMoveToPositionAsync(Length position) {
			/**
			 * TODO When Necessary:
			 * Allow overriding maximum rate for PID move to position,
			 * go slower when we have a cube in the jaws!
			 */
			if(!Robot.elevator.isDisconnected()) {
				addSequential(new MotorMoveToAbsolutePositionAsync(Robot.elevator, position));
			}
		}
		/** Wait for the Elevator to hit the hard reset limit*/
		public void addWaitForHardLimitSwitchSync() {
			if(!Robot.elevator.isDisconnected())
				addSequential(new MotorWaitForHardLimitSwitchSync(Robot.elevator, Direction.DOWN));
		}
		/** Wait for the Elevator to reach a target position. */
		public void addWaitForTargetPositionSync() {
			// Wait for Elevator to reach it's destination to within +/- one inch.
			if(!Robot.elevator.isDisconnected())
				addSequential(new MotorWaitForTargetPositionSync(Robot.elevator, LengthUOM.Inches.create(1)));
		}
	}

	public class Arm {

		/**
		 * Calibrate the arm (move down), but don't wait for completion.
		 * To check it you would have to wait for a limit switch.
		 */
		private void addCalibrateAsync() {
			if(!Robot.arm.isDisconnected()) {
				addSequential(new MotorCalibrateSensorAsync(Robot.arm, Direction.IN));
			}
		}
		/**
		 * Calibrate the arm (move down) and wait for the limit switch to be reached, so we know that
		 * our sensor has been set the value of the lower limit (zero).
		 */
		private void addCalibrateSequenceSync() {
			addCalibrateAsync();
			addWaitForHardLimitSwitchSync();
		}
		/** Move the arm In to the home position */
		public void addMoveInAsync() {
			addMoveToPositionAsync(ArmConfiguration.ArmDegrees.create(0));
		}

		/**
		 * Move the arm to the indicated position.  Does not wait for completion.
		 * @param armDegrees The number of ARM degrees.
		 * @see ArmConfiguration#ArmDegrees
		 */
		public void addMoveToPositionAsync(Length armDegrees) {
			if(!Robot.arm.isDisconnected())
				addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, armDegrees));
		}

		/** Wait for the Arm to hit the hard reset limit */
		public void addWaitForHardLimitSwitchSync() {
			if(!Robot.arm.isDisconnected())
				addSequential(new MotorWaitForHardLimitSwitchSync(Robot.arm, Direction.IN));
		}

		/** Wait for the Arm to get very close to a target position. */
		public void addWaitForTargetPositionSync() {
			if(!Robot.arm.isDisconnected()) {
				addSequential(new MotorWaitForTargetPositionSync(Robot.arm, ArmConfiguration.ArmDegrees.create(5.0)));
			}
		}
	}

	public class Cube {
		/**
		 * Keep track of whether we think we should have a cube at this point in the sequence, so we can
		 * scale back movements if necessary.
		 */
		private boolean haveCube = true;

		/**
		 * Add a "deliver" sequence tailored towards target. Wait for elevator. Deliver cube. Lower elevator.
		 * @param target where we want to place the cube
		 * @param returnToPlacementHeightAsync do we want to go back to placement height afterwards?
		 */
		public void addDeliverSequenceSync() {
			elevator.addWaitForTargetPositionSync();
			addShootSequenceSync();
		}

// We do not currently do any cube dropping
//		/** Add a drop cube sequence. */
//		private void addDropSequenceSync() {
//			addJawsOpenSync();
//			// NB: We expect to have a cube... but since the jaws were open I assume we're trying to shake it loose so go fast...
//			arm.addMoveToLevelPositionAsync();
//			arm.addWaitForTargetPositionSync();
//		}

		/** Add a "grab" cube sequence */
		private void addGrabSequenceSync() {
			addIntakeInAsync();
			addJawsCloseSync();
			time.addDelayInSecondsSync(0.2);
			addIntakeStopSync();
		}

		/** Add a command to start the intake spinning inwards */
		private void addIntakeInAsync() {
			addSequential(new IntakeSpinAsync(Robot.intake, Direction.IN));
		}

		/** Add a command to start the intake spinning outwards */
		private void addIntakeOutAsync() {
			addSequential(new IntakeSpinAsync(Robot.intake, Direction.OUT));
		}

		/** Add a command to stop the intake spinning */
		private void addIntakeStopSync() {
			addSequential(new IntakeStopInstant(Robot.intake));
		}

		/** Add a synchronous command to close the jaws */
		private void addJawsCloseSync() {
			addSequential(new SolenoidSetStateInstant(Robot.jaws, Direction.CLOSE));
		}

		/** Add a synchronous command to open the jaws */
		private void addJawsOpenSync() {
			addSequential(new SolenoidSetStateInstant(Robot.jaws, Direction.OPEN));
		}

		/** Add a "shoot" cube sequence. */
		public void addShootSequenceSync() {
			addIntakeOutAsync();
			time.addDelayInSecondsSync(0.2);
			addIntakeStopSync();
		}

		/**
		 * Set the sticky hint about whether we have a cube or not, which can help us scale back our speed to keep it.
		 * @param haveCube true/false
		 */
		public void setHaveCube(boolean haveCube) {
			this.haveCube = haveCube;
		}
	}

	public class Time {
		/** Add a delay in seconds. */
		private void addDelayInSecondsSync(double seconds) {
			addSequential(new TimedCommand(seconds));
		}
	}
}
