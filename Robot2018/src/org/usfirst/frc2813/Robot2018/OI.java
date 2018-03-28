package org.usfirst.frc2813.Robot2018;

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.ToggleCompressor;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDrive;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSpin;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorCalibrateSensor;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorDisable;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveInDirection;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePosition;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorPositionPIDTest;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorVelocityPIDTest;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSet;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggle;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemDisableDefaultCommand;
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
		 * (6)  CLIMBING BAR SOLENOID
		 * (7)  SHIFT GEARS SOLENOID
		 * (8)  ELEVATOR RATCHET SOLENOID
		 * (9)  INTAKE/ARM SOLENOID
		 * (10) ELEVATOR SHUFFLE
		 * (11) MOVE ARM UP
		 * (12) MOVE ARM DOWN
		 *
		 */
		buttonPanel = new Joystick(0);

		CommandGroup calibration = new CommandGroup();
		calibration.addSequential(new MotorCalibrateSensor(Robot.arm, Direction.REVERSE));
		calibration.addSequential(new MotorCalibrateSensor(Robot.elevator, Direction.REVERSE));

		SubsystemCommand<Solenoid> ratchetEngageAndLock = new SolenoidSet(Robot.ratchet, Direction.ENGAGED, RunningInstructions.RUN_NORMALLY, Lockout.UntilUnlocked);
		SubsystemCommand<Solenoid> climbingBarExtendAndLock = new SolenoidSet(Robot.climbingBar, Direction.OUT, RunningInstructions.RUN_FOREVER, Lockout.UntilUnlocked);
		SubsystemCommand<Motor> armDisableAndLock = new MotorDisable(Robot.arm, RunningInstructions.RUN_FOREVER, Lockout.UntilUnlocked);
		
		CommandGroup climbSequenceEngage = new CommandGroup();
		/* Engage and lock the ratchet. */
		climbSequenceEngage.addSequential(ratchetEngageAndLock);
		/* Extend and lock the climbing bar. */
		climbSequenceEngage.addSequential(climbingBarExtendAndLock);
		// disable sequence for a motor is:
		climbSequenceEngage.addSequential(armDisableAndLock);

		// TODO
		CommandGroup climbSequenceDisengage = new CommandGroup();

		new JoystickButton(buttonPanel, 1).whileHeld(new IntakeSpin(Robot.intake, Direction.IN));
		new JoystickButton(buttonPanel, 2).whileHeld(new IntakeSpin(Robot.intake, Direction.OUT));
		new JoystickButton(buttonPanel, 4).whileHeld(new MotorMoveInDirection(Robot.elevator, Direction.DOWN));
		new JoystickButton(buttonPanel, 5).whileHeld(new MotorMoveInDirection(Robot.elevator, Direction.UP));
		if(true) { // NB: The other settings are for Mike's debugging of PID vales on Arm and Elevator and are disabled by default.		
				new JoystickButton(buttonPanel, 6).whenPressed(climbSequenceEngage);
				new JoystickButton(buttonPanel, 7).whenPressed(new SolenoidToggle(Robot.driveTrain.getGearShiftSolenoid()));
				new JoystickButton(buttonPanel, 8).whenPressed(climbSequenceDisengage);				
				new JoystickButton(buttonPanel, 9).whenPressed(new SolenoidToggle(Robot.jaws));
		} else {
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
		new JoystickButton(buttonPanel, 10).whenPressed(calibration);
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

