package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.subsystems.SRXElevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetSpeed extends Command {
	double speed;
    public SetSpeed(double speed) {
        this.speed=speed;
        requires(Robot.SRXElevator);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	RobotMap.srxElevator.selectProfileSlot(1, Constants.movePIDLoopIdx);
    }
    protected void execute() {
    	System.out.println("SetSpeed Execute" + RobotMap.srxElevator.getSelectedSensorPosition(0)+" Speed " + speed);
    	Robot.SRXElevator.setSpeed(speed);
    }
	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return false;
	}
	protected void end() {
		System.out.println("SetSpeed End" + RobotMap.srxElevator.getSelectedSensorPosition(0) + " Speed " + speed);
		//Robot.SRXElevator.setSpeed(0);
		//new MaintainElevatorPosition().start();
	}

}
