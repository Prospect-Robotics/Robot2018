package org.usfirst.frc2813.Robot2018.subsystems.drivetrain;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

/*
 * DifferentialDrive with PID to hold position when idle
 */
public class DifferentialDriveWithBlocking extends DifferentialDrive {

	public DifferentialDriveWithBlocking(SpeedController leftMotor, SpeedController rightMotor) {
		super(leftMotor, rightMotor);
	}

	private boolean isIdleArcade(double xSpeed, double zRotation) {
		// TODO: Return true if stick is in the dead zone
		return false;
	}

	private boolean isIdleCurvature(double xSpeed, double zRotation) {
		// TODO: Return true if stick is in the dead zone
		return false;
	}

	private boolean isIdleTank(double leftSpeed, double rightSpeed) {
		// TODO: Return true if stick is in the dead zone
		return false;
	}
	
	private void enterBlockingMode() {
		// TODO: Start PID loop to keep the encoders from moving
	}
	
	private void exitBlockingMode() {
		// TODO: Start PID loop to keep the encoders from moving
	}
	
	@Override
	public void arcadeDrive(double xSpeed, double zRotation) {
		if(isIdleArcade(xSpeed, zRotation)) {
			enterBlockingMode();
		} else {
			exitBlockingMode();
			super.arcadeDrive(xSpeed, zRotation);
		}
	}

	@Override
	public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
		if(isIdleArcade(xSpeed, zRotation)) {
			enterBlockingMode();
		} else {
			exitBlockingMode();
			super.arcadeDrive(xSpeed, zRotation, squaredInputs);
		}
	}

	@Override
	public void curvatureDrive(double xSpeed, double zRotation, boolean isQuickTurn) {
		if(isIdleCurvature(xSpeed, zRotation)) {
			enterBlockingMode();
		} else {
			exitBlockingMode();
			super.curvatureDrive(xSpeed, zRotation, isQuickTurn);
		}
	}

	@Override
	public void tankDrive(double leftSpeed, double rightSpeed) {
		if(isIdleTank(leftSpeed, rightSpeed)) {
			enterBlockingMode();
		} else {
			exitBlockingMode();
			super.tankDrive(leftSpeed, rightSpeed);
		}
	}

	@Override
	public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
		if(isIdleTank(leftSpeed, rightSpeed)) {
			enterBlockingMode();
		} else {
			exitBlockingMode();
			super.tankDrive(leftSpeed, rightSpeed, squaredInputs);
		}
	}
}

