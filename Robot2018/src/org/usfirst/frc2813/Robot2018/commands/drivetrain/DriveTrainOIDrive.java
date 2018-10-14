// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.drivetrain;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.logging.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Drives the robot in arcade drive with Robot.oi.joystick1.
 */
public final class DriveTrainOIDrive extends SubsystemCommand<DriveTrain> {
	
	private final Joystick joystick1; 
	private final long startTime;
	
	public DriveTrainOIDrive(DriveTrain driveTrain, Joystick joystick1) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.joystick1 = joystick1;
		this.startTime = System.currentTimeMillis();
//		addArg("joystick1", joystick1);
//		addArg("joystick2", joystick2);
		setName(toString());
	}

	/**
	 * Called repeatedly when this Command is scheduled to run
	 */
	@Override
	protected void ghscExecute() {
		if(!DriverStation.getInstance().isAutonomous()) {
			if(subsystem.shouldSuspendToPreventBrownout()) {
				Logger.error("DriveTrain is disabling itself to prevent brownout.");
			} else {
				subsystem.arcadeDrive(joystick1);
			}
		}
	}

	/**
	 * Never finished, wait for interruption?
	 */
	@Override
	protected boolean ghscIsFinished() {
		return false;
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

	@Override
	protected void ghscInitialize() {
	}
}
