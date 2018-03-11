// RobotBuilder Version: 2.0
package org.usfirst.frc2813.Robot2018;

import java.util.logging.Level;

import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroup;
import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroupGenerator;
import org.usfirst.frc2813.Robot2018.commands.post.POST;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;
import org.usfirst.frc2813.Robot2018.subsystems.Intake;
import org.usfirst.frc2813.Robot2018.subsystems.Jaws;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in
 * the project.
 */
public class Robot extends TimedRobot {
	public static final ADXRS450_Gyro gyro = new ADXRS450_Gyro();//Model # of gyro connected

	public static AutonomousCommandGroup autonomousCommand;
	public static AutonomousCommandGroupGenerator autoCmdGenerator;

	public static OI oi;
	public static DriveTrain driveTrain;
	public static Elevator elevator;
	public static Arm arm;
	public static Jaws jaws;
	public static Intake intake;
	public static UsbCamera camera;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	//@Override
	public void robotInit() {
		System.out.println("In robotInit");

		RobotMap.init();
		driveTrain = new DriveTrain();
		elevator = new Elevator();
		arm = new Arm();
		jaws = new Jaws();
		intake = new Intake();

		// OI must be constructed after subsystems. If the OI creates Commands
		//(which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();
		// Add commands to Autonomous Sendable Chooser

		// initialize the camera
		camera = CameraServer.getInstance().startAutomaticCapture();
		camera.setResolution(640, 480);
	}

	/**
	 * This function is called when the disabled button is hit.
	 * You can use it to reset subsystems before shutting down.
	 */
	@Override
	public void disabledInit(){
		RobotMap.driveTrainSpeedControllerStarboard.setNeutralMode(NeutralMode.Coast);
		RobotMap.driveTrainSpeedControllerPort.setNeutralMode(NeutralMode.Coast);
		RobotMap.driveTrainSpeedControllerStarFollow.setNeutralMode(NeutralMode.Coast);
		RobotMap.driveTrainSpeedControllerPortFollow.setNeutralMode(NeutralMode.Coast);
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	//@Override
	public void autonomousInit() {
		System.out.println("Autonomous Init");
		autonomousCommand = new AutonomousCommandGroup();
		autoCmdGenerator = new AutonomousCommandGroupGenerator();

		RobotMap.driveTrainSpeedControllerStarboard.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerPort.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerStarFollow.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerPortFollow.setNeutralMode(NeutralMode.Brake);
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
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null) autonomousCommand.cancel();
		RobotMap.driveTrainSpeedControllerStarboard.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerPort.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerStarFollow.setNeutralMode(NeutralMode.Brake);
		RobotMap.driveTrainSpeedControllerPortFollow.setNeutralMode(NeutralMode.Brake);
		new POST().start();
	}

	/**
	 * This function is called periodically during operator control
	 */
	//@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
	
	public void setLevel(Level l){
		System.setProperty("java.util.logging.ConsoleHandler.level", l.toString());
	}
}
