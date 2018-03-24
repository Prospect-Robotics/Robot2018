package org.usfirst.frc2813.Robot2018;

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.commands.ToggleCompressor;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainOIDriveSync;
import org.usfirst.frc2813.Robot2018.commands.intake.IntakeSpinSync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorCalibrateSensorAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveInDirectionSync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePositionAsync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorPositionPIDTest;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorVelocityPIDTest;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForHardLimitSwitchSync;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorWaitForTargetPositionSync;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggleStateInstant;
import org.usfirst.frc2813.Robot2018.triggers.RoboRIOUserButton;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
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
		calibration.addSequential(new MotorCalibrateSensorAsync(Robot.arm, Direction.REVERSE));
		calibration.addSequential(new MotorWaitForHardLimitSwitchSync(Robot.arm, Direction.REVERSE));
		calibration.addSequential(new MotorCalibrateSensorAsync(Robot.elevator, Direction.REVERSE));
		calibration.addSequential(new MotorWaitForHardLimitSwitchSync(Robot.elevator, Direction.REVERSE));

		new JoystickButton(buttonPanel, 1).whileHeld(new IntakeSpinSync(Robot.intake, Direction.IN));
		new JoystickButton(buttonPanel, 2).whileHeld(new IntakeSpinSync(Robot.intake, Direction.OUT));
		new JoystickButton(buttonPanel, 4).whileHeld(new MotorMoveInDirectionSync(Robot.elevator, Direction.DOWN));
		new JoystickButton(buttonPanel, 5).whileHeld(new MotorMoveInDirectionSync(Robot.elevator, Direction.UP));
		if(true) { // NB: The other settings are for Mike's debugging of PID vales on Arm and Elevator and are disabled by default.		
				new JoystickButton(buttonPanel, 6).whenPressed(new SolenoidToggleStateInstant(Robot.climbingBar));
				new JoystickButton(buttonPanel, 7).whenPressed(new SolenoidToggleStateInstant(Robot.driveTrain.getGearShiftSolenoid()));
				new JoystickButton(buttonPanel, 8).whenPressed(new SolenoidToggleStateInstant(Robot.ratchet));
				new JoystickButton(buttonPanel, 9).whenPressed(new SolenoidToggleStateInstant(Robot.jaws));
		} else {
			CommandGroup armDemo = new CommandGroup();
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(30)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(45)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(60)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(75)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(90)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			armDemo.addSequential(new MotorMoveToAbsolutePositionAsync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(105)));
			armDemo.addSequential(new MotorWaitForTargetPositionSync(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(5)));
			armDemo.addSequential(new TimedCommand(3));
			
			new JoystickButton(buttonPanel, 6).whileHeld(new MotorPositionPIDTest(Robot.arm, Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(15), Robot.arm.getConfiguration().getNativeDisplayLengthUOM().create(110)));
			new JoystickButton(buttonPanel, 7).whileHeld(new MotorPositionPIDTest(Robot.elevator, LengthUOM.Inches.create(5), LengthUOM.Inches.create(30)));
			//	new JoystickButton(buttonPanel, 8).whileHeld(new MotorVelocityPIDTest(Robot.arm));
			new JoystickButton(buttonPanel, 8).whenPressed(armDemo);
			new JoystickButton(buttonPanel, 9).whileHeld(new MotorVelocityPIDTest(Robot.elevator));
			new JoystickButton(buttonPanel, 9).whileHeld(new MotorMoveToAbsolutePositionAsync(Robot.elevator, LengthUOM.Inches.create(6)));
		}
		new JoystickButton(buttonPanel, 10).whenPressed(calibration);
		new JoystickButton(buttonPanel, 11).whileHeld(new MotorMoveInDirectionSync(Robot.arm, Direction.IN));
		new JoystickButton(buttonPanel, 12).whileHeld(new MotorMoveInDirectionSync(Robot.arm, Direction.OUT));

		joystick1 = new Joystick(1);
		joystick2 = new Joystick(2);

		LiveWindow.add(RobotMap.compressor);
		new RoboRIOUserButton().whenPressed(new ToggleCompressor(RobotMap.compressor));

		// SmartDashboard Buttons
		SmartDashboard.putData("OIDrive", new DriveTrainOIDriveSync(Robot.driveTrain, joystick1, joystick2));

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

