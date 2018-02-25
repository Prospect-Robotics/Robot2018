package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetPosition extends InstantCommand {
	double inchesFromBottom;
	private static final double PULSES_PER_INCH = RobotMap.SRX_MAG_PULSES_PER_REVOLUTION / RobotMap.ELEVATOR_INCHES_PER_REVOLUTION;
	
    public SetPosition(double inchesFromBottom) {
        super();
        this.inchesFromBottom=inchesFromBottom;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.SRXElevator);
    }

    // Called once when the command executes
    protected void initialize() {
    	RobotMap.srxElevator.set(ControlMode.Position, (int) (inchesFromBottom * PULSES_PER_INCH));
    }

}
