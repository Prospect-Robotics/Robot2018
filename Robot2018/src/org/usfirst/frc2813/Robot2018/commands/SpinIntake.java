package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Spin the intake IN or OUT
 */
public class SpinIntake extends Command {
	private final String name;
	private final Direction direction;
	private double speed;
    public SpinIntake(String name, Direction direction) {
    	this.name = name;
    	this.direction = direction;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.intake);
    	this.speed=1;
    	if (direction != Direction.IN && direction != Direction.OUT && direction != Direction.STOP) {
    		throw new UnsupportedOperationException("Invalid Direction for Intake");
    	}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Logger.printFormat(LogType.INFO,"SpinIntake Set to Move %s at speed %s",direction,(direction == Direction.IN ? -1 : 1));
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (direction == Direction.STOP) speed = 0;
    	Robot.intake.spx.set(direction == Direction.IN ? -speed : speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//Set the speed to 0 when button is released.
    	Logger.info("SpinIntake end.");
    	Robot.intake.spx.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Logger.info("SpinIntake interrupted.");
    	Robot.intake.spx.disable();
    }
    
    public String toString() {
    	return name;
    }
}
