package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class DriveTrainOIDriveWithPIDStop extends DriveTrainAutoStop {
	
	private final Joystick joystick1; 
	private final Joystick joystick2;

	public DriveTrainOIDriveWithPIDStop(DriveTrain driveTrain, Joystick joystick1, Joystick joystick2) {
		super(driveTrain);
		this.joystick1 = joystick1;
		this.joystick2 = joystick2;
		addArg("joystick1", joystick1);
		addArg("joystick2", joystick2);
		setName(toString());
	}
	
	/**
	 * We are always going to keep going as the default command and we're interactive
	 */
	protected boolean ghscIsFinished() {
		return false;
	}

	protected boolean isJoystickIdle() {
		return joystick1.getY() == 0 || joystick1.getX() == 0;
	}

	/**
	 * Called repeatedly when this Command is scheduled to run
	 */
	@Override
	protected void ghscExecute() {
		boolean pidStop = false;
		if(DriverStation.getInstance().isAutonomous()) {
			pidStop = true;
		} else if(isJoystickIdle()) {
			pidStop = true;
		} else {
			if(isPIDEnabled()) {
				disablePID();
			}
			subsystem.arcadeDrive(joystick1, joystick2);
		}
		
		if(pidStop && !isPIDEnabled()) {
			enablePID();
		}
	}
}
