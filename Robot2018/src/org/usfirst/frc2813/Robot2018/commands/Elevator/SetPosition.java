package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetPosition extends InstantCommand {
	double position;

    public SetPosition(double position) {
        this.position = position;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.elevator);
    }

    protected void initialize() {
    	Robot.elevator.setPosition(position);
    }
}
