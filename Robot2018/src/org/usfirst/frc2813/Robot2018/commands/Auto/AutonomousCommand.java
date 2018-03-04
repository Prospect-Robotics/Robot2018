// AutonomousCommand - a class to run autonomous mode

package org.usfirst.frc2813.Robot2018.commands.Auto;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc2813.Robot2018.Direction;

import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ResetEncoders;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ResetGyro;
import org.usfirst.frc2813.Robot2018.commands.Arm.SpinIntake;

/**
 * Generate a series of sequential commands to operate autonomously. Takes into
 * account game data.
 */
public class AutonomousCommand extends CommandGroup {
	private int directionBias;  // used to share code between left/right
	private static final SendableChooser<Direction> positionSelector = new SendableChooser<Direction>();
	static {
		positionSelector.addDefault("LEFT", Direction.LEFT);
		positionSelector.addObject("CENTER", Direction.CENTER);
		positionSelector.addObject("RIGHT", Direction.RIGHT);
		SmartDashboard.putData("Which position is the robot in?", positionSelector);
	}
	/**
	 * Code to be run during the Autonomous 15 second period. 
	 * This code uses the gameData from the driver station and a 
	 * sendable chooser on the Smart Dashboard to decide which
	 * sequence to run.
	 */
	public AutonomousCommand() {
		/**
		 * The game data from the driver station:
		 * Which side of the near switch, scale, and far switch we have.
		 * Data is passed as a single string with each piece as a 'L' or 'R'.
		 */
		class GameData {
			private Direction nearSwitch, scale, farSwitch;
			private Direction splitChar(char c) {
				return (c == 'L') ? Direction.LEFT : Direction.RIGHT;
			}
			GameData(String gd) {
				nearSwitch = splitChar(gd.charAt(0));
				scale = splitChar(gd.charAt(1));
				farSwitch = splitChar(gd.charAt(2));
			}
			public Direction getNearSwitch() { return nearSwitch; }
			public Direction getScale() { return scale; }
			public Direction getFarSwitch() { return farSwitch; }
		}

		/**
		 * AutoCmd - generate the autonomous command sequence
		 * constructor sets encoders and gyro.
		 * speed and direction are stateful.
		 */
		class AutoCmd {
			private static final int FORWARD = -1;
			private static final int BACKWARD = 1;
			private double driveSpeed = 1;
			private double turnSpeed = 0.25;
			private double curveSpeed = 0.4;
			private static final int MAX_ELEVATOR = 0;//TODO replace with correct value

			public AutoCmd() {
				addSequential(new ResetEncoders());
				addSequential(new ResetGyro());
			}
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

			//elevator commands
			public void raiseElevator(double amount) {
				//TODO create method //addSequential();
			}
			public void raiseElevator() { raiseElevator(MAX_ELEVATOR); }
			public void lowerElevator(double amount) { raiseElevator(-amount); }
			public void lowerElevator() { raiseElevator(-MAX_ELEVATOR); }

			// arm control commands
			public void dropCube() {
				// TODO: add command to close arms
				addSequential(new SpinIntake(Direction.OUT, true));
			}
			public void grabCube() {
				// TODO: add command to open arms
				addSequential(new SpinIntake(Direction.IN, false));
			}

			public void sleep(double seconds) {
				addSequential(new TimedCommand(seconds));
			}
		}

		// Generate a command list into which we will place autonomous commands
		AutoCmd autoCmdList = new AutoCmd();

		// Read the state of the field pieces
		GameData gameData = new GameData(DriverStation.getInstance().getGameSpecificMessage());

		// Read our location on the field
		Direction position = positionSelector.getSelected();

		 // allows left->right and right->left to share code
		directionBias = (position == Direction.LEFT) ? 1 : -1;

		if (position == gameData.getScale()) {
			// we are on the same side as the scale. Leave switch for team mates
			autoCmdList.driveForward(150);
			autoCmdList.turnRight(90 * directionBias);
			autoCmdList.raiseElevator();
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();
		}
		else if (position != Direction.CENTER) {
			// from far side we cross over between switch and scale and place block on scale
			autoCmdList.driveForward(50);
			autoCmdList.turnRight(45 * directionBias);
			autoCmdList.driveForward(50); // diagonally across field
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.raiseElevator();
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();			
		}
		else {
			// We are in the center start position
			 // allows left->right and right->left to share code
			directionBias = (position == Direction.LEFT) ? 1 : -1;

			autoCmdList.driveForward(10); // enough to turn
			autoCmdList.turnLeft(45 * directionBias);
			autoCmdList.driveForward(40); // diagonally from start to far side of near switch
			autoCmdList.turnRight(45);
			autoCmdList.raiseElevator(AutoCmd.MAX_ELEVATOR / 2);
			autoCmdList.dropCube();
			autoCmdList.lowerElevator();			
		}
	}
}