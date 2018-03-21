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
	
	private void enterDefensiveHoldPositionMode() {
		// TODO: Start holding position
	}
	
	private void exitDefensiveHoldPositionMode() {
		// TODO: Stop holding position
	}
	
	@Override
	public void arcadeDrive(double xSpeed, double zRotation) {
		if(isIdleArcade(xSpeed, zRotation)) {
			super.stopMotor();
			enterDefensiveHoldPositionMode();
		} else {
			exitDefensiveHoldPositionMode();
			super.arcadeDrive(xSpeed, zRotation);
		}
	}

	@Override
	public void arcadeDrive(double xSpeed, double zRotation, boolean squaredInputs) {
		if(isIdleArcade(xSpeed, zRotation)) {
			super.stopMotor();
			enterDefensiveHoldPositionMode();
		} else {
			exitDefensiveHoldPositionMode();
			super.arcadeDrive(xSpeed, zRotation, squaredInputs);
		}
	}

	@Override
	public void curvatureDrive(double xSpeed, double zRotation, boolean isQuickTurn) {
		if(isIdleCurvature(xSpeed, zRotation)) {
			super.stopMotor();
			enterDefensiveHoldPositionMode();
		} else {
			exitDefensiveHoldPositionMode();
			super.curvatureDrive(xSpeed, zRotation, isQuickTurn);
		}
	}

	@Override
	public void tankDrive(double leftSpeed, double rightSpeed) {
		if(isIdleTank(leftSpeed, rightSpeed)) {
			super.stopMotor();
			enterDefensiveHoldPositionMode();
		} else {
			exitDefensiveHoldPositionMode();
			super.tankDrive(leftSpeed, rightSpeed);
		}
	}

	@Override
	public void tankDrive(double leftSpeed, double rightSpeed, boolean squaredInputs) {
		if(isIdleTank(leftSpeed, rightSpeed)) {
			super.stopMotor();
			enterDefensiveHoldPositionMode();
		} else {
			exitDefensiveHoldPositionMode();
			super.tankDrive(leftSpeed, rightSpeed, squaredInputs);
		}
	}
}

