// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.

package org.usfirst.frc2813.Robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
//import edu.wpi.first.wpilibj.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
	public static WPI_VictorSPX driveTrainSpeedControllerPort;// Port Motor
	public static WPI_VictorSPX driveTrainSpeedControllerStarboard;// Starboard Motor
	public static VictorSPX driveTrainSpeedControllerPortFollow;
	public static VictorSPX driveTrainSpeedControllerStarFollow;
	public static DifferentialDrive driveTrainRobotDrive;
	//TODO verify if encoders are correctly named port/starboard
	public static Encoder driveTrainQuadratureEncoderStarboard;// Starboard Motor
	public static Encoder driveTrainQuadratureEncoderPort;// Port Motor
	public static Solenoid driveTrainSingleSolenoid;
	public static DoubleSolenoid driveTrainDoubleSolenoid;
	public static Solenoid armSingleSolenoid;//for the competition bot
	public static DoubleSolenoid armDoubleSolenoid;//for the practice bot
	public static WPI_VictorSPX elevatorSpeedControllerPort;	//  The WPI_VictorSPX type is used for the master elevator motor - others will follow this one
															//  TODO:  document WHY the WPI_VictorSPX type and the VictorSPX type are different
	public static VictorSPX elevatorSpeedControllerStarboard;		//  Controller Starboard will follow controller Port; For followers, use the VictorSPX type rather than the WPI_VictorSPX type
	public static VictorSPX elevatorSpeedControllerPortFollow;		//  Controller PortFollow is the paired motor with controller Port; it will follow 1
	public static VictorSPX elevatorSpeedControllerStarboardFollow;		//  Controller StarboardFollow is the paired motor with controller Starboard; it will follow 2
	public static WPI_VictorSPX intakeSpeedController;
	public static VictorSPX intakeSpeedControllerFollow;
	public static Encoder elevatorQuadratureEncoder1;
	public static DigitalInput elevatorLimitSwitch;
	public static SpeedController armSpeedController;
	public static Encoder armQuadratureEncoder;
	public static DigitalInput armLimitSwitch;
	
	private static final double WHEEL_DIAMETER = 4;
	public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
	//public static final double REVOLUTIONS_PER_INCH = 1.0 / WHEEL_CIRCUMFERENCE;
	public static final double PULSES_PER_REVOLUTION = 768; // it should be 256 but the robot only % as far as it should with 256
	//public static final double PULSES_PER_INCH = PULSES_PER_REVOLUTION * REVOLUTIONS_PER_INCH;goes ~80
	public static final double DRIVE_TRAIN_INCHES_PER_PULSE = WHEEL_CIRCUMFERENCE / PULSES_PER_REVOLUTION; // inches per revolution times revolutions per pulse (1/pulses per revolution) equals inches per pulse.
	
	
	public static final double SRX_MAG_PULSES_PER_REVOLUTION = 4096; // pulses per revolution on a Talon SRX branded magnetic encoder, commonly known as an "SRX mag".
	public static final double ELEVATOR_INCHES_PER_REVOLUTION = Math.PI * 1.25;
	public static final double ELEVATOR_INCHES_PER_PULSE = ELEVATOR_INCHES_PER_REVOLUTION / SRX_MAG_PULSES_PER_REVOLUTION;
	// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

	@SuppressWarnings("deprecation")
	public static void init() {
		// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
		driveTrainSpeedControllerPort = new WPI_VictorSPX(1);
		driveTrainSpeedControllerPort.setName("DriveTrain", "Port Motor");
		driveTrainSpeedControllerPort.setInverted(false);
		LiveWindow.add(driveTrainSpeedControllerPort);
		driveTrainSpeedControllerStarboard = new WPI_VictorSPX(7);
		driveTrainSpeedControllerStarboard.setName("DriveTrain", "Starboard Motor");
		driveTrainSpeedControllerStarboard.setInverted(false);
		LiveWindow.add(driveTrainSpeedControllerStarboard);

		driveTrainSpeedControllerPortFollow = new VictorSPX(2);
		driveTrainSpeedControllerPortFollow.set(ControlMode.Follower, driveTrainSpeedControllerPort.getDeviceID());
		driveTrainSpeedControllerPortFollow.setInverted(driveTrainSpeedControllerPort.getInverted());
		driveTrainSpeedControllerStarFollow = new VictorSPX(8);
		driveTrainSpeedControllerStarFollow.set(ControlMode.Follower, driveTrainSpeedControllerStarboard.getDeviceID());
		driveTrainSpeedControllerStarFollow.setInverted(false);

		// driveTrainSpeedController3 = new PWMVictorSPX(2);
		// LiveWindow.addActuator("DriveTrain", "Speed Controller 3", (PWMVictorSPX)
		// driveTrainSpeedController3);
		// driveTrainSpeedController3.setInverted(false);
		// driveTrainSpeedController4 = new PWMVictorSPX(3);
		// LiveWindow.addActuator("DriveTrain", "Speed Controller 4", (PWMVictorSPX)
		// driveTrainSpeedController4);
		// driveTrainSpeedController4.setInverted(false);
		driveTrainRobotDrive = new DifferentialDrive(driveTrainSpeedControllerPort, driveTrainSpeedControllerStarboard);
		LiveWindow.add(driveTrainRobotDrive);
		// driveTrainSpeedController3, driveTrainSpeedController4);

		driveTrainRobotDrive.setSafetyEnabled(true);
		driveTrainRobotDrive.setExpiration(0.1);
		// driveTrainRobotDrive.setSensitivity(0.5);
		driveTrainRobotDrive.setMaxOutput(1.0);

		driveTrainQuadratureEncoderStarboard = new Encoder(10, 11, true, EncodingType.k4X);// port 0 2*0+10=10, port 0 2*0+11=11
		LiveWindow.addSensor("DriveTrain", "Starboard Encoder", driveTrainQuadratureEncoderStarboard);
		driveTrainQuadratureEncoderStarboard.setDistancePerPulse(DRIVE_TRAIN_INCHES_PER_PULSE);
		driveTrainQuadratureEncoderStarboard.setSamplesToAverage(1);
		driveTrainQuadratureEncoderStarboard.setPIDSourceType(PIDSourceType.kRate);
		driveTrainQuadratureEncoderPort = new Encoder(12, 13, true, EncodingType.k4X);
		driveTrainQuadratureEncoderPort.setReverseDirection(true);
		LiveWindow.addSensor("DriveTrain", "Port Encoder", driveTrainQuadratureEncoderPort);
		driveTrainQuadratureEncoderPort.setDistancePerPulse(DRIVE_TRAIN_INCHES_PER_PULSE);
		driveTrainQuadratureEncoderPort.setSamplesToAverage(1);
		driveTrainQuadratureEncoderPort.setPIDSourceType(PIDSourceType.kRate);
		// driveTrainQuadratureEncoder3 = new Encoder(4, 5, true, EncodingType.k4X);
		// LiveWindow.addSensor("DriveTrain", "Quadrature Encoder 3",
		// driveTrainQuadratureEncoder3);
		// driveTrainQuadratureEncoder3.setDistancePerPulse(1.0);
		// driveTrainQuadratureEncoder3.setPIDSourceType(PIDSourceType.kRate);
		// driveTrainQuadratureEncoder4 = new Encoder(6, 7, true, EncodingType.k4X);
		// LiveWindow.addSensor("DriveTrain", "Quadrature Encoder 4",
		// driveTrainQuadratureEncoder4);
		// driveTrainQuadratureEncoder4.setDistancePerPulse(1.0);
		// driveTrainQuadratureEncoder4.setPIDSourceType(PIDSourceType.kRate);
		driveTrainSingleSolenoid = new Solenoid(0, 0);
		LiveWindow.addActuator("DriveTrain", "Solenoid 1", driveTrainSingleSolenoid);
		
		//  TODO:  Add better comments about port numbering
		//  In the 2018 robot, we have motor controllers mounted on each side of the robot (Port and Starboard)
		//  The Port motor controllers are numbered 1-6, Starboard 7-11 (one fewer controller on the Starboard side)
		//  TODO:  verify the numbering
		//  In general, for a task that has motors on both sides (like drive train and elevator), the starboard side number is (Port Number + 6)
		//	e.g. Port motors are ports 1 and 2, starboard motors are ports 7 and 8 (n+6)
		// The intake motors are an anomaly because the wire for the intake motors just goes up one side of the
		// elevator carriage, so both controllers are on the same side of the robot (ports 5 and 6)
		// Note:  all of the VictorSPX controllers are under CAN bus control, so we use 1-based addressing, reserving
		// port 0 on the CAN bus for new controllers / uninitilized controllers
		elevatorSpeedControllerPort = new WPI_VictorSPX(3);
		LiveWindow.addActuator("Elevator", "Speed Controller 1", (WPI_VictorSPX) elevatorSpeedControllerPort);
		elevatorSpeedControllerPort.setInverted(false);	// Motors on the Port side spin forward
		elevatorSpeedControllerStarboard = new VictorSPX(9);
		elevatorSpeedControllerStarboard.set(ControlMode.Follower, elevatorSpeedControllerPort.getDeviceID());
		elevatorSpeedControllerStarboard.setInverted(false);	// Motors on the Starboard side spin backward
		
		elevatorSpeedControllerPortFollow = new VictorSPX(4);
		elevatorSpeedControllerPortFollow.set(ControlMode.Follower, elevatorSpeedControllerPort.getDeviceID());
		elevatorSpeedControllerPortFollow.setInverted(elevatorSpeedControllerPort.getInverted());
		elevatorSpeedControllerStarboardFollow = new VictorSPX(10);
		elevatorSpeedControllerStarboardFollow.set(ControlMode.Follower, elevatorSpeedControllerPort.getDeviceID());
		elevatorSpeedControllerStarboardFollow.setInverted(false);
		
		intakeSpeedController = new WPI_VictorSPX(5);
		LiveWindow.addActuator("Intake", "Speed Controller 1", (WPI_VictorSPX) intakeSpeedController);
		intakeSpeedController.setInverted(false);
		intakeSpeedControllerFollow = new VictorSPX(6);
		intakeSpeedControllerFollow.set(ControlMode.Follower, intakeSpeedController.getDeviceID());
		intakeSpeedControllerFollow.setInverted(true);

		/* 
		 *  Encoder for the Elevator SHAFT is physically connected to Encoder #3 on the Spartan Board
		 *  Each Encoder has two DIO signals (rising / falling pulse signals)
		 *  The Spartan board maps Encoder ports to RoboRIO ports with the formula:
		 *  RoboRIO DIO signal A = (Spartan Board Encoder Port * 2) + 10
		 *  RoboRIO DIO signal B = (Spartan Board Encoder Port * 2) + 11
		 *  For port 3, the RoboRIO signals are (3*2 + 10 = 16 and 3*2 + 11 = 17.
		 */ 
		
		elevatorQuadratureEncoder1 = new Encoder(16, 17, true, EncodingType.k4X);
		elevatorQuadratureEncoder1.setDistancePerPulse(ELEVATOR_INCHES_PER_PULSE);
		elevatorQuadratureEncoder1.setPIDSourceType(PIDSourceType.kRate);
		LiveWindow.addSensor("Elevator", "Quadrature Encoder", elevatorQuadratureEncoder1);
		
		elevatorLimitSwitch = new DigitalInput(0);

		armSpeedController = new WPI_VictorSPX(11);
		armQuadratureEncoder = new Encoder(14, 15, true, EncodingType.k4X);	// Spartan Board Encoder #2
		armLimitSwitch = new DigitalInput(1);
		armDoubleSolenoid = new DoubleSolenoid(0,2,3);
		LiveWindow.addSensor("Arm", "Arm Quadrature Encoder", armQuadratureEncoder);
		
		
		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
	}
}
