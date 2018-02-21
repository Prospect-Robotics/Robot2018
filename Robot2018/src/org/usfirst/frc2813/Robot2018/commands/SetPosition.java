package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetPosition extends InstantCommand {
	double inchesFromBottom;
    public SetPosition(double inchesFromBottom) {
        super();
        this.inchesFromBottom=inchesFromBottom;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.SRXElevator.setPosition(inchesFromBottom);
    }

}
