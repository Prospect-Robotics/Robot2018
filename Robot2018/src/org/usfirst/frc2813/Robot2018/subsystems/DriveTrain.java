// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.OIDrive;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

/**
 * This is our drive train. It goes.
 */
public class DriveTrain extends Subsystem {
    private static final double WHEEL_DIAMETER = 4;
    public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
    //public static final double REVOLUTIONS_PER_INCH = 1.0 / WHEEL_CIRCUMFERENCE;
    public static final double PULSES_PER_REVOLUTION = 768; // it should be 256 but the robot only % as far as it should with 256
    //public static final double PULSES_PER_INCH = PULSES_PER_REVOLUTION * REVOLUTIONS_PER_INCH;goes ~80
    public static final double INCHES_PER_PULSE = WHEEL_CIRCUMFERENCE / PULSES_PER_REVOLUTION; // inches per revolution times revolutions per pulse (1/pulses per revolution) equals ihes per pulse.

	
	public final SpeedController speedControllerPort = RobotMap.driveTrainSpeedControllerPort;
	public final SpeedController speedControllerStarboard = RobotMap.driveTrainSpeedControllerStarboard;
	private final VictorSPX speedControllerPortFollow = RobotMap.driveTrainSpeedControllerPortFollow;
	private final VictorSPX speedControllerStarboardFollow = RobotMap.driveTrainSpeedControllerStarFollow;
	private final DifferentialDrive robotDrive = RobotMap.driveTrainRobotDrive;
	public final Encoder encoderStarboard = RobotMap.driveTrainQuadratureEncoderStarboard;
	public final Encoder encoderPort = RobotMap.driveTrainQuadratureEncoderPort;
	public final Solenoid gearShift = RobotMap.driveTrainGearShiftSolenoid;
	public boolean encoderPortFunctional, encoderStarboardFunctional; // set by POST.
	
	public DriveTrain() {
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

		encoderStarboard.setDistancePerPulse(INCHES_PER_PULSE);
		encoderStarboard.setSamplesToAverage(1);
		encoderStarboard.setPIDSourceType(PIDSourceType.kRate);
		encoderPort.setReverseDirection(true);
		encoderPort.setDistancePerPulse(INCHES_PER_PULSE);
		encoderPort.setSamplesToAverage(1);
		encoderPort.setPIDSourceType(PIDSourceType.kRate);
	}

	// @Override
	public void initDefaultCommand() {
		setDefaultCommand(new OIDrive());
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
		// double z = joystick1.getX() + joystick1.getTwist();
		// double x = joystick1.getY();
		// Please note: Grady had added this some time ago and it wasn't working.  I just fixed it.  Jack may yell at us for changing it.
		gearShift.set(Math.abs(joystick1.getY()) > .7);		//  If >70% speed, shift into high gear; if <=70% speed, shift into low gear
		
		/*
		 *  The arcadeDrive call parameters are (signed) speed and (signed) turn value [-1, 1]
		 *  Pushing the joystick forward to drive forward increases the joystick's Y parameter.  Even though the arcadeDrive calls this field "xSpeed", it comes from the Y axis of the joystick
		 *  Pushing the joystick left and right changes the joystick's X parameter.  Even though the arcadeDrive calls this field "zRotation", it comes from the X axis of the joystick.
		 */
		robotDrive.arcadeDrive(joystick1.getY(), -joystick1.getX() * Math.abs(joystick1.getX()), false);
		
	}
	// Put methods for controlling this subsystem
	// here. Call these from Commands.

	public void tankDrive(Joystick joystick1, Joystick joystick2) {// defines tankDrive
		robotDrive.tankDrive(-joystick1.getY() * Math.abs(joystick1.getY()),
				-joystick2.getY() * Math.abs(joystick2.getY()));
	}

	public void arcadeDrive(double forwardSpeed, double turnSpeed) {
		robotDrive.arcadeDrive(forwardSpeed, turnSpeed, false); // false here means do not square the inputs (if
																// omitted, the argument defaults to true)
	}

	public void curvatureDrive(double forwardSpeed, double turnRadius) {
		robotDrive.curvatureDrive(forwardSpeed, turnRadius, false);
	}
	
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
    	 * you must set BOTH the speed and the distance to a negative value (multiply by "BACKWARDS"
    	 */

		if (encoderPortFunctional && encoderStarboardFunctional)
			return (encoderStarboard.getDistance() + (-1 * encoderPort.getDistance()))/2;
		else if(encoderPortFunctional) {
			System.out.println("encoderPort NOT FUNCTIONAL");
			return -encoderPort.getDistance();
		}
		else if(encoderStarboardFunctional) {
			System.out.println("encoderStarboard NOT FUNCTIONAL");
			return encoderStarboard.getDistance();
		}
		else {
			System.out.println("Both Encoders NOT FUNCTIONAL");
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