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

import org.usfirst.frc2813.Robot2018.GameData;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSourceType;
//import edu.wpi.first.wpilibj.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	// Read the state of the field pieces
	public static GameData gameData;

	public static WPI_VictorSPX driveTrainSpeedControllerPort;// Port Motor
	public static WPI_VictorSPX driveTrainSpeedControllerStarboard;// Starboard Motor
	public static VictorSPX driveTrainSpeedControllerPortFollow;
	public static VictorSPX driveTrainSpeedControllerStarFollow;
	public static DifferentialDrive driveTrainRobotDrive;
	//TODO verify if encoders are correctly named port/starboard
	public static Encoder driveTrainQuadratureEncoderStarboard;// Starboard Motor
	public static Encoder driveTrainQuadratureEncoderPort;// Port Motor
	public static Solenoid driveTrainSingleSolenoid;
	public static Solenoid armSingleSolenoid;//for the competition bot

	public static TalonSRX srxElevator;		// TODO:  rename when we are sure this works; NOT WPI_TalonSRX - need the CTRE libraries

	public static TalonSRX srxArm;			// TODO:  rename when we are sure this works; NOT WPI_TalonSRX - need the CTRE libraries

	public static Solenoid elevatorRatchet;
	public static Solenoid climbingBar;
	public static VictorSPX elevatorSpeedControllerPort;	//  The WPI_VictorSPX type is used for the master elevator motor - others will follow this one
															//  TODO:  document WHY the WPI_VictorSPX type and the VictorSPX type are different
	public static VictorSPX elevatorSpeedControllerStarboard;		//  Controller Starboard will follow controller Port; For followers, use the VictorSPX type rather than the WPI_VictorSPX type
	public static VictorSPX elevatorSpeedControllerPortFollow;		//  Controller PortFollow is the paired motor with controller Port; it will follow 1
	public static WPI_VictorSPX intakeSpeedController;
	public static VictorSPX intakeSpeedControllerFollow;
	//public static Encoder armQuadratureEncoder;
	//public static DigitalInput armLimitSwitch;

	private static final double WHEEL_DIAMETER = 4;
	public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
	//public static final double REVOLUTIONS_PER_INCH = 1.0 / WHEEL_CIRCUMFERENCE;
	public static final double PULSES_PER_REVOLUTION = 768; // it should be 256 but the robot only % as far as it should with 256
	//public static final double PULSES_PER_INCH = PULSES_PER_REVOLUTION * REVOLUTIONS_PER_INCH;goes ~80
	public static final double DRIVE_TRAIN_INCHES_PER_PULSE = WHEEL_CIRCUMFERENCE / PULSES_PER_REVOLUTION; // inches per revolution times revolutions per pulse (1/pulses per revolution) equals inches per pulse.

	public static final int SRX_MAG_PULSES_PER_REVOLUTION = 4096; // pulses per revolution on a Talon SRX branded magnetic encoder, commonly known as an "SRX mag".
	public static final double SRX_VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.  One second equals 1000 millseconds.
	public static final double ELEVATOR_INCHES_PER_REVOLUTION = Math.PI * 1.25;
	public static final double ELEVATOR_INCHES_PER_PULSE = ELEVATOR_INCHES_PER_REVOLUTION / SRX_MAG_PULSES_PER_REVOLUTION;
	public static final double ELEVATOR_PULSES_PER_INCH = SRX_MAG_PULSES_PER_REVOLUTION / ELEVATOR_INCHES_PER_REVOLUTION;

	public static final double ARM_GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.  You can remove the /1 if you like.
	public static final double ARM_PULSES_PER_REVOLUTION = ARM_GEAR_RATIO * SRX_MAG_PULSES_PER_REVOLUTION;
	public static final double ARM_ONE_DEGREE = ARM_PULSES_PER_REVOLUTION / 360;
	public static final double ARM_ONE_DEGREE_PER_SECOND = ARM_ONE_DEGREE * SRX_VELOCITY_TIME_UNITS_PER_SEC;

	@SuppressWarnings("deprecation")
	public static void init() {

		// Get the game setup data from the driver station
		gameData = new GameData(DriverStation.getInstance().getGameSpecificMessage());
				
		/* CAN BUS PORT SUMMARY
		 * (1)  WPI_VictorSPX DriveTrain Port Master
		 * (2)  VictorSPX DriveTrain Port Follower
		 * (3)  VictorSPX Elevator Port Follower
		 * (4)  VictorSPX Elevator Port Follower
		 * (5)  VictorSPX UNUSED
		 * (6)  VictorSPX Intake Follower
		 * (7)  WPI_VictorSPX DriveTrain Starboard Master
		 * (8)  VictorSPX DriveTrain Starboard Follower
		 * (9)  VictorSPX Elevator Starboard Follower
		 * (10) TalonSRX Elevator Starboard Master
		 * (11) WPI_VictorSPX Intake Master
		 * (12) TalonSRX Arm
		 */
		
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
		driveTrainRobotDrive = new DifferentialDrive(driveTrainSpeedControllerPort, driveTrainSpeedControllerStarboard);
		LiveWindow.add(driveTrainRobotDrive);

		driveTrainRobotDrive.setSafetyEnabled(true);
		driveTrainRobotDrive.setExpiration(0.1);
		// driveTrainRobotDrive.setSensitivity(0.5);//TODO Why aren't we setting sensitivity?
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
		driveTrainSingleSolenoid = new Solenoid(0, 0);
		LiveWindow.addActuator("DriveTrain", "Solenoid 1", driveTrainSingleSolenoid);

		/*
		 * Testing the Talon motor controller
		 */
		srxElevator = new TalonSRX(10);		// CAN bus slot 10
		srxElevator.setInverted(false);

		//  TODO:  Add better comments about port numbering
		//  In the 2018 robot, we have motor controllers mounted on each side of the robot (Port and Starboard)
		//  The Port motor controllers are numbered 1-5, Starboard 7-11 and 6 (one fewer controller on the Starboard side)
		//  TODO:  verify the numbering
		//  In general, for a task that has motors on both sides (like drive train and elevator), the starboard side number is (Port Number + 6)
		//	e.g. Port motors are ports 1 and 2, starboard motors are ports 7 and 8 (n+6)
		// The intake motors are an anomaly because the wire for the intake motors just goes up one side of the
		// elevator carriage, so both controllers are on the same side of the robot (ports 5 and 6)
		// Note:  all of the VictorSPX controllers are under CAN bus control, so we use 1-based addressing, reserving
		// port 0 on the CAN bus for new controllers / uninitialized controllers
		elevatorSpeedControllerPort = new VictorSPX(3);
		elevatorSpeedControllerPort.set(ControlMode.Follower, srxElevator.getDeviceID());
		elevatorSpeedControllerPort.setInverted(false);	// Motors on the Port side spin forward
		elevatorSpeedControllerStarboard = new VictorSPX(9);
		elevatorSpeedControllerStarboard.set(ControlMode.Follower, srxElevator.getDeviceID());
		elevatorSpeedControllerStarboard.setInverted(false);	// Motors on the Starboard side spin backward

		elevatorSpeedControllerPortFollow = new VictorSPX(4);
		elevatorSpeedControllerPortFollow.set(ControlMode.Follower, srxElevator.getDeviceID());
		elevatorSpeedControllerPortFollow.setInverted(false);

		/*
		 * Talon motor controller for Arm
		 */
		srxArm = new TalonSRX(12);		// CAN bus slot 12
		srxArm.setInverted(false);

		intakeSpeedController = new WPI_VictorSPX(11);	//  Competition bot changed from 5 to 11
		LiveWindow.addActuator("Intake", "Speed Controller 1", (WPI_VictorSPX) intakeSpeedController);
		intakeSpeedController.setInverted(false);
		intakeSpeedControllerFollow = new VictorSPX(6);
		intakeSpeedControllerFollow.set(ControlMode.Follower, intakeSpeedController.getDeviceID());
		intakeSpeedControllerFollow.setInverted(true);

		armSingleSolenoid = new Solenoid(0,1);
		elevatorRatchet = new Solenoid(0,2);
		climbingBar = new Solenoid(0,4);
		// END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
	}
}
