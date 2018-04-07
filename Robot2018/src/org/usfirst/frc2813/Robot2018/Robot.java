// RobotBuilder Version: 2.0
package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroup;
import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroupGenerator;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainPrintEncoderValues;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainPrintPowerLevels;
import org.usfirst.frc2813.Robot2018.commands.post.POST;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.intake.Intake;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ElevatorConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.ClimbingBarConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.GearShiftConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.JawsConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.RatchetConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
//import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in
 * the project.
 */
public class Robot extends TimedRobot {
	/**
	 *  This is the FIRST interface to read the state of the field pieces.
	 */
	public static GameData gameData;

	/**
	 *  This is the sendable choose we use to read the robot start position from
	 *  the drive station.
	 */
	public static final SendableChooser<Direction> positionSelector = new SendableChooser<>();
	static {
		Logger.info("Autonomous Position Selector Creation");
		positionSelector.addDefault("LEFT", Direction.LEFT);
		positionSelector.addObject("CENTER", Direction.CENTER);
		positionSelector.addObject("RIGHT", Direction.RIGHT);
	}
	public static final SendableChooser<Direction> autonomousSelectorCurves = new SendableChooser<>();
	static {
		Logger.info("Autonomous strategy Option: CURVED PATHING");
		autonomousSelectorCurves.addDefault("Curves", Direction.ON);
		autonomousSelectorCurves.addDefault("Angles", Direction.OFF);
	}

	public static final ADXRS450_Gyro gyro = new ADXRS450_Gyro();//Model # of gyro connected

	public static AutonomousCommandGroup autonomousCommand;
	public static AutonomousCommandGroupGenerator autoCmdGenerator;

	public static OI oi;
	public static DriveTrain driveTrain;
	public static Motor elevator;
	public static Motor arm;
	public static Intake intake;
	public static Solenoid jaws;
//	public static UsbCamera camera;
	public static Solenoid gearShifter;
	public static Solenoid ratchet;
	public static Solenoid climbingBar;

	/**
	 * Officially documenting our logical model for the robot.  LEFT is negative.
	 * This is consistent with a compass and a gyro, and has nothing to do with any input device axis.
	 */
	public static final double LEFT_BIAS = -1.0;
	/**
	 * Officially documenting our logical model for the robot.  RIGHT is positive.
	 * This is consistent with a compass and a gyro, and has nothing to do with any input device axis.
	 */
	public static final double RIGHT_BIAS = 1.0;
	/**
	 * Officially documenting our logical model for the robot.  FORWARD is positive.
	 */
	public static final double FORWARD_BIAS = 1.0;
	/**
	 * Officially documenting our logical model for the robot.  REVERSE is negative.
	 */
	public static final double REVERSE_BIAS = -1.0;
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	//@Override
	public void robotInit() {
		Logger.info("In robotInit");

		RobotMap.init();
		driveTrain = new DriveTrain(gyro);
		elevator = new Motor(new ElevatorConfiguration(), RobotMap.elevatorSpeedControllerRightMaster);
		arm = new Motor(new ArmConfiguration(), RobotMap.armSpeedControllerMaster);
		intake = new Intake(RobotMap.intakeSpeedControllerMaster);
		jaws = new Solenoid(new JawsConfiguration(), RobotMap.jawsSolenoid);
		ratchet = new Solenoid(new RatchetConfiguration(), RobotMap.ratchetSolenoid);
		climbingBar = new Solenoid(new ClimbingBarConfiguration(), RobotMap.climbingBarSolenoid);
		gearShifter = new Solenoid(new GearShiftConfiguration(), RobotMap.gearShiftSolenoid);

		// Ask the Gearheads what position the robot is in
		SmartDashboard.putData("Which position is the robot in?", positionSelector);

		// As the Gearheads how they want autonomous to work
		SmartDashboard.putData("Do you want autonomous to use curves?", autonomousSelectorCurves);

		// OI must be constructed after subsystems. If the OI creates Commands
		//(which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();
		// Add commands to Autonomous Sendable Chooser

		// initialize the camera
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);
		camera.setFPS(15);
		new DriveTrainPrintEncoderValues(Robot.driveTrain, 2000).start();
//		new DriveTrainPrintPowerLevels(Robot.driveTrain, 2000).start();
	}

	/**
	 * This function is called when the disabled button is hit.
	 * You can use it to reset subsystems before shutting down.
	 */
	@Override
	public void disabledInit(){
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
			autonomousCommand = null;
		}

		driveTrain.setBrakeCoast(NeutralMode.Coast);
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	//@Override
	public void autonomousInit() {
		Logger.info("Autonomous Init");
		driveTrain.setBrakeCoast(NeutralMode.Brake);
		driveTrain.setGearShift(Direction.LOW_GEAR);
		// Get the game setup data from the driver station
		gameData = new GameData(DriverStation.getInstance().getGameSpecificMessage());

		autonomousCommand = new AutonomousCommandGroup();
		autoCmdGenerator = new AutonomousCommandGroupGenerator();
		new POST(autonomousCommand).start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	//@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	//@Override
	public void teleopInit() {
		Logger.info("teleopInit STARTED");
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
			autonomousCommand = null;
		}
		driveTrain.setBrakeCoast(NeutralMode.Brake);
		new POST().start();
	}

	/**
	 * This function is called periodically during operator control
	 */
	//@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	public void robotPeriodic() {
		// Complain no more!
	}
}
