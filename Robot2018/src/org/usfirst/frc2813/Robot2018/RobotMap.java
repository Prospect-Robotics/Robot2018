// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
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
	/* Solenoid Left Summary
	 * 0 Gear Shift
	 * 1 Jaws
	 * 2 Ratchet
	 * 3 Climbing Bar 
	 */
	public final static int SOLENOID_MODULE_ID = 0;
	public final static int SOLENOID_ID_GearShift = 0;
	public final static int SOLENOID_ID_Jaws = 1;
	public final static int SOLENOID_ID_Ratchet = 2;
	public final static int SOLENOID_ID_ClimbingBar = 3;

	/* CAN BUS PORT SUMMARY
	 * (1)  WPI_VictorSPX DriveTrain Left Master
	 * (2)  VictorSPX DriveTrain Left Follower
	 * (3)  VictorSPX Elevator Left Follower
	 * (4)  VictorSPX Elevator Left Follower
	 * (5)  VictorSPX Arm Follower
	 * (6)  VictorSPX Intake Follower
	 * (7)  WPI_VictorSPX DriveTrain Right Master
	 * (8)  VictorSPX DriveTrain Right Follower
	 * (9)  VictorSPX Elevator Right Follower
	 * (10) TalonSRX Elevator Right Master
	 * (11) WPI_VictorSPX Intake Master
	 * (12) TalonSRX Arm
	 */
	public final static int CAN_ID_DriveTrain_Left_Master    = 1;
	public final static int CAN_ID_DriveTrain_Left_Follower  = 2;
	public final static int CAN_ID_Elevator_Left_Follower_1  = 3;
	public final static int CAN_ID_Elevator_Left_Follower_2  = 4;
	public final static int CAN_ID_Arm_Follower              = 9; // 5;
	public final static int CAN_ID_Intake_Master             = 6;
	public final static int CAN_ID_DriveTrain_Right_Master   = 7;
	public final static int CAN_ID_DriveTrain_Right_Follower = 8;
	public final static int CAN_ID_Elevator_Right_Follower   = 11; // 9;
	public final static int CAN_ID_Elevator_Right_Master     = 10;
	public final static int CAN_ID_Intake_Follower           = 5;  //11;
	public final static int CAN_ID_Arm_Master                = 12;
	

	/* 
	PDP PORT SUMMARY

	* 0 DriveTrain Left Master (CAN 1)       15 DriveTrain Right Master (CAN 7)
	* 1 DriveTrain Left Follower (CAN 2)	 14 DriveTrain Right Follower (CAN 8)
	* 2 Elevator Left Follower (CAN 3)       13 Elevator Right Follower (CAN 9)
	* 3 Elevator Left Follower (CAN 4)       12 Elevator Right Master (CAN 10)
	* 4 Arm Follower (CAN 5)                 11 Intake Master (CAN 11)
	* 5 Arm Master (CAN 12)                  10 Intake Follower (CAN 6)
	* 6 N/C                                   9 N/C 
	* 7 N/C                                   8 N/C
	*/
	public static final int PDP_PORT_DriveTrain_Left_Master = 0;
	public static final int PDP_PORT_DriveTrain_Left_Follower = 1;
	public static final int PDP_PORT_Elevator_Left_Follower1 = 2;
	public static final int PDP_PORT_Elevator_Left_Follower2 = 3;
	public static final int PDP_PORT_Arm_Follower = 4;
	public static final int PDP_PORT_Arm_Master = 5;
	public static final int PDP_PORT_DriveTrain_Right_Master = 15;
	public static final int PDP_PORT_DriveTrain_Right_Follower = 14;
	public static final int PDP_PORT_Elevator_Right_Follower = 13;
	public static final int PDP_PORT_Elevator_Right_Master = 12;
	public static final int PDP_PORT_Intake_Master = 11;
	public static final int PDP_PORT_Intake_Follower = 10;
	/*
	 *  Drive Train Subsystem
	 */
	public static WPI_TalonSRX driveTrainSpeedControllerLeftMaster;
	public static WPI_TalonSRX driveTrainSpeedControllerRightMaster;
	public static VictorSPX driveTrainSpeedControllerLeftFollower;
	public static VictorSPX driveTrainSpeedControllerRightFollower;
	public static DifferentialDrive driveTrainRobotDrive;
	public static Encoder driveTrainRightEncoder;
	public static Encoder driveTrainLeftEncoder;
	public static Solenoid gearShiftSolenoid;
	public static Solenoid jawsSolenoid;
	
	public static PowerDistributionPanel pdp;

	/*
	 * Elevator Subsystem
	 */
	public static TalonSRX elevatorSpeedControllerRightMaster;                     // Elevator motor controller
	public static Solenoid ratchetSolenoid;
	public static Solenoid climbingBarSolenoid;
	public static VictorSPX elevatorSpeedControllerLeftFollower1;	//  The WPI_VictorSPX type is used for the master elevator motor - others will follow this one
															//  TODO:  document WHY the WPI_VictorSPX type and the VictorSPX type are different
	public static VictorSPX elevatorSpeedControllerRightFollower;		//  Controller Right will follow controller Left; For followers, use the VictorSPX type rather than the WPI_VictorSPX type
	public static VictorSPX elevatorSpeedControllerLeftFollower2;		//  Controller LeftFollow is the paired motor with controller Left; it will follow 1

	/*
	 * Arm Subsystem. This subsystem includes the arm, intake and jaws
	 */
	public static TalonSRX armSpeedControllerMaster;                         // Arm motor controller
	public static WPI_VictorSPX armSpeedControllerFollower;
	public static WPI_TalonSRX intakeSpeedControllerMaster;
	public static VictorSPX intakeSpeedControllerFollower;

	/**
	 *  this is our single compressor. We use this for
	 *   - shifting gears
	 *   - engage/disengage climbing bar
	 *   - jaws on arms
	 *   - elevator lockout ratchet
	 */
	public static Compressor compressor;
	/*
	 * Called from Robot constructor before subsystems are constructed.
	 * Construct low level objects for the subsystems such as motor controllers
	 * and solenoids.
	 */
	@SuppressWarnings("deprecation")
	public static void init() {

		// PDP
		pdp = new PowerDistributionPanel(); 
		/*
		 * Drive Train subsystem
		 */
		driveTrainSpeedControllerLeftMaster = new WPI_TalonSRX(CAN_ID_DriveTrain_Left_Master);
		driveTrainSpeedControllerLeftMaster.setName("DriveTrain", "Left Motor");
		driveTrainSpeedControllerLeftMaster.setInverted(false);
		LiveWindow.add(driveTrainSpeedControllerLeftMaster);
		driveTrainSpeedControllerRightMaster = new WPI_TalonSRX(CAN_ID_DriveTrain_Right_Master);
		driveTrainSpeedControllerRightMaster.setName("DriveTrain", "Right Motor");
		driveTrainSpeedControllerRightMaster.setInverted(false);
		LiveWindow.add(driveTrainSpeedControllerRightMaster);

		driveTrainSpeedControllerLeftFollower = new VictorSPX(CAN_ID_DriveTrain_Left_Follower);
		driveTrainSpeedControllerLeftFollower.follow(driveTrainSpeedControllerLeftMaster );
		driveTrainSpeedControllerLeftFollower.setInverted(driveTrainSpeedControllerLeftMaster.getInverted());
		driveTrainSpeedControllerRightFollower = new VictorSPX(CAN_ID_DriveTrain_Right_Follower);
		driveTrainSpeedControllerRightFollower.follow(driveTrainSpeedControllerRightMaster);
		driveTrainSpeedControllerRightFollower.setInverted(false);
		driveTrainRobotDrive = new DifferentialDrive(driveTrainSpeedControllerLeftMaster, driveTrainSpeedControllerRightMaster);
		LiveWindow.add(driveTrainRobotDrive);

		driveTrainRightEncoder = new Encoder(10, 11, true, EncodingType.k4X);// port 0 2*0+10=10, port 0 2*0+11=11
		LiveWindow.addSensor("DriveTrain", "Right Encoder", driveTrainRightEncoder);
		
		driveTrainLeftEncoder = new Encoder(12, 13, false, EncodingType.k4X);
		LiveWindow.addSensor("DriveTrain", "Left Encoder", driveTrainLeftEncoder);

		/*
		 * Elevator subsystem
		 */
		elevatorSpeedControllerRightMaster = new TalonSRX(CAN_ID_Elevator_Right_Master);		// CAN bus slot 10 (Talon is on the Right side)
		elevatorSpeedControllerRightMaster.setInverted(true);
		elevatorSpeedControllerRightFollower = new VictorSPX(CAN_ID_Elevator_Right_Follower);
		elevatorSpeedControllerRightFollower.follow(elevatorSpeedControllerRightMaster);
		// NB: When you are follower on the SAME SIDE do not invert again!  Output from master is already correct for it's side.  Invert only on the 'reverse' side.
		
		elevatorSpeedControllerLeftFollower1 = new VictorSPX(CAN_ID_Elevator_Left_Follower_1);
		elevatorSpeedControllerLeftFollower1.follow(elevatorSpeedControllerRightMaster);
		elevatorSpeedControllerLeftFollower1.setInverted(true); // NB: 
		elevatorSpeedControllerLeftFollower2 = new VictorSPX(CAN_ID_Elevator_Left_Follower_2);
		elevatorSpeedControllerLeftFollower2.follow(elevatorSpeedControllerRightMaster);
		elevatorSpeedControllerLeftFollower2.setInverted(true); // NB: Wired incorrectly on practice bot
		/*
		 * Arm Subsystem
		 */
		armSpeedControllerMaster = new TalonSRX(CAN_ID_Arm_Master);
		armSpeedControllerFollower = new WPI_VictorSPX(CAN_ID_Arm_Follower);
		armSpeedControllerFollower.setInverted(false); // NB: Handled in wiring
		armSpeedControllerFollower.follow(armSpeedControllerMaster);
		/*
		 * Intake Subsystem
		 */
		intakeSpeedControllerMaster = new WPI_TalonSRX(CAN_ID_Intake_Master);
		intakeSpeedControllerMaster.setInverted(true);
		intakeSpeedControllerFollower = new VictorSPX(CAN_ID_Intake_Follower);
		intakeSpeedControllerFollower.follow(intakeSpeedControllerMaster);
		intakeSpeedControllerFollower.setInverted(intakeSpeedControllerMaster.getInverted() ? false : true);
		/*
		 * Compressor
		 */
		compressor = new Compressor();
		/*
		 * Solenoids
		 */
		gearShiftSolenoid = new Solenoid(SOLENOID_MODULE_ID, SOLENOID_ID_GearShift);
		jawsSolenoid = new Solenoid(SOLENOID_MODULE_ID,SOLENOID_ID_Jaws);
		ratchetSolenoid = new Solenoid(SOLENOID_MODULE_ID,SOLENOID_ID_Ratchet);
		climbingBarSolenoid = new Solenoid(SOLENOID_MODULE_ID,SOLENOID_ID_ClimbingBar);
	}
}
