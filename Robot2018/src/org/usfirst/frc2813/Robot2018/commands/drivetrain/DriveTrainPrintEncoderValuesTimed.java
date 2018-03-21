package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 * Print out the drive train encoder values to the log.
 */
public final class DriveTrainPrintEncoderValuesTimed extends AbstractDriveTrainTimedCommand {
	private long last;
	private double timeout;
	private final Encoder starboardEncoder;
	private final Encoder portEncoder;

	public DriveTrainPrintEncoderValuesTimed(DriveTrain driveTrain, double timeout) {
		super(driveTrain, false /* do not require subsystem for background monitoring command */, timeout);
		this.last = 0;
		this.timeout = timeout;
		this.portEncoder = driveTrain.getEncoderPort();
		this.starboardEncoder = driveTrain.getEncoderStarboard();
		setName(toString());
		setRunWhenDisabled(true);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		if((System.currentTimeMillis() - last) >= 100) {
			System.out.println(
					"starboardEncoder " + starboardEncoder.get() + " [" + starboardEncoder.getRaw() + "] " +starboardEncoder.getDistance()+" "+ starboardEncoder.getDistance()*DriveTrain.WHEEL_CIRCUMFERENCE_INCHES +" ["+starboardEncoder.getDistancePerPulse()+"] "+starboardEncoder.getDirection()+" ["+starboardEncoder.getStopped()+"]\n" +
							"ENCODERPORT " + portEncoder.get() + " [" + portEncoder.getRaw() + "]" +portEncoder.getDistance()+" "+ portEncoder.getDistance()*DriveTrain.WHEEL_CIRCUMFERENCE_INCHES+" ["+portEncoder.getDistancePerPulse()+"] "+portEncoder.getDirection()+" ["+portEncoder.getStopped()+"]\n"+
							"DRIVETRAIN" + Robot.driveTrain.getDistance() +"\n"
					);
			this.last = System.currentTimeMillis();
		}
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ", timeout=" + timeout + ")"; 
    }
}
