package org.usfirst.frc2813.Robot2018.commands;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ResetEncoderLimitSwitch extends Command {
	private TalonSRX talon;
	private Subsystem subsystem;
    public ResetEncoderLimitSwitch(TalonSRX talon, Subsystem subsystem) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.subsystem=subsystem;
    	this.talon=talon;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (talon.getSensorCollection().isRevLimitSwitchClosed()) {

    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
