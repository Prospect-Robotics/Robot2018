package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.SpinIntake;
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
	private final double driveSpeed = 1;
	private final double turnSpeed = 0.25;
	private final double curveSpeed = 0.4;

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

	/**
	 * A note on Encoders and the sign of distance:
	 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS")
	 */
	private void drive(Length distance, Direction direction) {
		double motorSpeed = direction == Direction.FORWARD ? driveSpeed : -driveSpeed;
		addSequential(new PIDAutoDrive(motorSpeed, distance.convertTo(LengthUOM.Inches).getValue()));
	}
	public void driveForward(Length distance) {
		drive(distance, Direction.FORWARD);
	}
	public void driveBackward(Length distance) {
		drive(distance, Direction.BACKWARD);
	}

	public void turnLeft(double angle) {
		addSequential(new AutoTurn(turnSpeed, angle));
	}
	public void turnLeft() { turnLeft(90); } // Default turns are 90 degree
	public void turnRight(double angle) { turnLeft(-angle); } // right turn is a negative left turn
	public void turnRight() { turnLeft(-90); }

	/*
	 * We do not currently use curve drive in autonomous
	 * */
	public void curveCounterForward(double angle, double radius) {
		addSequential(new AutoCurveDrive(-curveSpeed , -angle, radius));
	}
	public void curveClockForward(double angle, double radius) {
		addSequential(new AutoCurveDrive(curveSpeed, angle, -radius));
	}
	public void curveCounterBackward(double angle, double radius) {
		addSequential(new AutoCurveDrive(curveSpeed, -angle, radius));
	}
	public void curveClockBackward(double angle, double radius) {
		addSequential(new AutoCurveDrive(-curveSpeed, angle, -radius));
	}

	//elevator commands - FIXME! these commands return before they
	//reach the desired elevator position
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
		addSequential(new SpinIntake("SpinIntake DOWN", Direction.OUT));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.OPEN));
		addSequential(new SpinIntake("SpinIntake UP", Direction.STOP));
	}
	public void grabCube() {
		addSequential(new SpinIntake("SpinIntake(IN)", Direction.IN));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.CLOSE));
		sleep(0.2);
		addSequential(new SpinIntake("SpinIntake(STOP)", Direction.STOP));
	}

	public void sleep(double seconds) {
		addSequential(new TimedCommand(seconds));
	}
}
