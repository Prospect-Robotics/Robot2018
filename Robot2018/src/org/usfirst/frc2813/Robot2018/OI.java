package org.usfirst.frc2813.Robot2018;

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.ToggleCompressor;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDrive;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSpin;
import org.usfirst.frc2813.Robot2018.commands.interlock.Unlock;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorCalibrateSensor;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorDisable;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveInDirection;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePosition;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToRelativePosition;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorPositionPIDTest;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorVelocityPIDTest;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSet;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggle;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemDisableDefaultCommand;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemRestoreDefaultCommand;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemSetDefaultCommand;
import org.usfirst.frc2813.Robot2018.interlock.IInterlock;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
import org.usfirst.frc2813.Robot2018.triggers.RoboRIOUserButton;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.TimedCommand;
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

	public Joystick joystick1, joystick2, buttonPanel;
	public final SendableChooser<BiConsumer<Joystick, Joystick>> driveStyleChooser;
	
	protected static void createMotorTestingButtons(Joystick buttonPanel)
	{
		CommandGroup armDemo = new CommandGroup();
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(30), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(45), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(60), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(75), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(90), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		armDemo.addSequential(new MotorMoveToAbsolutePosition(Robot.arm, ArmConfiguration.ArmDegrees.create(105), ArmConfiguration.ArmDegrees.create(5)));
		armDemo.addSequential(new TimedCommand(3));
		
		new JoystickButton(buttonPanel, 6).whileHeld(new MotorPositionPIDTest(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(15), Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(110)));
		new JoystickButton(buttonPanel, 7).whileHeld(new MotorPositionPIDTest(Robot.elevator, LengthUOM.Inches.create(5), LengthUOM.Inches.create(30)));
		//	new JoystickButton(buttonPanel, 8).whileHeld(new MotorVelocityPIDTest(Robot.arm));
		new JoystickButton(buttonPanel, 8).whenPressed(armDemo);
		new JoystickButton(buttonPanel, 9).whileHeld(new MotorVelocityPIDTest(Robot.elevator));
		new JoystickButton(buttonPanel, 9).whileHeld(new MotorMoveToAbsolutePosition(Robot.elevator, LengthUOM.Inches.create(6)));
	}
	
	protected CommandGroup createCalibrationSequence() {
		CommandGroup calibration = new CommandGroup();
		calibration.addSequential(new MotorCalibrateSensor(Robot.arm, Direction.REVERSE));
		calibration.addSequential(new MotorCalibrateSensor(Robot.elevator, Direction.REVERSE));
		return calibration;
	}
	
	/**
	 * When we are climbing, we are retracting the elevator - 
	 * elevator "down" climbs, and elevator "up" will break the robot because of the ratchet...
	 * 
	 * NOTE: Use Direction.ROBOT_CLIMB_UP and Direction.ROBOT_CLIMB_DOWN if this confuses you.
	 */
	protected static CommandGroup createClimbSequence() {
		CommandGroup climbSequence = new CommandGroup();
		SubsystemCommand<Motor> elevatorClimb = new MotorMoveToAbsolutePosition(
				Robot.elevator,
				LengthUOM.Inches.create(0.0), // move to 0.0 inches (full climbed).  This is not a bug.  Fully retracted elevator is a fully climbed robot. 
				LengthUOM.Inches.create(1.0), // tolerance +/- 1.0 inches
				CommandDuration.FOREVER, 
				Lockout.UntilUnlocked);
		/* Extend and lock the climbing bar. */
		climbSequence.addSequential(new SolenoidSet(Robot.climbingBar, Direction.OUT, CommandDuration.DISABLED, Lockout.UntilUnlocked)); 
		/* Should disable 'hold position' fighting us, and the ratchet! */
		climbSequence.addSequential(new SubsystemDisableDefaultCommand(Robot.elevator));   
	    /* Now immediately engage and lock the ratchet! */
		climbSequence.addSequential(new SolenoidSet(Robot.ratchet, Direction.ENGAGED, CommandDuration.DISABLED, Lockout.UntilUnlocked));
		/* Should disable 'hold position' fighting us */
		climbSequence.addSequential(new SubsystemDisableDefaultCommand(Robot.arm));        
        /* Arm should go limp */
		climbSequence.addSequential(new MotorDisable(Robot.arm, CommandDuration.DISABLED, Lockout.UntilUnlocked));
		
		/*NB: Currently we are going to go with manual elevator climb control..."down is up", "up" is interlocked. */
		// climbSequence.addSequential(elevatorClimb);            /* Should fully retract the elevator */

		return climbSequence;
	}

	/**
	 * When we are climbing, we are retracting the elevator - 
	 * elevator "down" climbs, and elevator "up" will break the robot because of the ratchet...
	 * 
	 * NOTE: Use Direction.ROBOT_CLIMB_UP and Direction.ROBOT_CLIMB_DOWN if this confuses you.
	 */
	protected static CommandGroup createClimbAbortSequence() {
		CommandGroup climbAbort = new CommandGroup();
		SubsystemCommand<Motor> elevatorClimbQuarterTurn = new MotorMoveToRelativePosition(
				Robot.elevator,
				Direction.ROBOT_CLIMB_UP, // down means climb... up will break the robot.  This is *NOT* a bug.
				Robot.elevator.getConfiguration().getNativeSensorLengthUOM().create(1024), /* NB: This is 1/4 turn, of encoder with 4096 pulses per revolution */
				Robot.elevator.getConfiguration().getNativeSensorLengthUOM().create(25), // tolerance +/- 25 pulses
				CommandDuration.FOREVER, 
				Lockout.UntilUnlocked);

		/* First we can unlock the climbing bar, if nobody is on it - it's not going to kill us to retract it. */
		climbAbort.addSequential(new Unlock(Robot.climbingBar));

		/* Unlock and re-enable holding for the arm */
		climbAbort.addSequential(new Unlock(Robot.arm));
		climbAbort.addSequential(new SubsystemRestoreDefaultCommand(Robot.arm));

		/* Unlock and reset the ratchet.  It won't be free until we do a 1/4 turn higher */
		climbAbort.addSequential(new Unlock(Robot.ratchet));
		climbAbort.addSequential(new SolenoidSet(Robot.ratchet, Direction.DISENGAGED));
		climbAbort.addSequential(elevatorClimbQuarterTurn); // This will disengage the ratchet if it's stuck 

		/* Lastly, unlock the Elevator */ 
		climbAbort.addSequential(new SubsystemRestoreDefaultCommand(Robot.elevator)); 

		return climbAbort;
	}

	@SuppressWarnings("unused")
	public OI() {
		/*
		 * BUTTON PANEL BUTTONS:
		 *
		 * (1)  SPIN INTAKE IN
		 * (2)  SPIN INTAKE OUT
		 * (3)  FLOOR ELEVATOR
		 * (4)  ELEVATOR DOWN
		 * (5)  ELEVATOR UP
		 * (6)  ***** START CLIMBING SEQUENCE
		 * (7)  SHIFT GEARS SOLENOID
		 * (8)  ***** ABORT CLIMBING SEQUENCE
		 * (9)  INTAKE/ARM SOLENOID
		 * (10) ARM/ELEVATOR CALIBRATION
		 * (11) MOVE ARM UP
		 * (12) MOVE ARM DOWN
		 *
		 */
		buttonPanel = new Joystick(0);

		new JoystickButton(buttonPanel, 1).whileHeld(new IntakeSpin(Robot.intake, Direction.IN));
		new JoystickButton(buttonPanel, 2).whileHeld(new IntakeSpin(Robot.intake, Direction.OUT));
		
		SubsystemCommand<Motor> elevatorUp = new MotorMoveInDirection(Robot.elevator, Direction.UP);
		// Add an interlock on elevatorUp so that it will not engage with the ratchet 
		elevatorUp.addInterlock(new IInterlock() {
			public boolean isSafeToOperate() {
				return Robot.ratchet.getTargetPosition().equals(Direction.DISENGAGED);
			}
		});
		new JoystickButton(buttonPanel, 4).whileHeld(new MotorMoveInDirection(Robot.elevator, Direction.DOWN));
		new JoystickButton(buttonPanel, 5).whileHeld(elevatorUp);
		if(true) { // NB: The other settings are for Mike's debugging of PID vales on Arm and Elevator and are disabled by default.		
				new JoystickButton(buttonPanel, 6).whenPressed(createClimbSequence());
				new JoystickButton(buttonPanel, 7).whenPressed(new SolenoidToggle(Robot.driveTrain.getGearShiftSolenoid()));
				new JoystickButton(buttonPanel, 8).whenPressed(createClimbAbortSequence());				
				new JoystickButton(buttonPanel, 9).whenPressed(new SolenoidToggle(Robot.jaws));
		} else {
			createMotorTestingButtons(buttonPanel);
		}
		new JoystickButton(buttonPanel, 10).whenPressed(createCalibrationSequence());
		new JoystickButton(buttonPanel, 11).whileHeld(new MotorMoveInDirection(Robot.arm, Direction.IN));
		new JoystickButton(buttonPanel, 12).whileHeld(new MotorMoveInDirection(Robot.arm, Direction.OUT));

		joystick1 = new Joystick(1);
		joystick2 = new Joystick(2);

		LiveWindow.add(RobotMap.compressor);
		new RoboRIOUserButton().whenPressed(new ToggleCompressor(RobotMap.compressor));

		// SmartDashboard Buttons
		SmartDashboard.putData("OIDrive", new DriveTrainOIDrive(Robot.driveTrain, joystick1, joystick2));

		driveStyleChooser = new SendableChooser<>();
		driveStyleChooser.addDefault("Arcade drive", Robot.driveTrain::arcadeDrive);
		driveStyleChooser.addObject("Tank drive", Robot.driveTrain::tankDrive);
	}

	public Joystick getJoystick1() {
		return joystick1;
	}
	public Joystick getJoystick2() {
		return joystick2;
	}
	public Joystick getbuttonPanel() {
		return buttonPanel;
	}
}

