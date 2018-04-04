package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.util.Formatter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Print out the drive train encoder values to the log.
 */
public final class DriveTrainPrintPowerLevels extends SubsystemCommand<DriveTrain> {
	private long last;
	private final int reportingInterval;

	public DriveTrainPrintPowerLevels(DriveTrain driveTrain, int reportingInterval) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.last = 0;
		this.reportingInterval = reportingInterval;
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
		if((System.currentTimeMillis() - last) >= reportingInterval) {
			Logger.info("CURRENT- LM=" 
			+ RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_DriveTrain_Left_Master)
			+ " LF="
			+ RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_DriveTrain_Left_Follower)
			+ " RM="
			+ RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_DriveTrain_Right_Master)
			+ " RF="
			+ RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_DriveTrain_Right_Follower) 
			+ " TOTAL=" + RobotMap.pdp.getTotalCurrent()
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
