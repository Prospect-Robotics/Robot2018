package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PIDMoveElevator extends Command {
	//private PIDOutput debugPrintPIDOutput;
	private final double fps;
	public final PIDController controller = new PIDController(1.0/500.0, 0, 0, Robot.elevator.encoder, this::debugPrintPIDOutput);	// Kp, Ki, Kd
	public PIDMoveElevator(double feetPerSecond) {
		requires(Robot.elevator);
		fps = feetPerSecond;
	}
	public void debugPrintPIDOutput(double pidOutput) {
		System.out.println("Output updated to: "+pidOutput);
		Robot.elevator.speedController.pidWrite(pidOutput);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		//MaintainElevatorPosition.controller.disable();
		if (Robot.elevator.encoder.getPIDSourceType() != PIDSourceType.kRate) {
			controller.disable();
			controller.reset();
			Robot.elevator.encoder.setPIDSourceType(PIDSourceType.kRate);
		}
		controller.setSetpoint(fps);
		controller.enable();

		System.out.println("MoveElevator:initialize:  rate ("+Robot.elevator.encoder.getPIDSourceType()+") to maintain is: "+fps+" feet per second");

	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
    	System.out.println("MoveElevator:execute:  rate was: "+fps+", getDistance is: "+Robot.elevator.encoder.getDistance());
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
    	System.out.println("MoveElevator:isFinished:  rate was: "+fps+", getDistance is: "+Robot.elevator.encoder.getDistance());
		return false;
	}

	// Called once after isFinished returns true or another command which requires
	// one or more of the same subsystems is scheduled to run
	protected void end() {
		// Should I disable the PID controller here?
		controller.disable();
    	System.out.println("MoveElevator:end:  rate was: "+fps+", getDistance is: "+Robot.elevator.encoder.getDistance());

		// ... didn't think I would have to buuuut
		// Robot.elevator.controller.disable();
	}
}
