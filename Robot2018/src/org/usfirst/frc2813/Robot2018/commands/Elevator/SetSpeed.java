package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetSpeed extends Command {
	double speed;
	/**
	 * Set the speed of the elevator
	 * @param speed in feet per second
	 */
    public SetSpeed(double speed) {
        this.speed=speed;
        requires(Robot.SRXElevator);
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
		return false;
	}
	protected void end() {
		System.out.println("SetSpeed End" + RobotMap.srxElevator.getSelectedSensorPosition(0) + " Speed " + speed);
	}

}
