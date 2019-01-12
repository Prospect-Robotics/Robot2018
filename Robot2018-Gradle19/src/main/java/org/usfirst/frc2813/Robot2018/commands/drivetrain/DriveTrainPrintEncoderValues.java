package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.post.POST;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.util.Formatter;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Print out the drive train encoder values to the log.
 */
public final class DriveTrainPrintEncoderValues extends SubsystemCommand<DriveTrain> {
	private long last;
	private final Encoder rightEncoder;
	private final Encoder leftEncoder;
	private final int reportingInterval;

	public DriveTrainPrintEncoderValues(DriveTrain driveTrain, int reportingInterval) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.last = 0;
		this.leftEncoder = driveTrain.getEncoderLeft();
		this.rightEncoder = driveTrain.getEncoderRight();
		this.reportingInterval = reportingInterval;
		setName(toString());
		setRunWhenDisabled(true);
	}

	// Called just before this Command runs the first time
	@Override
	protected void ghscInitialize() {
	}

	public static String getEncoderStatus(String label, Encoder encoder) {
		return Formatter.safeFormat("Encoder.%s Get=%s Raw=%s Dist=%s DistPerPulse=%s Stopped=%s]",
			label,
			encoder.get(), 
			encoder.getRaw(),  
			encoder.getDistance(),
			encoder.getDistancePerPulse(),
			encoder.getStopped());
	}
	// Called repeatedly when this Command is scheduled to run
	@Override
	protected void ghscExecute() {
		if((System.currentTimeMillis() - last) >= reportingInterval) {
			if(POST.isPostComplete()) {
				System.out.println(Formatter.safeFormat("DriveTrain %s%s[Dist=%s] %s / %s", 
							Robot.driveTrain.encoderPortFunctional ? " [LEFT OK]" : " <<LEFT BROKEN>>",
							Robot.driveTrain.encoderStarboardFunctional ? " [RIGHT OK]" : " <<RIGHT BROKEN>>",
							Robot.driveTrain.getDistance(),
							getEncoderStatus("Left", leftEncoder), 
							getEncoderStatus("Right", rightEncoder)
						));
			} else {
				System.out.println(Formatter.safeFormat("DriveTrainPrintEncoderValues: Waiting for POST."));
			}
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
