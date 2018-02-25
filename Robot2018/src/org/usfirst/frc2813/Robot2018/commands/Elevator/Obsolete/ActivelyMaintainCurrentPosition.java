package org.usfirst.frc2813.Robot2018.commands.Elevator.Obsolete;

import org.usfirst.frc2813.Robot2018.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class ActivelyMaintainCurrentPosition extends InstantCommand {

    public ActivelyMaintainCurrentPosition() {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	System.out.println("Elevator"+RobotMap.srxElevator.getSelectedSensorPosition(0));
    	RobotMap.srxElevator.set(ControlMode.Position, RobotMap.srxElevator.getSelectedSensorPosition(0));
    }

}
