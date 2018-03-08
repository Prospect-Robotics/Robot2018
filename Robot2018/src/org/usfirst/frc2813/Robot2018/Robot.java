// RobotBuilder Version: 2.0
package org.usfirst.frc2813.Robot2018;

//TODO: move to commandGroups
import org.usfirst.frc2813.Robot2018.commands.Auto.AutonomousCommandGroup;

import org.usfirst.frc2813.Robot2018.commands.Auto.AutonomousCommandGroupGenerator;

import org.usfirst.frc2813.Robot2018.commands.post.POST;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
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
	public static UsbCamera camera;
	public static int startAbsolutePositionElevator;
	public static int startRelativePositionElevator;
	public static int startAbsolutePositionArm;
	public static int startRelativePositionArm;


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

		// OI must be constructed after subsystems. If the OI creates Commands
		//(which it very likely will), subsystems are not guaranteed to be
		// constructed yet. Thus, their requires() statements may grab null
		// pointers. Bad news. Don't move it.
		oi = new OI();
		// Add commands to Autonomous Sendable Chooser

		// initialize the camera
		//camera = CameraServer.getInstance().startAutomaticCapture();
		//camera.setResolution(640, 480);


		/*
		 * Test Talon PID code from Team 217
		 */

		/* choose the sensor and sensor direction */
		RobotMap.srxElevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, Constants.maintainPIDLoopIdx,
				Constants.kTimeoutMs);

		startAbsolutePositionElevator = RobotMap.srxElevator.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);

		RobotMap.srxElevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.maintainPIDLoopIdx,
				Constants.kTimeoutMs);

		startRelativePositionElevator = RobotMap.srxElevator.getSelectedSensorPosition(Talon.PRIMARY_CLOSED_LOOP_SENSOR);

		/* choose to ensure sensor is positive when output is positive */
		RobotMap.srxElevator.setSensorPhase(Constants.kSensorPhase);

		/* choose based on what direction you want forward/positive to be.
		 * This does not affect sensor phase. */
		RobotMap.srxElevator.setInverted(Constants.kMotorInvert);

		/* set the peak and nominal outputs, 12V means full */
		RobotMap.srxElevator.configNominalOutputForward(0, Constants.kTimeoutMs);
		RobotMap.srxElevator.configNominalOutputReverse(0, Constants.kTimeoutMs);
		RobotMap.srxElevator.configPeakOutputForward(1, Constants.kTimeoutMs);
		RobotMap.srxElevator.configPeakOutputReverse(-1, Constants.kTimeoutMs);

		/*
		 * set the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table in Section 17.2.1 for native
		 * units per rotation.
		 */
		RobotMap.srxElevator.configAllowableClosedloopError(0, Constants.maintainPIDLoopIdx, Constants.kTimeoutMs);

		/* set closed loop gains in slot0, typically kF stays zero. */
		RobotMap.srxElevator.config_kF(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kP(Constants.maintainPIDLoopIdx, 0.8, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kI(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kD(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);

		RobotMap.srxElevator.config_kF(Constants.movePIDLoopIdx, 0.0, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kP(Constants.movePIDLoopIdx, 0.75, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kI(Constants.movePIDLoopIdx, 0.01, Constants.kTimeoutMs);
		RobotMap.srxElevator.config_kD(Constants.movePIDLoopIdx, 40.0, Constants.kTimeoutMs);
		/*
		 * Test Talon PID code from Team 217
		 */

		/* choose the sensor and sensor direction */
		RobotMap.srxArm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.maintainPIDLoopIdx,
				Constants.kTimeoutMs);

		/* choose to ensure sensor is positive when output is positive */
		RobotMap.srxArm.setSensorPhase(Constants.kSensorPhase);

		/* choose based on what direction you want forward/positive to be.
		 * This does not affect sensor phase. */
		RobotMap.srxArm.setInverted(Constants.kMotorInvert);

		/* set the peak and nominal outputs, 12V means full */
		RobotMap.srxArm.configNominalOutputForward(0, Constants.kTimeoutMs);
		RobotMap.srxArm.configNominalOutputReverse(0, Constants.kTimeoutMs);
		RobotMap.srxArm.configPeakOutputForward(1, Constants.kTimeoutMs);
		RobotMap.srxArm.configPeakOutputReverse(-1, Constants.kTimeoutMs);

		/*
		 * set the allowable closed-loop error, Closed-Loop output will be
		 * neutral within this range. See Table in Section 17.2.1 for native
		 * units per rotation.
		 */
		RobotMap.srxArm.configAllowableClosedloopError(0, Constants.maintainPIDLoopIdx, Constants.kTimeoutMs);

		/* set closed loop gains in slot0, typically kF stays zero. */
		RobotMap.srxArm.config_kF(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kP(Constants.maintainPIDLoopIdx, 0.1, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kI(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kD(Constants.maintainPIDLoopIdx, 0.0, Constants.kTimeoutMs);

		RobotMap.srxArm.config_kF(Constants.movePIDLoopIdx, 0, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kP(Constants.movePIDLoopIdx, 2, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kI(Constants.movePIDLoopIdx, 0, Constants.kTimeoutMs);
		RobotMap.srxArm.config_kD(Constants.movePIDLoopIdx, 0, Constants.kTimeoutMs);

		/*
//		 * lets grab the 360 degree position of the MagEncoder's absolute
//		 * position, and intitally set the relative sensor to match.
//		 */
		//		int absolutePosition = _talon.getSensorCollection().getPulseWidthPosition();
		//		/* mask out overflows, keep bottom 12 bits */
		//		absolutePosition &= 0xFFF;
		//		if (Constants.kSensorPhase)
		//			absolutePosition *= -1;
		//		if (Constants.kMotorInvert)
		//			absolutePosition *= -1;
		//		/* set the quadrature (relative) sensor to match absolute */
		//		_talon.setSelectedSensorPosition(absolutePosition, Constants.kPIDLoopIdx, Constants.kTimeoutMs);

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
}
