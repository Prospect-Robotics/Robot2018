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
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc2813.Robot2018.Robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/*
 * TODO: THIS CLASS TO BE REMOVED
 */
public class MoveElevator extends Command {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_DECLARATIONS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
	private Encoder quadratureEncoder1;
	private DigitalInput digitalInput1;
	private TalonSRX speedController1;
	private SpeedController speedController2;
	private static final double DESIRED_ENCODER_VALUE = 10;//the desired encoder value to stop at top TODO replace with desired encoder value
	private boolean direction;
	private boolean stop;
    public MoveElevator(boolean upDown, boolean stop) {//if moving up, true; down, false
    	direction=upDown;
    	this.stop=stop;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTOR
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=VARIABLE_SETTING
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
        requires(Robot.SRXElevator);

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=REQUIRES
    }

    // Called just before this Command runs the first time
    //@Override
    protected void initialize() {
    //	quadratureEncoder1 = Robot.SRXElevator.encoder;
    	//digitalInput1 = Robot.SRXElevator.limitSwitch;
    	//speedController1 = Robot.elevator.speedController;
    }

    // Called repeatedly when this Command is scheduled to run
    //@Override
    protected void execute() {
    	if(direction == true && stop == false) {
    		//speedController1.set(.5);//TODO not sure if 1 or -1 is up
    		speedController2.set(-.5);
    	}
    	else if (direction == false && stop == false) {
    		//speedController1.set(-.5);//TODO not sure if -1 or 1 is down
    		speedController2.set(.5);
    	}
    	else if (stop == true) {
    		//speedController1.set(0);
    		speedController2.set(0);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    //@Override
    protected boolean isFinished() {
    	
        return false;
    }

    // Called once after isFinished returns true
    @Override
    protected void end() {
    	System.out.println("END");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    @Override
    protected void interrupted() {
    }
}
