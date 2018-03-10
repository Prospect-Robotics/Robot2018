package org.usfirst.frc2813.Robot2018.commands;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *
 */
public class PrintOutEncoderValues extends TimedCommand {
	private Encoder myEncoder1;
	private Encoder myEncoder2;
	private long last;

	public PrintOutEncoderValues(double timeout, Encoder myEncoder1, Encoder myEncoder2) {
		super(timeout);
		requires(Robot.driveTrain);
		this.last = 0;
		this.myEncoder1 = myEncoder1;
		this.myEncoder2=myEncoder2;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		if((System.currentTimeMillis() - last) >= 100) {
			System.out.println(
					"ENCODERSTARBOARD " + myEncoder1.get() + " [" + myEncoder1.getRaw() + "] " +myEncoder1.getDistance()+" "+ myEncoder1.getDistance()*DriveTrain.WHEEL_CIRCUMFERENCE +" ["+myEncoder1.getDistancePerPulse()+"] "+myEncoder1.getDirection()+" ["+myEncoder1.getStopped()+"]\n" +
							"ENCODERPORT " + myEncoder2.get() + " [" + myEncoder2.getRaw() + "]" +myEncoder2.getDistance()+" "+ myEncoder2.getDistance()*DriveTrain.WHEEL_CIRCUMFERENCE+" ["+myEncoder2.getDistancePerPulse()+"] "+myEncoder1.getDirection()+" ["+myEncoder1.getStopped()+"]\n"+
							"DRIVETRAIN" + Robot.driveTrain.getDistance() +"\n"
					);

			this.last = System.currentTimeMillis();
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
