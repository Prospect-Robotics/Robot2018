// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
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

	/*
	 *  Drive Train Subsystem
	 */
	public static WPI_VictorSPX driveTrainSpeedControllerPort;// Port Motor
	public static WPI_VictorSPX driveTrainSpeedControllerStarboard;// Starboard Motor
	public static VictorSPX driveTrainSpeedControllerPortFollow;
	public static VictorSPX driveTrainSpeedControllerStarFollow;
	public static DifferentialDrive driveTrainRobotDrive;
	//TODO verify if encoders are correctly named port/starboard
	public static Encoder driveTrainQuadratureEncoderStarboard;// Starboard Motor
	public static Encoder driveTrainQuadratureEncoderPort;// Port Motor
	public static Solenoid driveTrainGearShiftSolenoid;
	public static Solenoid jawsSolenoid;

	/*
	 * Elevator Subsystem
	 */
	public static TalonSRX srxElevator;                     // Elevator motor controller
	public static Solenoid elevatorRatchet;
	public static Solenoid climbingBar;
	public static VictorSPX elevatorSpeedControllerPort;	//  The WPI_VictorSPX type is used for the master elevator motor - others will follow this one
															//  TODO:  document WHY the WPI_VictorSPX type and the VictorSPX type are different
	public static VictorSPX elevatorSpeedControllerStarboard;		//  Controller Starboard will follow controller Port; For followers, use the VictorSPX type rather than the WPI_VictorSPX type
	public static VictorSPX elevatorSpeedControllerPortFollow;		//  Controller PortFollow is the paired motor with controller Port; it will follow 1
	
	/*
	 * Arm Subsystem. This subsystem includes the arm, intake and jaws
	 */
	public static TalonSRX srxArm;                         // Arm motor controller
	public static WPI_VictorSPX intakeSpeedController;
	public static VictorSPX intakeSpeedControllerFollow;
	//public static Encoder armQuadratureEncoder;
	//public static DigitalInput armLimitSwitch;

	/*
	 * Called from Robot constructor before subsystems are constructed.
	 * Construct low level objects for the subsystems such as motor controllers
	 * and solenoids.
	 */
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

		/*
		 * Drive Train subsystem
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

		driveTrainQuadratureEncoderStarboard = new Encoder(10, 11, true, EncodingType.k4X);// port 0 2*0+10=10, port 0 2*0+11=11
		LiveWindow.addSensor("DriveTrain", "Starboard Encoder", driveTrainQuadratureEncoderStarboard);
		
		driveTrainQuadratureEncoderPort = new Encoder(12, 13, true, EncodingType.k4X);
		LiveWindow.addSensor("DriveTrain", "Port Encoder", driveTrainQuadratureEncoderPort);
		//LiveWindow.addActuator("DriveTrain", "Solenoid 1", driveTrainGearShiftSolenoid);

		/*
		 * Elevator subsystem
		 */
		srxElevator = new TalonSRX(10);		// CAN bus slot 10 (Talon is on the Starboard side)

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
		/*
		 * A note on Following:
		 * The elevator is controlled by one TalonSRX motor controller with four VictorSPX motor controllers following.
		 * Typically, when you have motors on opposite sides of the robot, the motor on the far side will need to spin
		 * in the opposite direction of the motor on the close side.
		 * We have had issues getting both following and setInverted to work.  Rather than trying to debug this, the
		 * ROBOT HAS BEEN WIRED SO THAT THE MOTORS ON THE PORT SIDE ARE OPPOSITE OF THE MOTORS ON THE STARBOARD SIDE.
		 * Therefore, we don't need to setInverted on the far side motors, but need to be very careful to ensure
		 * that the physical wiring is correct! 
		 */
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
		/*
		 * Arm Subsystem
		 */
		srxArm = new TalonSRX(12);		// CAN bus slot 12

		intakeSpeedController = new WPI_VictorSPX(11);	//  Competition bot changed from 5 to 11
		//LiveWindow.addActuator("Intake", "Speed Controller 1", intakeSpeedController);
		
		// FIXME move to subsystem
		intakeSpeedController.setInverted(false);
		intakeSpeedControllerFollow = new VictorSPX(6);
		intakeSpeedControllerFollow.set(ControlMode.Follower, intakeSpeedController.getDeviceID());
		intakeSpeedControllerFollow.setInverted(true);

		jawsSolenoid = new Solenoid(0,1);
		elevatorRatchet = new Solenoid(0,2);
		climbingBar = new Solenoid(0,4);
	}
}
