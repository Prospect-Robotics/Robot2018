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

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.commands.ClimbingBar;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorRatchet;
import org.usfirst.frc2813.Robot2018.commands.Elevator.FloorElevator;
import org.usfirst.frc2813.Robot2018.commands.Elevator.SetSpeed;
import org.usfirst.frc2813.Robot2018.commands.ToggleCompressor;
import org.usfirst.frc2813.Robot2018.commands.Arm.ArmSolenoid;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;
import org.usfirst.frc2813.Robot2018.commands.Arm.SpinIntake;
import org.usfirst.frc2813.Robot2018.commands.AutoDrive.AutonomousCommand;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.OIDrive;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ResetEncoders;
import org.usfirst.frc2813.Robot2018.commands.DriveTrain.ShiftGears;
import org.usfirst.frc2813.Robot2018.subsystems.RoboRIOUserButton;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * This class is the glue that binds the controls on the physical operator 
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    // Joystick stick = new Joystick(port);
    // Button button = new JoystickButton(stick, buttonNumber);

    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.

    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:

    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());

    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());

    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenReleased(new ExampleCommand());


    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS
    public JoystickButton trigger;
    public Joystick joystick1, joystick2, buttonPanel;
    public JoystickButton spinIntakeIn, spinIntakeOut,
    					  bottomElevatorToLimitSwitch, elevatorUp, elevatorDown, elevatorRatchet,
    					  shiftGears, climbingBar,
    					  armUpToLimitSwitch, armDownToLimitSwitch, 
    					  armMoveUp, armMoveDown, armSolenoid;
	public final SendableChooser<BiConsumer<Joystick, Joystick>> driveStyleChooser;

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    public OI() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    	/*
    	 * 
    	 */
        buttonPanel = new Joystick(0);
        
        elevatorRatchet = new JoystickButton(buttonPanel, 8);
        elevatorRatchet.whenPressed(new ElevatorRatchet());
        climbingBar = new JoystickButton(buttonPanel, 6);
        climbingBar.whenPressed(new ClimbingBar());
        shiftGears = new JoystickButton(buttonPanel, 7);
        shiftGears.whenPressed(new ShiftGears());
        //elevatorDown.whenPressed(new PrintButtonStatus(true, false));
        elevatorDown = new JoystickButton(buttonPanel, 5);
        elevatorDown.whileHeld(new SetSpeed(-1));		// units of inches per second!
        //elevatorDown.whenReleased(new SetSpeed(0));
        //elevatorDown.whenReleased(new PrintButtonStatus(false, false));
        elevatorUp = new JoystickButton(buttonPanel, 4);
        //elevatorUp.whenPressed(new PrintButtonStatus(true, true));
        elevatorUp.whileHeld(new SetSpeed(1));
        //elevatorUp.whenReleased(new SetSpeed(0));
        //elevatorUp.whenReleased(new PrintButtonStatus(false, true));
        bottomElevatorToLimitSwitch = new JoystickButton(buttonPanel, 3);
        bottomElevatorToLimitSwitch.whenPressed(new FloorElevator());
        spinIntakeOut = new JoystickButton(buttonPanel, 2);
        spinIntakeOut.whileHeld(new SpinIntake(false, false));
        spinIntakeOut.whenReleased(new SpinIntake(false, true));
        spinIntakeIn = new JoystickButton(buttonPanel, 1);
        spinIntakeIn.whileHeld(new SpinIntake(true, false));
        spinIntakeIn.whenReleased(new SpinIntake(true, true));
        //new JoystickButton(buttonPanel, 9).whenPressed(new ArmLimitSwitch(true));
        //new JoystickButton(buttonPanel, 10).whenPressed(new ArmLimitSwitch(false));
        armMoveUp = new JoystickButton(buttonPanel, 11);
        armMoveUp.whileHeld(new MoveArm(true));
        armMoveDown = new JoystickButton(buttonPanel, 12);
        armMoveDown.whileHeld(new MoveArm(false));
        armSolenoid = new JoystickButton(buttonPanel, 9);
        armSolenoid.whenPressed(new ArmSolenoid());
        joystick1 = new Joystick(1);
        joystick2 = new Joystick(2);
        
        trigger = new JoystickButton(joystick1, 1);
        trigger.whileHeld(new ResetEncoders());
        
        Compressor compressor = new Compressor();
        LiveWindow.add(compressor);
        new RoboRIOUserButton().whenPressed(new ToggleCompressor(compressor));


        // SmartDashboard Buttons
        SmartDashboard.putData("OIDrive", new OIDrive());
        SmartDashboard.putData("Autonomous Command", new AutonomousCommand());
        SmartDashboard.putData("ToggleRelay0", new ShiftGears());
        //SmartDashboard.putData("ToggleRelay1", new ToggleRelay1());
        //SmartDashboard.putData("ToggleRelay2", new ToggleRelay2());
        //SmartDashboard.putData("SpinIntakeIn", new SpinIntake(true));
        //SmartDashboard.putData("SpinIntakeOut", new SpinIntake(false));
        SmartDashboard.putData("BottomElevator", new FloorElevator());
        //SmartDashboard.putData("MoveElevatorUp", new MoveElevator(true));
        //SmartDashboard.putData("MoveElevatorDown", new MoveElevator(false));
        
        driveStyleChooser = new SendableChooser<>();
        driveStyleChooser.addDefault("Arcade drive", Robot.driveTrain::arcadeDrive);
        driveStyleChooser.addObject("Tank drive", Robot.driveTrain::tankDrive);
        

        // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
    public Joystick getJoystick1() {
        return joystick1;
    }
    public Joystick getJoystick2() {
    	return joystick2;
    }

    public Joystick getbuttonPanel() {
        return buttonPanel;
    }


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=FUNCTIONS
}

