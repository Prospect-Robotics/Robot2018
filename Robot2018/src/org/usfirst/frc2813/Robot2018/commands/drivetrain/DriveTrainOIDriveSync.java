// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.drivetrain;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

/**
 * Drives the robot in arcade drive with Robot.oi.joystick1.
 */
public final class DriveTrainOIDriveSync extends AbstractDriveTrainCommand {
	
	private final Joystick joystick1; 
	private final Joystick joystick2;
	
	public DriveTrainOIDriveSync(DriveTrain driveTrain, Joystick joystick1, Joystick joystick2) {
		super(driveTrain, true /* require the subsystem */);
		this.joystick1 = joystick1;
		this.joystick2 = joystick2;
		setName(toString());
	}

	/**
	 * Called repeatedly when this Command is scheduled to run
	 */
	@Override
	protected void execute() {
		super.execute();
		if(!DriverStation.getInstance().isAutonomous()) {
			driveTrain.arcadeDrive(joystick1, joystick2);
		}
	}

	/**
	 * Never finished, wait for interruption?
	 */
	protected boolean isFinished() {
		return false;
	}
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + driveTrain + ", joystick1=" + joystick1 + ", joystick2=" + joystick2 + ")"; 
    }
}
