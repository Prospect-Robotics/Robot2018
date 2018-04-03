package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.util.Formatter;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Print out the drive train encoder values to the log.
 */
public final class DriveTrainPrintEncoderValues extends SubsystemCommand<DriveTrain> {
	private long last;
	private final Encoder starboardEncoder;
	private final Encoder portEncoder;
	private final int reportingInterval;

	public DriveTrainPrintEncoderValues(DriveTrain driveTrain, int reportingInterval) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.last = 0;
		this.portEncoder = driveTrain.getEncoderPort();
		this.starboardEncoder = driveTrain.getEncoderStarboard();
		this.reportingInterval = reportingInterval;
		setName(toString());
		setRunWhenDisabled(true);
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
	}

	public static String getEncoderStatus(String label, Encoder encoder) {
		return Formatter.safeFormat("Encoder.%s Get=%s Raw=%s Dist=%s DistInches=%s DistPerPulse=%s Dir=%s Stopped=%s]",
			label,
			encoder.get(), 
			encoder.getRaw(),  
			encoder.getDistance(),
			encoder.getDistance()*DriveTrain.WHEEL_CIRCUMFERENCE_INCHES, 
			encoder.getDistancePerPulse(),
			encoder.getDirection(),
			encoder.getStopped());
	}
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void ghscExecute() {
		if((System.currentTimeMillis() - last) >= reportingInterval) {
			System.out.println(
				Formatter.safeFormat("%s\n%s\nDriveTrain [Dist=%s]\n",
					getEncoderStatus("Left", portEncoder),
					getEncoderStatus("Right", starboardEncoder),
					Robot.driveTrain.getDistance()));
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
