package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.Arm.SpinIntake;
import org.usfirst.frc2813.Robot2018.commands.Auto.AutoTurn;
//import org.usfirst.frc2813.Robot2018.commands.Auto.AutoCurveDrive;
import org.usfirst.frc2813.Robot2018.commands.Auto.PIDAutoDrive;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ResetEncoders;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ResetGyro;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToPosition;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetState;
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
	private static final int FORWARD = -1;
	private static final int BACKWARD = 1;
	private double driveSpeed = 1;
	private double turnSpeed = 0.25;
	//private double curveSpeed = 0.4;

	public AutonomousCommandGroup() {
		addSequential(new ResetEncoders());
		addSequential(new ResetGyro());
	}

	// track state
	public void setDriveSpeed(double speed) { driveSpeed=speed; }
	public void setTurnSpeed(double speed) { turnSpeed=speed; }

	/*
	 * A note on Encoders and the sign of distance:
	 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
	 */
	public void driveForward(double distance) {
		addSequential(new PIDAutoDrive(FORWARD*driveSpeed, distance));
	}
	public void driveBackward(double distance) {
		addSequential(new PIDAutoDrive(BACKWARD*driveSpeed, distance));
	}

	public void turnLeft(double angle) {
		addSequential(new AutoTurn(turnSpeed, angle));
	}
	public void turnLeft() { turnLeft(90); } // Default turns are 90 degree
	public void turnRight(double angle) { turnLeft(-angle); } // right turn is a negative left turn
	public void turnRight() { turnLeft(-90); }

	/*
	 * We do not currently use curve drive in autonomous
	public void curveCounterForward(double angle, double radius) {
		addSequential(new AutoCurveDrive(-curveSpeed, -angle, radius));
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
	*/

	//elevator commands - FIXME! these commands return before they
	//reach the desired elevator position
	public void elevatorMoveToPosition(Length position) {
		addSequential(new MotorMoveToPosition(Robot.elevator, position));
	}
	public void lowerElevator() {
		elevatorMoveToPosition(LengthUOM.Inches.create(0));
	}

	// arm control commands
	public void dropCube() {
		// TODO: should we delay between these?
		// TODO: consider making this sequence a command
		addSequential(new SpinIntake(Direction.OUT));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.OPEN));
		addSequential(new SpinIntake());
	}
	public void grabCube() {
		// TODO: should we delay between these? Change order?
		// TODO: consider making this pair a command for arm
		addSequential(new SpinIntake(Direction.IN));
		addSequential(new SolenoidSetState(Robot.jaws, Direction.CLOSE));
	}

	public void sleep(double seconds) {
		addSequential(new TimedCommand(seconds));
	}
}
