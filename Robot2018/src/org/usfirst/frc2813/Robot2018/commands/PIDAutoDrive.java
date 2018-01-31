package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.WatchdogPIDController;
import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDAutoDrive extends Command {
	
	private final WatchdogPIDController controller = new WatchdogPIDController(1, 0, 0, Robot.gyro, this::usePIDOutput);
	private final double forwardSpeed;
	private final double distance;
	private double stopAt;
	
    public PIDAutoDrive(double forwardSpeed, double distance) {
        requires(Robot.driveTrain);
        controller.setInputRange(0, 360);
        controller.setContinuous();
        controller.setOutputRange(-1, 1);
        this.forwardSpeed=forwardSpeed;
        this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	stopAt = Robot.driveTrain.quadratureEncoder1.getDistance() + distance;
    	controller.enable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.driveTrain.quadratureEncoder1.getDistance() >= stopAt;
    }

    // Called once after isFinished returns true
    protected void end() {
    	controller.disable();
    }
    
    private void usePIDOutput(double output) {
    	Robot.driveTrain.arcadeDrive(forwardSpeed, output);
    }
}
