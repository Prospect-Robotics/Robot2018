package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Constants;
import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

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
	static final double ARM_PULSES_PER_REVOLUTION = ARM_GEAR_RATIO * RobotMap.SRX_MAG_PULSES_PER_REVOLUTION;
	static final double ARM_ONE_DEGREE = ARM_PULSES_PER_REVOLUTION / 360;
	static final double ARM_ONE_DEGREE_PER_SECOND = ARM_ONE_DEGREE * RobotMap.SRX_VELOCITY_TIME_UNITS_PER_SEC;
	static final double ARM_DEFAULT_SPEED = 5 * ARM_ONE_DEGREE_PER_SECOND;
	static final double ARM_MIN = 0.0;
	static final double ARM_MAX = ARM_ONE_DEGREE * ARM_DEGREES;

	// TODO: Move to Talon
	public static final int TALON_TIMEOUT_TO_CONFIGURE_MS = 10;
	
	public Arm() {
		// TODO: create srx controller class. Move most of this code there
		RobotMap.srxArm.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_TO_CONFIGURE_MS);
		RobotMap.srxArm.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
		RobotMap.srxArm.configForwardSoftLimitEnable(true, 10);
		RobotMap.srxArm.configForwardSoftLimitThreshold((int) (ARM_MAX), TALON_TIMEOUT_TO_CONFIGURE_MS);
		// RobotMap.srxArm.configSetParameter(ParamEnum.eClearPositionOnLimitR, 0, 1, 0, TALON_TIMEOUT_TO_CONFIGURE_MS);

		
		int absolutePosition = RobotMap.srxArm.getSensorCollection().getPulseWidthPosition();
		if (Constants.kSensorPhase)
			absolutePosition *= -1;
		if (Constants.kMotorInvert)
			absolutePosition *= -1;
		RobotMap.srxArm.setSelectedSensorPosition(absolutePosition, Constants.maintainPIDLoopIdx, Constants.kTimeoutMs);
		
		
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
		return RobotMap.srxArm.getSelectedSensorPosition(Constants.PRIMARY_CLOSED_LOOP_SENSOR);
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
		RobotMap.srxArm.selectProfileSlot(1, 1);
		double speed = armSpeed;
		speed *= armDirection == Direction.UP ? 1 : -1;
		RobotMap.srxArm.set(ControlMode.Velocity, speed);	
		System.out.printf("Starting arm movement. Speed %f", speed);
	}
	
	// stop arm from moving - this is active as we require pid to resist gravity
	public static void haltArm() {
		if (!armIsHalted) {
			return;
		}
		armIsHalted = false;//FIXME True or false here?
		RobotMap.srxArm.selectProfileSlot(0, 0);
		RobotMap.srxArm.set(ControlMode.Position, readArmPosition());
		System.out.printf("Halting arm movement. Position %f", readArmPosition());
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