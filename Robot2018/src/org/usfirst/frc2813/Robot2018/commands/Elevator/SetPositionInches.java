package org.usfirst.frc2813.Robot2018.commands.Elevator;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetPositionInches extends InstantCommand {
	double inchesFromBottom;

    public SetPositionInches(double inchesFromBottom) {
        this.inchesFromBottom = inchesFromBottom;
        // Use requires() here to declare subsystem dependencies
        requires(Robot.elevator);
    }

    protected void initialize() {
    	Robot.elevator.setPositionInInches(inchesFromBottom);
    }
}
