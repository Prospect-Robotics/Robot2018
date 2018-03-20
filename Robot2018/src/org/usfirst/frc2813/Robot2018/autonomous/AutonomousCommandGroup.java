package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.ToggleIntake;
import org.usfirst.frc2813.Robot2018.commands.auto.AutoCurveDrive;
import org.usfirst.frc2813.Robot2018.commands.auto.AutoTurn;
import org.usfirst.frc2813.Robot2018.commands.auto.PIDAutoDrive;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.ResetEncoders;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.ResetGyro;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePosition;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForTargetPosition;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetState;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;


/**
 * AutoCmd - generate the autonomous command sequence. Note that this class constructs
 * the command list and provides methods to add new commands and then calls a generator
 * to generate the command list based on field setup.
 *
 * speed and direction are stateful.
 */
public class AutonomousCommandGroup extends CommandGroup {
	private double driveSpeed = 1;
	private double turnSpeed = 0.25;
	private double curveSpeed = 0.4;

	private double currentSpeed = 0.0;

	// FIXME! what until type? What values should these be?
	private static final Length armPositionLevel = LengthUOM.Inches.create(12);
	private static final Length armPositionHigh = LengthUOM.Inches.create(20);

	public AutonomousCommandGroup() {
		Logger.info("Autonomous: adding reset commands");
		addSequential(new ResetEncoders());
		addSequential(new ResetGyro());
		Logger.info("Autonomous: reset commands added");
	}

	// track state
	public void setDriveSpeed(double speed) { driveSpeed=speed; }
	public void setTurnSpeed(double speed) { turnSpeed=speed; }
	public void setCurveSpeed(double speed) { curveSpeed=speed; }

	private void drive(Length distance, Direction direction, double endSpeed) {
		addSequential(new PIDAutoDrive(driveSpeed, direction, distance.convertTo(LengthUOM.Inches).getValue(), currentSpeed, endSpeed));
		currentSpeed = endSpeed;
	}
	public void driveForward(Length distance, double endSpeed) {
		drive(distance, Direction.FORWARD, endSpeed);
	}
	public void driveBackward(Length distance, double endSpeed) {
		drive(distance, Direction.BACKWARD, endSpeed);
	}

	public void turnLeft(double angle) {
		addSequential(new AutoTurn(turnSpeed, angle));
	}
	public void turnLeft() { turnLeft(90); } // Default turns are 90 degree
	public void turnRight(double angle) { turnLeft(-angle); } // right turn is a negative left turn
	public void turnRight() { turnLeft(-90); }

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
		addSequential(new AutoCurveDrive(speed, angle, radius));		
	}
	public void curveClockForward(double angle, double radius) {
		curve(Direction.FORWARD, angle, radius, true);
	}
	public void curveCounterForward(double angle, double radius) {
		curve(Direction.FORWARD, angle, radius, false);
	}
	public void curveClockBackward(double angle, double radius) {
		curve(Direction.BACKWARD, angle, radius, true);
	}
	public void curveCounterBackward(double angle, double radius) {
		curve(Direction.BACKWARD, angle, radius, false);
	}

	/**
	 * elevator commands. Note that the move commands are instant commands.
	 * Follow up with with waitForElevator if you're not sure the elevator
	 * has arrived.
	 * @param position
	 */
	public void elevatorMoveToPosition(Length position) {
		addSequential(new MotorMoveToAbsolutePosition(Robot.elevator, position));
	}
	public void lowerElevator() {
		elevatorMoveToPosition(LengthUOM.Inches.create(0));
	}
	public void armMoveToPosition(Length position) {
		addSequential(new MotorMoveToAbsolutePosition(Robot.arm, position));
	}
	public void waitForElevator() {
		// Wait for Elevator to reach it's destiniation to within +/- one inch.
		addSequential(new MotorWaitForTargetPosition(Robot.elevator, LengthUOM.Inches.create(1)));
	}

	// arm control commands
	public void levelArm() {
		addSequential(new MotorMoveToAbsolutePosition(Robot.arm, armPositionLevel));
	}
	public void raiseArm() {
		addSequential(new MotorMoveToAbsolutePosition(Robot.arm, armPositionHigh));
	}
	private void openJaws() {
		addSequential(new SolenoidSetState(Robot.jaws, Direction.OPEN));		
	}
	public void dropCube() {
		openJaws();
		levelArm();
	}
	public void shootCube() {
		addSequential(new ToggleIntake(Direction.OUT));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.OPEN));
		addSequential(new ToggleIntake(Direction.STOP));
	}
	public void grabCube() {
		addSequential(new ToggleIntake(Direction.IN));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.CLOSE));
		sleep(0.2);
		addSequential(new ToggleIntake(Direction.STOP));
	}

	public void sleep(double seconds) {
		addSequential(new TimedCommand(seconds));
	}
}
