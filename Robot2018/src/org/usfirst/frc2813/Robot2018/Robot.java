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

import org.usfirst.frc2813.Robot2018.commands.AutonomousCommand;
import org.usfirst.frc2813.Robot2018.commands.post.POST;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;
import org.usfirst.frc2813.Robot2018.subsystems.DriveTrain;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;
import org.usfirst.frc2813.Robot2018.subsystems.Intake;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
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

    Command autonomousCommand;

    public static OI oi;
    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public static DriveTrain driveTrain;
    public static Elevator elevator;
    public static Intake intake;
    public static Arm arm;
    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;
    private static String gameData = DriverStation.getInstance().getGameSpecificMessage();
    public static UsbCamera camera;
    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    //@Override
    public void robotInit() {
		System.out.println("In robotInit");      
    	
    	RobotMap.init();
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        driveTrain = new DriveTrain();
        elevator = new Elevator();
        intake = new Intake();
        arm = new Arm();

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
        // OI must be constructed after subsystems. If the OI creates Commands
        //(which it very likely will), subsystems are not guaranteed to be
        // constructed yet. Thus, their requires() statements may grab null
        // pointers. Bad news. Don't move it.
        oi = new OI();
        // Add commands to Autonomous Sendable Chooser
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS
        
        
        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=AUTONOMOUS
        
        // initialize the camera
       // camera = CameraServer.getInstance().startAutomaticCapture();
       // camera.setResolution(640, 480);
    }

    /**
     * This function is called when the disabled button is hit.
     * You can use it to reset subsystems before shutting down.
     */
    @Override
    public void disabledInit(){
    	driveTrain.setBrakeCoast(NeutralMode.Coast);
    }

    @Override
    public void disabledPeriodic() {
        Scheduler.getInstance().run();
    }

    //@Override
    public void autonomousInit() {
    	driveTrain.setBrakeCoast(NeutralMode.Brake);
    	System.out.println("Autonomous Init");
    	autonomousCommand = new AutonomousCommand();
        // schedule the autonomous to be run after POST completes  (example)
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
        driveTrain.setBrakeCoast(NeutralMode.Brake);
        new POST().start(); // will do nothing of POST already ran.
    }

    /**
     * This function is called periodically during operator control
     */
    //@Override
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
    }
}
