package org.usfirst.frc2813.Robot2018;

import java.util.function.BiConsumer;

import org.usfirst.frc2813.Robot2018.commands.ToggleCompressor;
import org.usfirst.frc2813.Robot2018.commands.ToggleSolenoidGeneral;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.OIDrive;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.ResetEncoders;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveInDirection;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorTesting;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetState;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggleState;
import org.usfirst.frc2813.Robot2018.subsystems.RoboRIOUserButton;
import org.usfirst.frc2813.units.Direction;

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

	public JoystickButton trigger;
	public Joystick joystick1, joystick2, buttonPanel;
	public JoystickButton spinIntakeIn, spinIntakeOut,
	bottomElevatorToLimitSwitch, elevatorUp, elevatorDown, elevatorRatchet,
	shiftGears, climbingBar,
	armUpToLimitSwitch, armDownToLimitSwitch,
	armMoveUp, armMoveDown, armSolenoid;
	public final SendableChooser<BiConsumer<Joystick, Joystick>> driveStyleChooser;

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
		 * (10) ELEVATOR TESTING
		 * (11) MOVE ARM UP
		 * (12) MOVE ARM DOWN
		 *
		 */
		buttonPanel = new Joystick(0);

		new JoystickButton(buttonPanel, 8).whenPressed(new ToggleSolenoidGeneral(RobotMap.elevatorRatchet));
		new JoystickButton(buttonPanel, 6).whenPressed(new ToggleSolenoidGeneral(RobotMap.climbingBar));
		new JoystickButton(buttonPanel, 7).whenPressed(new ToggleSolenoidGeneral(RobotMap.driveTrainGearShiftSolenoid));

		new JoystickButton(buttonPanel, 10).toggleWhenActive(new MotorTesting(Robot.elevator));

		//elevatorDown.whenPressed(new PrintButtonStatus(true, false));
		elevatorUp = new JoystickButton(buttonPanel, 5);
		elevatorUp.whileHeld(new MotorMoveInDirection(Robot.elevator, Direction.UP));
		//elevatorDown.whenReleased(new ElevatorSetSpeed(0));
		//elevatorDown.whenReleased(new PrintButtonStatus(false, false));
		elevatorDown = new JoystickButton(buttonPanel, 4);
		//elevatorUp.whenPressed(new PrintButtonStatus(true, true));
		elevatorDown.whileHeld(new MotorMoveInDirection(Robot.elevator, Direction.DOWN));
		//elevatorUp.whenReleased(new ElevatorSetSpeed(0));
		//elevatorUp.whenReleased(new PrintButtonStatus(false, true));

//		new JoystickButton(buttonPanel, 3).whenPressed(new MotorMoveToPosition(elevator.getConfiguration().getReverseLimit()));
		spinIntakeOut = new JoystickButton(buttonPanel, 2);
		spinIntakeOut.whileHeld(new MotorMoveInDirection(Robot.intake, Direction.OUT));
		spinIntakeOut.whenReleased(new MotorMoveInDirection(Robot.intake, Direction.OUT));
		spinIntakeIn = new JoystickButton(buttonPanel, 1);
		spinIntakeIn.whileHeld(new MotorMoveInDirection(Robot.intake, Direction.IN));
		spinIntakeIn.whenReleased(new MotorMoveInDirection(Robot.intake, Direction.IN));
		//new JoystickButton(buttonPanel, 9).whenPressed(new ArmLimitSwitch(true));
		new JoystickButton(buttonPanel, 11).whileHeld(new MotorMoveInDirection(Robot.intake, Direction.UP));
		new JoystickButton(buttonPanel, 12).whileHeld(new MotorMoveInDirection(Robot.intake, Direction.DOWN));
		new JoystickButton(buttonPanel, 9).whenPressed(new SolenoidToggleState(Robot.jaws));

		joystick1 = new Joystick(1);
		joystick2 = new Joystick(2);

		new JoystickButton(joystick1, 1).whileHeld(new ResetEncoders());//the trigger on most joysticks

		Compressor compressor = new Compressor();
		LiveWindow.add(compressor);
		new RoboRIOUserButton().whenPressed(new ToggleCompressor(compressor));

		// SmartDashboard Buttons
		SmartDashboard.putData("OIDrive", new OIDrive());
		//SmartDashboard.putData("ToggleRelay0", new ShiftGears());
		//SmartDashboard.putData("ToggleRelay1", new ToggleRelay1());
		//SmartDashboard.putData("ToggleRelay2", new ToggleRelay2());
		//SmartDashboard.putData("SpinIntakeIn", new SpinIntake(true));
		//SmartDashboard.putData("SpinIntakeOut", new SpinIntake(false));
//		SmartDashboard.putData("BottomElevator", new ElevatorMoveToPosition(Robot.elevator.getSensorAxisConfiguration().getReverseLimit()));
		//SmartDashboard.putData("MoveElevatorUp", new MoveElevator(true));
		//SmartDashboard.putData("MoveElevatorDown", new MoveElevator(false));

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

