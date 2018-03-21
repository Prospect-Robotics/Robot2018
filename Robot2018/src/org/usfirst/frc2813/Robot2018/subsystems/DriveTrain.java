// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDrive;
import org.usfirst.frc2813.logging.Logger;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * This is our drive train. It goes.
 *
 * Note that the hardware is probably wired incorrectly.  Normally the motor polarity on both sides of the drive train is wired
 * the same way and then you set one side's speed controller to inverted and one side's sensor to inverted
 * (if encoders and motors face the same way, one side would have sensor and motor inverted in software.
 * Conversely, if the encoders and motors face opposite directions then you would need to invert the sensor on one side
 * and invert the motor on the other).
 *
 * TODO: Hardware may be wired incorrectly.
 * TODO:  encoderPort.setReverseDirection is set to true, but we are still multiplying the distance value by -1.  This seems like a bug.
 */
public class DriveTrain extends GearheadsSubsystem {
    public static final double WHEEL_DIAMETER_INCHES = 4;
    public static final double WHEEL_CIRCUMFERENCE_INCHES = Math.PI * WHEEL_DIAMETER_INCHES;
    public static final double ENCODER_TO_DRIVE_SCALE = 3;
    public static final double ENCODER_PULSES_PER_ENCODER_REVOLUTION = 256;
    public static final double ENCODER_PULSES_PER_WHEEL_REVOLUTION = ENCODER_PULSES_PER_ENCODER_REVOLUTION * ENCODER_TO_DRIVE_SCALE;
    public static final double INCHES_PER_ENCODER_PULSE = WHEEL_CIRCUMFERENCE_INCHES / ENCODER_PULSES_PER_WHEEL_REVOLUTION;

	public final SpeedController speedControllerPort;
	public final SpeedController speedControllerStarboard;
	private final VictorSPX speedControllerPortFollow;
	private final VictorSPX speedControllerStarboardFollow;
	private final DifferentialDrive robotDrive;
	public final Encoder encoderStarboard;
	public final Encoder encoderPort;
	public final Solenoid gearShift;

	public boolean encoderPortFunctional, encoderStarboardFunctional; // set by POST.

	public DriveTrain() {
		speedControllerPort = RobotMap.driveTrainSpeedControllerPort;
		speedControllerStarboard = RobotMap.driveTrainSpeedControllerStarboard;
		speedControllerPortFollow = RobotMap.driveTrainSpeedControllerPortFollow;
		speedControllerStarboardFollow = RobotMap.driveTrainSpeedControllerStarFollow;
		robotDrive = RobotMap.driveTrainRobotDrive;
		encoderStarboard = RobotMap.driveTrainQuadratureEncoderStarboard;
		encoderPort = RobotMap.driveTrainQuadratureEncoderPort;
		gearShift = RobotMap.driveTrainGearShiftSolenoid;

		addChild(robotDrive);
		addChild((Sendable) speedControllerPort);
		LiveWindow.add((Sendable) speedControllerPort);
		addChild((Sendable) speedControllerStarboard);
		LiveWindow.add((Sendable) speedControllerStarboard);

		robotDrive.setSafetyEnabled(true);
		robotDrive.setExpiration(0.1);
		// robotDrive.setSensitivity(0.5);//TODO Why aren't we setting sensitivity?
		robotDrive.setMaxOutput(1.0);

		speedControllerPort.setInverted(false);
		speedControllerStarboard.setInverted(false);

		encoderStarboard.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		encoderStarboard.setSamplesToAverage(1);
		encoderStarboard.setPIDSourceType(PIDSourceType.kRate);
		encoderPort.setReverseDirection(true);
		encoderPort.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		encoderPort.setSamplesToAverage(1);
		encoderPort.setPIDSourceType(PIDSourceType.kRate);
	}

	// @Override
	public void initDefaultCommand() {
		setDefaultCommand(new DriveTrainOIDrive());
	}

	@Override
	public void periodic() {
		// Put code here to be run every loop

	}
	//TODO Clean up driveTrain.arcadeDrive below

