// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.subsystems.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDrive;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDriveWithPIDStop;
import org.usfirst.frc2813.Robot2018.subsystems.GearheadsSubsystem;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.GearShiftConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.interfaces.Gyro;
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
    public static final double WHEEL_DIAMETER_INCHES = 3.95;
    public static final double WHEEL_CIRCUMFERENCE_INCHES = Math.PI * WHEEL_DIAMETER_INCHES;
    public static final double ENCODER_TO_DRIVE_SCALE = 3;
    public static final double ENCODER_PULSES_PER_ENCODER_REVOLUTION = 256;
    public static final double ENCODER_PULSES_PER_WHEEL_REVOLUTION = ENCODER_PULSES_PER_ENCODER_REVOLUTION * ENCODER_TO_DRIVE_SCALE;
    public static final double INCHES_PER_ENCODER_PULSE = WHEEL_CIRCUMFERENCE_INCHES / ENCODER_PULSES_PER_WHEEL_REVOLUTION;

    private final Gyro gyro;
    private final WPI_TalonSRX speedControllerLeftMaster;
	private final WPI_TalonSRX speedControllerRightMaster;
	private final VictorSPX speedControllerPortFollow;
	private final VictorSPX speedControllerStarboardFollow;
	private final DifferentialDrive robotDrive;
	private final Encoder encoderRight;
	private final Encoder encoderLeft;
	private final Solenoid gearShift;

	public boolean encoderPortFunctional, encoderStarboardFunctional; // set by POST.

	public static final int PEAK_CURRENT_LIMIT       = 35;
	public static final int PEAK_CURRENT_DURATION    = 0;
	public static final int CONTINUOUS_CURRENT_LIMIT = 35;

	public DriveTrain(Gyro gyro) {
		this.gyro = gyro;
		this.encoderRight = RobotMap.driveTrainRightEncoder;
		this.encoderLeft = RobotMap.driveTrainLeftEncoder;
		this.speedControllerLeftMaster = RobotMap.driveTrainSpeedControllerLeftMaster;
		this.speedControllerRightMaster = RobotMap.driveTrainSpeedControllerRightMaster;
		this.speedControllerPortFollow = RobotMap.driveTrainSpeedControllerLeftFollower;
		this.speedControllerStarboardFollow = RobotMap.driveTrainSpeedControllerRightFollower;
		this.robotDrive = RobotMap.driveTrainRobotDrive;
		this.gearShift = new Solenoid(new GearShiftConfiguration(), RobotMap.gearShiftSolenoid);

		addChild(robotDrive);
		addChild((Sendable) speedControllerLeftMaster);
		LiveWindow.add((Sendable) speedControllerLeftMaster);
		addChild((Sendable) speedControllerRightMaster);
		LiveWindow.add((Sendable) speedControllerRightMaster);

		robotDrive.setSafetyEnabled(true);
		robotDrive.setExpiration(0.1);
		// robotDrive.setSensitivity(0.5);//TODO Why aren't we setting sensitivity?
		robotDrive.setMaxOutput(1.0);

		speedControllerLeftMaster.setInverted(false);
		speedControllerRightMaster.setInverted(false);

		encoderRight.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		encoderRight.setSamplesToAverage(1);
		encoderRight.setPIDSourceType(PIDSourceType.kRate);
		encoderLeft.setDistancePerPulse(INCHES_PER_ENCODER_PULSE);
		encoderLeft.setSamplesToAverage(1);
		encoderLeft.setPIDSourceType(PIDSourceType.kRate);

		// Set static current limits
		speedControllerLeftMaster.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, 10);
		speedControllerLeftMaster.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, 10);
		speedControllerLeftMaster.configPeakCurrentDuration(PEAK_CURRENT_DURATION, 10);
		speedControllerLeftMaster.enableCurrentLimit(true);

		speedControllerRightMaster.configContinuousCurrentLimit(CONTINUOUS_CURRENT_LIMIT, 10);
		speedControllerRightMaster.configPeakCurrentLimit(PEAK_CURRENT_LIMIT, 10);
		speedControllerRightMaster.configPeakCurrentDuration(PEAK_CURRENT_DURATION, 10);
		speedControllerRightMaster.enableCurrentLimit(true);
	}

	public Gyro getGyro() {
		return gyro;
	}
	
	public Solenoid getGearShiftSolenoid() {
		return gearShift;
	}
	
	public Encoder getEncoderRight() {
		return encoderRight;
	}
	
	public Encoder getEncoderLeft() {
		return encoderLeft;
	}
	
	public SpeedController getSpeedControllerRightMaster() {
		return speedControllerRightMaster;
	}
	
	public SpeedController getSpeedControllerLeftMaster() {
		return speedControllerLeftMaster;
	}
	
	// @Override
	public void initDefaultCommand() {
		/*
		NOTE: The WPI model makes it impossible not to reach outside DriveTrain to grab joysticks from OI
		 because DriveTrain has to be created before OI creates the joysticks.  Plus they are asking for
		 a default command inside a subsystem, which is mixing the UI with the implementation and
		 is poor design, but nothing we can do. */
		setDefaultCommand(new DriveTrainOIDrive(this, Robot.oi.getJoystick1(), Robot.oi.getJoystick2()));
//		setDefaultCommand(new DriveTrainOIDriveWithPIDStop(this, Robot.oi.getJoystick1(), Robot.oi.getJoystick2()));
	}

	@Override
	public void periodic() {
		// If our voltage is getting low and ANYTHING else is running, stop the drive train
		if(shouldSuspendToPreventBrownout()) {
			stop();
		}
	}
	
	/**
	 * If voltage is getting low and arm/elevator/intake is running, stop driving... 
	 */
	public boolean shouldSuspendToPreventBrownout() {
//		return RobotMap.pdp.getVoltage() <= 8 && 
//				(  (RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_Arm_Master) > 0)
//				|| (RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_Elevator_Right_Master) > 0)
//				|| (RobotMap.pdp.getCurrent(RobotMap.PDP_PORT_Intake_Master) > 0));
		return false;
	}

	/**
	 * arcadeDrive for use in OI
	 * @param joystick1 The joystick to use for linear throttle and angular throttle
	 * @param joystickIgnored We don't use this one in arcadeDrive
	 */
	public void arcadeDrive(Joystick joystick1, Joystick joystickIgnored) {// defines arcadeDrive for OI
		/*
		 *  The arcadeDrive call parameters are (signed) speed and (signed) turn value [-1, 1]
		 *  Pushing the joystick forward to drive forward decreases the joystick's Y parameter.
		 *  Even though the arcadeDrive calls this field "xSpeed", it comes from the Y axis of the joystick
		 *  Pushing the joystick left and right changes the joystick's X parameter.
		 *  Even though the arcadeDrive calls this field "zRotation", it comes from the X axis of the joystick.
		 */
		robotDrive.arcadeDrive(joystick1.getY(), ((-joystick1.getX() * Math.abs(joystick1.getX())) + (-joystick1.getTwist() * Math.abs(joystick1.getTwist()))), false);

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
	 * Return the distance the robot has traveled in inches since the last call to
	 * driveTrain.reset(), or robot program start, whichever
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
		if (encoderPortFunctional && encoderStarboardFunctional) {
			return (encoderRight.getDistance() + encoderLeft.getDistance())/2;
		} 
		else if(encoderPortFunctional) {
			if(!sentEncoderWarnings) {
				Logger.info("WARNING: The right drive train encoder is non-functional.");
				sentEncoderWarnings = true;
			}
			return encoderLeft.getDistance();
		}
		else if(encoderStarboardFunctional) {
			if(!sentEncoderWarnings) {
				Logger.info("WARNING: The left drive train encoder is non-functional.");
				sentEncoderWarnings = true;
			}
			return encoderRight.getDistance();
		}
		else {
//			if(!sentEncoderWarnings) {
				Logger.error("ERROR: Both drive train encoders are non-functional.  Returning zero.");
//				sentEncoderWarnings = true;
//			}
			return 0;
		}
	}
	
	public void stop() {
		speedControllerLeftMaster.set(0);
		speedControllerRightMaster.set(0);
	}

	public void setBrakeCoast(NeutralMode b) {
		speedControllerLeftMaster.setNeutralMode(b);
		speedControllerRightMaster.setNeutralMode(b);
		speedControllerPortFollow.setNeutralMode(b);
		speedControllerStarboardFollow.setNeutralMode(b);
	}
	
	/**
	 * Set the gear shifter into high or low gear
	 * @param gear The gear to shift to.
	 */
	public void setGearShift(Direction gear) {
		this.gearShift.setTargetPosition(gear);
	}
	
}
