package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.RandomPIDController2;
import org.usfirst.frc2813.WatchdogPIDInterface;
import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDAutoDrive extends Command {
	// divide Ki and multiply Kd by 0.05 to emulate the behavior of a normal PIDController which uses a fixed 0.05 second period.
	private final RandomPIDController2 controller = new RandomPIDController2(0.3, 0 / .05, 0.0005, Robot.gyro, this::usePIDOutput);
	//private final PIDController controller = new PIDController(0.15, 0, 0.2);
	//private final WatchdogPIDInterface watchdog = new WatchdogPIDInterface(controller);
	private final double forwardSpeed;
	private final double distance;
	private double stopAt;
	
    public PIDAutoDrive(double forwardSpeed, double distance) {
        requires(Robot.driveTrain);
        controller.setInputRange(0, 360);
        controller.setContinuous();
        controller.setOutputRange(-1, 1);
   //     watchdog.setSafetyEnabled(true);
   //     watchdog.setExpiration(0.1);
        this.forwardSpeed=forwardSpeed;
        this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	stopAt = Robot.driveTrain.getDistance() + distance;
    	controller.enable();
    	System.out.println("Started");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
//    	watchdog.feed();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        double distanceRemaining = stopAt - Robot.driveTrain.getDistance();
        if(distance < 0)
        	distanceRemaining *= -1;
        System.out.println("Distance remaining: "+distanceRemaining);
        return distanceRemaining <= 0;
    }

    // Called once after isFinished returns true
    protected void end() {
    	System.out.println("Stopping...");
    	controller.disable();
    	controller.reset();
    }
    
    private void usePIDOutput(double output) {
    	/*
    	 * PID controllers write their output to zero when disabled.
    	 * The watchdog will call disable() every 0.1 seconds when
    	 * the command isn't running.  Every time the output is written
    	 * we cause the robot to move.  If the watchdog isn't alive,
    	 * don't accept input.
    	 */
//    	if(!watchdog.isAlive()) return;
    	System.out.println("Output updated to: "+output);//+", Time since last run: "+controller.getTimeDelta());
    	Robot.driveTrain.arcadeDrive(forwardSpeed, -output);
    }
}
