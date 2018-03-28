package org.usfirst.frc2813.Robot2018;

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroupGenerator;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
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
import org.usfirst.frc2813.Robot2018.interlock.IInterlock;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.Robot2018.triggers.RoboRIOUserButton;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

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
	
	/*
	 * There are currently three sets of button definitions.  
	 * Standard - What we've been using all along, 
	 * MotorTesting - what I used for motor testing, 
	 * Competition - and what Jesse wants for competition.
	 */
	private enum ButtonLayout { Standard, MotorTesting, Competition };
	private static ButtonLayout buttonLayout = ButtonLayout.Standard;
	
	/**
	 * IF the position buttons 11 and 12 were supposed to move the ELEVATOR, change here..
	 */
	private static boolean POSITION_BUTTONS_INCLUDE_ELEVATOR = true;
	/**
	 * IF the position buttons 11 and 12 were supposed to move the ARM, change here..
	 */
	private static boolean POSITION_BUTTONS_INCLUDE_ARM = false;
	
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

	public final Joystick joystick1;
	public final Joystick joystick2;
	public final Joystick buttonPanel;
	public final SendableChooser<BiConsumer<Joystick, Joystick>> driveStyleChooser;
	
	public CommandGroup createArmDemoSequence() {
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
		return armDemo;
	}

	public Command createArmPositionPIDTest() {
		return new MotorPositionPIDTest(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(15), Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(110));
	}

	public Command createElevatorVelocityPIDTest() {
		return new MotorVelocityPIDTest(Robot.elevator);
	}
	public Command createElevatorPositionPIDTest() {
		return new MotorPositionPIDTest(Robot.elevator, LengthUOM.Inches.create(5), LengthUOM.Inches.create(30));
	}
	public Command createShootingPositionSequence() {
		CommandGroup group = new CommandGroup();
		if(POSITION_BUTTONS_INCLUDE_ELEVATOR) {
			group.addSequential(createElevatorToShootPosition());
		}
		if(POSITION_BUTTONS_INCLUDE_ARM) {
			group.addSequential(createArmToShootPosition());
		}
		return group;
	}
	public Command createPickUpPositionSequence() {
		CommandGroup group = new CommandGroup();
		if(POSITION_BUTTONS_INCLUDE_ELEVATOR) {
			group.addSequential(createElevatorToPickUpHeight());
		}
		if(POSITION_BUTTONS_INCLUDE_ARM) {
			group.addSequential(createArmToPickUpPosition());
		}
		return group;
	}
	public Command createArmToPickUpPosition() {
		return new MotorMoveToAbsolutePosition(Robot.arm, AutonomousCommandGroupGenerator.ARM_POSITION_GRAB_CUBE);
	}
	public Command createElevatorToPickUpHeight() {
		return new MotorMoveToAbsolutePosition(Robot.elevator, AutonomousCommandGroupGenerator.ELEVATOR_HEIGHT_GRAB_CUBE);
	}
	public Command createElevatorToShootPosition() {
		return new MotorMoveToAbsolutePosition(Robot.elevator, AutonomousCommandGroupGenerator.ELEVATOR_HEIGHT_SWITCH);
	}
	public Command createArmToShootPosition() {
		return new MotorMoveToAbsolutePosition(Robot.arm, AutonomousCommandGroupGenerator.ARM_POSITION_FORWARD_SHOOT);
	}
	public Command createElevatorCalibrate() {
		return new MotorCalibrateSensor(Robot.elevator, Direction.REVERSE);
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
				RunningInstructions.RUN_NORMALLY, 
				Lockout.UntilUnlocked);
		/* Extend and lock the climbing bar. */
		climbSequence.addSequential(new SolenoidSet(Robot.climbingBar, Direction.OUT, RunningInstructions.RUN_NORMALLY, Lockout.UntilUnlocked)); 
		/* Should disable 'hold position' fighting us, and the ratchet! */
		climbSequence.addSequential(new SubsystemDisableDefaultCommand(Robot.elevator));   
	    /* Now immediately engage and lock the ratchet! */
		climbSequence.addSequential(new SolenoidSet(Robot.ratchet, Direction.ENGAGED, RunningInstructions.RUN_NORMALLY, Lockout.UntilUnlocked));
		/* Should disable 'hold position' fighting us */
		climbSequence.addSequential(new SubsystemDisableDefaultCommand(Robot.arm));        
        /* Arm should go limp */
		climbSequence.addSequential(new MotorDisable(Robot.arm, RunningInstructions.RUN_NORMALLY, Lockout.UntilUnlocked));
		
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
				RunningInstructions.RUN_NORMALLY, 
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
	
	public Command createIntakeIn() {
		return new IntakeSpin(Robot.intake, Direction.IN);
	}
	
	public Command createIntakeOut() {
		return new IntakeSpin(Robot.intake, Direction.OUT);
	}
	
	public Command createElevatorUp() {
		SubsystemCommand<Motor> elevatorUp = new MotorMoveInDirection(Robot.elevator, Direction.UP);
		// Add an interlock on elevatorUp so that it will not engage with the ratchet 
		elevatorUp.addInterlock(new IInterlock() {
			public boolean isSafeToOperate() {
				return Robot.ratchet.getTargetPosition().equals(Direction.DISENGAGED);
			}
		});
		return elevatorUp;
	}
	
	public Command createElevatorDown() {
		return new MotorMoveInDirection(Robot.elevator, Direction.DOWN);
	}
	
	public Command createGearShiftToggle() {
		return new SolenoidToggle(Robot.driveTrain.getGearShiftSolenoid());
	}
	
	public Command createRobotJawsToggle() {
		return new SolenoidToggle(Robot.jaws);
	}
	
	public Command createArmIn() {
		return new MotorMoveInDirection(Robot.arm, Direction.IN);
	}
	
	public Command createArmOut() {
		return new MotorMoveInDirection(Robot.arm, Direction.OUT);
	}
	
	@SuppressWarnings("unused")
	public OI() {
		/*
		 * BUTTON PANEL BUTTONS:
		 * 
		 * STANDARD LAYOUT:
		 *
		 * (1)  SPIN INTAKE IN
		 * (2)  SPIN INTAKE OUT
		 * (3)  FLOOR ELEVATOR (calibrates too)
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
		 * MOTOR TESTING LAYOUT:
		 * 
		 * (1)  SPIN INTAKE IN
		 * (2)  SPIN INTAKE OUT
		 * (3)  FLOOR ELEVATOR (calibrates too)
		 * (4)  ELEVATOR DOWN
		 * (5)  ELEVATOR UP
		 * (6)  *** ARM POSITION PID TEST
		 * (7)  *** ELEVATOR POSITION PID TEST
		 * (8)  *** ARM DEMO SEQUENCE
		 * (9)  ELEVATOR TO PICKUP HEIGHT
		 * (10) ARM/ELEVATOR CALIBRATION
		 * (11) MOVE ARM UP
		 * (12) MOVE ARM DOWN
		 *
		 * COMPETITION LAYOUT:
		 * 
		 * (1)  START CLIMBING
		 * (2)  ABORT CLIMBING
		 * (3)  ARM UP
		 * (4)  ARM DOWN
		 * (5)  INTAKE IN
		 * (6)  INTAKE OUT
		 * (7)  ELEVATOR UP
		 * (8)  ELEVATOR DOWN
		 * (9)  JAWS
		 * (10) ELEVATOR TO FLOOR
		 * (11) ELEVATOR TO SHOOTING HEIGHT
		 * (12) ELEVATOR TO PICKUP HEIGHT
		 *
		 */
		buttonPanel = new Joystick(0);
		joystick1 = new Joystick(1);
		joystick2 = new Joystick(2);

		/*
		 * Load the desired button layout
		 */
		switch(buttonLayout) {
		case Standard:
			new JoystickButton(buttonPanel, 1).whileHeld(createIntakeIn());
			new JoystickButton(buttonPanel, 2).whileHeld(createIntakeOut());
			new JoystickButton(buttonPanel, 3).whileHeld(createElevatorCalibrate());
			new JoystickButton(buttonPanel, 4).whileHeld(createElevatorDown());
			new JoystickButton(buttonPanel, 5).whileHeld(createElevatorUp());
			new JoystickButton(buttonPanel, 6).whenPressed(createClimbSequence());
			new JoystickButton(buttonPanel, 7).whenPressed(createGearShiftToggle());
			new JoystickButton(buttonPanel, 8).whenPressed(createClimbAbortSequence());				
			new JoystickButton(buttonPanel, 9).whenPressed(createRobotJawsToggle());
			new JoystickButton(buttonPanel, 10).whenPressed(createCalibrationSequence());
			new JoystickButton(buttonPanel, 11).whileHeld(createArmIn());
			new JoystickButton(buttonPanel, 12).whileHeld(createArmOut());
			break;
		case MotorTesting:
			new JoystickButton(buttonPanel, 1).whileHeld(createIntakeIn());
			new JoystickButton(buttonPanel, 2).whileHeld(createIntakeOut());
			new JoystickButton(buttonPanel, 3).whileHeld(createElevatorCalibrate());
			new JoystickButton(buttonPanel, 4).whileHeld(createElevatorDown());
			new JoystickButton(buttonPanel, 5).whileHeld(createElevatorUp());
			new JoystickButton(buttonPanel, 6).whileHeld(createArmPositionPIDTest());
			new JoystickButton(buttonPanel, 7).whileHeld(createElevatorPositionPIDTest());
			new JoystickButton(buttonPanel, 8).whenPressed(createArmDemoSequence()); // new MotorVelocityPIDTest(Robot.arm));
			new JoystickButton(buttonPanel, 9).whileHeld(createElevatorToPickUpHeight()); // new createElevatorVelocityPIDTest());
			new JoystickButton(buttonPanel, 10).whenPressed(createCalibrationSequence());
			new JoystickButton(buttonPanel, 11).whileHeld(createArmIn());
			new JoystickButton(buttonPanel, 12).whileHeld(createArmOut());
			break;
		default:
		case Competition:
			new JoystickButton(buttonPanel, 1).whenPressed(createClimbSequence());
			new JoystickButton(buttonPanel, 2).whenPressed(createClimbAbortSequence());
			new JoystickButton(buttonPanel, 3).whileHeld(createArmIn());
			new JoystickButton(buttonPanel, 4).whileHeld(createArmOut());
			new JoystickButton(buttonPanel, 5).whileHeld(createIntakeIn());
			new JoystickButton(buttonPanel, 6).whileHeld(createIntakeOut());
			new JoystickButton(buttonPanel, 7).whileHeld(createElevatorUp());
			new JoystickButton(buttonPanel, 8).whileHeld(createElevatorDown());
			new JoystickButton(buttonPanel, 9).whenPressed(createRobotJawsToggle());
			new JoystickButton(buttonPanel, 10).whileHeld(createElevatorCalibrate()); // "elevator to floor"
			new JoystickButton(buttonPanel, 11).whileHeld(createShootingPositionSequence());
			new JoystickButton(buttonPanel, 12).whileHeld(createPickUpPositionSequence());
			break;
		}

		// Add compressor to live window
		LiveWindow.add(RobotMap.compressor);
		
		// Add roborio user button compressor toggle
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

