package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Print out the drive train encoder values to the log.
 */
public final class DriveTrainPrintEncoderValues extends SubsystemCommand<DriveTrain> {
	private long last;
	private double timeout;
	private final Encoder starboardEncoder;
	private final Encoder portEncoder;

	public DriveTrainPrintEncoderValues(DriveTrain driveTrain) {
		super(driveTrain, CommandDuration.DISABLED, Lockout.Disabled);
		this.last = 0;
		this.timeout = timeout;
		this.portEncoder = driveTrain.getEncoderPort();
		this.starboardEncoder = driveTrain.getEncoderStarboard();
		setName(toString());
		setRunWhenDisabled(true);
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void ghscExecute() {
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
	protected boolean ghscIsFinished() {
		return false;
	}
	
	@Override
	public boolean ghscIsSubsystemRequired() {
		return false;
	}
}
