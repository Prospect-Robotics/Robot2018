package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.SRXElevator;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetSpeed extends InstantCommand {
	double speed;
    public SetSpeed(double speed) {
        super();
        this.speed=speed;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.SRXElevator.setSpeed(speed);
    }

}
