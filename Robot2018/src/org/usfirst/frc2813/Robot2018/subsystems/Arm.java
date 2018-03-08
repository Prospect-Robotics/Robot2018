package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Arm subsystem has arm rotation, RobotMap.armSingleSolenoid open/close and intake in/out
 * Arm rotation has default halted state. Move command takes 
 */
public class Arm extends Subsystem {
	public boolean encoderFunctional = true;
	private static Direction armDirection;
	private static double armSpeed;
	private static boolean armIsHalted;
	private static Direction intakeDirection;
	private static double intakeSpeed;
	private static Talon talon;

	/**
	 * Double solenoids are two independent coils.
	 * 
	 * When the RobotMap.armSingleSolenoid solenoid coil is activated, this is set to a relatively small number,
	 * whereafter it is decremented each time through the {@link Scheduler} main loop.  When
	 * it reaches zero, the coil is turned off again.
	 */

	// INTAKE GEOMETRY
	private static final double INTAKE_DEFAULT_SPEED = 0.7;

	// ARM GEOMETRY
	static final double ARM_DEGREES = 24;
	static final double ARM_GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
	static final double ARM_PULSES_PER_REVOLUTION = ARM_GEAR_RATIO * Talon.SRX_MAG_PULSES_PER_REVOLUTION;
	static final double ARM_ONE_DEGREE = ARM_PULSES_PER_REVOLUTION / 360;
	static final double ARM_ONE_DEGREE_PER_SECOND = ARM_ONE_DEGREE * Talon.SRX_VELOCITY_TIME_UNITS_PER_SEC;
	static final double ARM_DEFAULT_SPEED = 5 * ARM_ONE_DEGREE_PER_SECOND;
	static final double ARM_MIN = 0.0;
	static final double ARM_MAX = ARM_ONE_DEGREE * ARM_DEGREES;

	public Arm() {
		talon = Talon(RobotMap.srxArm);
		talon.configHardLimitSwitch(Direction.BACKWARD);
		talon.configSoftLimitSwitch(Direction.FORWARD, (int)ARM_MAX);
		
		int absolutePosition = talon.getSensorCollection().getPulseWidthPosition();
		if (Constants.kSensorPhase)
			absolutePosition *= -1;
		if (Constants.kMotorInvert)
			absolutePosition *= -1;
		talon.setSelectedSensorPosition(absolutePosition, Constants.maintainPIDLoopIdx, Constants.kTimeoutMs);
		
		
		// track state and change as required. Start in moving so initialize can halt
		armIsHalted = false;
	}
	
	// speed and direction for arm and intake are state
	public static void setArmSpeed() { armSpeed = ARM_DEFAULT_SPEED; }
	public static void setArmSpeed(double speed) { armSpeed = speed; }
	public static void setArmDirection(Direction direction) { armDirection = direction; }
	public static void setIntakeSpeed() { intakeSpeed = INTAKE_DEFAULT_SPEED; }
	public static void setIntakeSpeed(double speed) { intakeSpeed = speed; }
	public static void setIntakeDirection(Direction direction) { intakeDirection = direction; }

	public static double readArmPosition() {
		return talon.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);
	}
	
	public static double readArmEndPosition() {
		if (armDirection == Direction.DOWN) {
			return 0.0;
		}
		else {
			return ARM_MAX;
		}
	}

	// Start arm moving
	public static void moveArm() {
		if (armIsHalted) {
			return;
		}
		armIsHalted = true;//FIXME True or false here?
		talon.setSpeedAndDirection(armSpeed, armDirection);
		System.out.format("Starting arm movement. Speed %f", armSpeed);
	}

	// stop arm from moving - this is active as we require pid to resist gravity
	public static void haltArm() {
		if (!armIsHalted) {
			return;
		}
		armIsHalted = false;//FIXME True or false here?
		talon.halt();
		System.out.format("Halting arm movement. Position %f", talon.readPosition());
	}

	// Manage jaws
	public static void closeJaws() {
		if (!jawsAreOpen()) {
			RobotMap.armSingleSolenoid.set(false);
		}
	}
	public static void openJaws() {
		if (jawsAreOpen()) {
			RobotMap.armSingleSolenoid.set(false);
		}
	}
	// accessor for state of jaws - TODO - can we initialize and track with boolean?
	public static Boolean jawsAreOpen() { return RobotMap.armSingleSolenoid.get(); }
	
	// Manage intake wheels
	public static void spinIntake() {
		// NOTE: 2nd speed controller is slaved to this one in RobotMap
		double speed = intakeSpeed * (intakeDirection == Direction.IN ? 1 : -1);
		RobotMap.intakeSpeedController.set(speed);
	}
	public static void haltIntake() {
		RobotMap.intakeSpeedController.set(0);
	}

	// initializes arm in static position
	@Override
	public void initDefaultCommand() {
		setDefaultCommand(new MoveArm());
	}

	@Override
	public void periodic() {}
}