	/**
	 * arcadeDrive for use in OI
	 * @param joystick1
	 * @param joystickIgnored
	 */
	public void arcadeDrive(Joystick joystick1, Joystick joystickIgnored) {// defines arcadeDrive for OI
		/*
		 *  The arcadeDrive call parameters are (signed) speed and (signed) turn value [-1, 1]
		 *  Pushing the joystick forward to drive forward decreases the joystick's Y parameter.
		 *  Even though the arcadeDrive calls this field "xSpeed", it comes from the Y axis of the joystick
		 *  Pushing the joystick left and right changes the joystick's X parameter.
		 *  Even though the arcadeDrive calls this field "zRotation", it comes from the X axis of the joystick.
		 */
		robotDrive.arcadeDrive(joystick1.getY(), -joystick1.getX() * Math.abs(joystick1.getX()), false);

	}

	public void arcadeDrive(double forwardSpeed, double turnSpeed) {
		/*
		 * WPI library expects forward and left to be negative to match the way Joystick values work on Windows.
		 * When we want to go forward, we reverse the sign to match at this place.
		 */
		robotDrive.arcadeDrive(- forwardSpeed, - turnSpeed, false); // false here means do not square the inputs (if
																// omitted, the argument defaults to true)
	}

	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void tankDrive(Joystick joystick1, Joystick joystick2) {// defines tankDrive
		robotDrive.tankDrive(-joystick1.getY() * Math.abs(joystick1.getY()), -joystick2.getY() * Math.abs(joystick2.getY()));
	}

	public void curvatureDrive(double forwardSpeed, double turnRadius) {
		robotDrive.curvatureDrive(forwardSpeed, turnRadius, false);
	}

	static boolean sentEncoderWarnings = false;
	/**
	 * Return the distance the robot has traveled in feet since the last call to
	 * {@link #reset Robot.driveTrain.reset()}, or robot program start, whichever
	 * was later. If the robot drove backwards, the value returned will be negative.
	 * If the robot spins in place, the value will not change.
	 *
	 * DriveTrain.getDistance() also checks to ensure functionality of the encoders and throws
	 * an error to the console if both encoders are nonfunctional
	 *
	 * @return the average of the left and right encoder values
	 */
	public double getDistance() {
		/*
    	 * A note on Encoders and the sign of distance:
    	 * Encoders will decrement when the roll backwards.  Therefore, if you want the robot to travel backwards during autonomous,
    	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS")
    	 *
    	 * NOTE: encoderPort should
    	 */
		if (encoderPortFunctional && encoderStarboardFunctional)
			return (encoderStarboard.getDistance() + (-1 * encoderPort.getDistance()))/2;
		else if(encoderPortFunctional) {
			if(!sentEncoderWarnings) {
				Logger.info("WARNING: The right drive train encoder is non-functional.");
				sentEncoderWarnings = true;
			}
			return -encoderPort.getDistance();
		}
		else if(encoderStarboardFunctional) {
			if(!sentEncoderWarnings) {
				Logger.info("WARNING: The left drive train encoder is non-functional.");
				sentEncoderWarnings = true;
			}
			return encoderStarboard.getDistance();
		}
		else {
			if(!sentEncoderWarnings) {
				Logger.info("WARNING: Both drive train encoders are non-functional.");
				sentEncoderWarnings = true;
			}
			return (encoderStarboard.getDistance() + (-1 * encoderPort.getDistance()))/2;
		}
	}

	/**
	 * Reset both encoders, restoring the value returned by {@link #getDistance() Robot.driveTrain.getDistance()} to zero.
	 */
	public void reset() {
		encoderStarboard.reset();
		encoderPort.reset();
	}
	public void setBrakeCoast(NeutralMode b) {
		((VictorSPX) speedControllerPort).setNeutralMode(b);
		((VictorSPX) speedControllerStarboard).setNeutralMode(b);
		speedControllerPortFollow.setNeutralMode(b);
		speedControllerStarboardFollow.setNeutralMode(b);
	}
}
