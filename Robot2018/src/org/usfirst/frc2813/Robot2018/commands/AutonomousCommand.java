// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class AutonomousCommand extends CommandGroup {

	private static final SendableChooser<Integer> positionSelector = new SendableChooser<Integer>();
	public static final int FORWARD_SPEED_x_percent_power = -1;
	public static final int BACKWARD_SPEED_x_percent_power = 1;
	public static final int FORWARD_DISTANCE = 1;
	public static final int BACKWARD_DISTANCE = 1;
	public static final double ONE_INCH = 1;			//  The Encoder code in WPI Lib translates the distance to inches based on the
														//  
	public static final double ONE_FOOT = 12 * ONE_INCH;
	
	static {
		positionSelector.addDefault("LEFT", 0);
		positionSelector.addObject("CENTER", 1);
		positionSelector.addObject("RIGHT", 2);
		SmartDashboard.putData("Which position is the robot in?", positionSelector);
	}

	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
	public AutonomousCommand() {
		System.out.println("AutonomousCommand:  Wheel circumference is "+RobotMap.WHEEL_CIRCUMFERENCE);
		// Robot.driveTrain.quadratureEncoder1.reset();
		// Robot.driveTrain.quadratureEncoder2.reset();
		addSequential(new ResetEncoders());
		addSequential(new ResetGyro());

    	/*
    	 * A note on Encoders and the sign of distance:
    	 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
    	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
    	 */
		
		addSequential(new PIDAutoDrive(FORWARD_SPEED_x_percent_power * 0.6, FORWARD_DISTANCE * 15 * ONE_FOOT));				// drive at % of max speed some distance
		addSequential(new PIDAutoDrive(BACKWARD_SPEED_x_percent_power * 0.6, BACKWARD_DISTANCE * 15 * ONE_FOOT));	// drive at % of max speed some distance
		//  This code makes the robot drive backwards, but it doesn't stop...  FLAG  - fix this!
//		addSequential(new PIDAutoDrive(BACKWARD_x_PERCENT_POWER * 0.2, 6 * ONE_FOOT));	// drive back the same distance - are we where we started?
		// addSequential(new
		// PrintOutEncoderValues(60,Robot.driveTrain.quadratureEncoder1,Robot.driveTrain.quadratureEncoder2));
		// addSequential(new AutoTurn(0.1,180));
		/*
		 * From here down is for choosing Auto program There are 10 possibilities
		 */

		final String gameData = DriverStation.getInstance().getGameSpecificMessage();
		int position = positionSelector.getSelected();
		switch (position) {
		case 0://POSITION LEFT
			switch(gameData) {
			case "LLL":
				//L+LLL; go to left switch/scale (?)
				break;
			case "LRL":
				//L+LRL; go to left switch, continue to right scale
				break;
			case "RLR":
				//L+RLR; go to left scale
				break;
			case "RRR":
				//L+RRR; go to right scale
				break;
			}
			break;
		case 1://POSITION CENTER
			switch (gameData.charAt(1)) {
			case 'L':
				//C+L__; go to left switch
				break;
			case 'R':
				//C+R__; go to right switch
				break;
			}
		case 2://POSITION RIGHT
			switch(gameData) {
			case "LLL":
				//R+LLL; go to left scale
				break;
			case "LRL":
				//R+LRL; go to right scale
				break;
			case "RLR":
				//R+RLR; go to right switch, continue to left scale
				break;
			case "RRR":
				//R+RRR; go to right switch/scale (?)
				break;
			}
			break;
		}

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=PARAMETERS
		// Add Commands here:
		// e.g. addSequential(new Command1());
		// addSequential(new Command2());
		// these will run in order.

		// To run multiple commands at the same time,
		// use addParallel()
		// e.g. addParallel(new Command1());
		// addSequential(new Command2());
		// Command1 and Command2 will run in parallel.

		// A command group will require all of the subsystems that each member
		// would require.
		// e.g. if Command1 requires chassis, and Command2 requires arm,
		// a CommandGroup containing them would require both the chassis and the
		// arm.
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS

		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=COMMAND_DECLARATIONS

	}
}
