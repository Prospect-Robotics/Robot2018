package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetSpeed extends Command {
	double speed;
	
	private static final double PULSES_PER_INCH = RobotMap.SRX_MAG_PULSES_PER_REVOLUTION / RobotMap.ELEVATOR_INCHES_PER_REVOLUTION;

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
    	// set() accepts a velocity in position change per 100ms.  Divide by 10 to convert position change per 1000ms to that.
    	RobotMap.srxElevator.set(ControlMode.Velocity, (speed * 12.0 * PULSES_PER_INCH) / 10.0);
    }
	@Override
	protected boolean isFinished() {
		return false;
	}
	protected void end() {
		System.out.println("SetSpeed End" + RobotMap.srxElevator.getSelectedSensorPosition(0) + " Speed " + speed);
	}

}
