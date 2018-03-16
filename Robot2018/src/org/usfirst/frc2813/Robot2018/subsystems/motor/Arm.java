package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Arm.ArmHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Arm subsystem. Rotates up and down
 *
 * Arm can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in degrees.
 * speed is in inches per second.
 */
public class Arm extends SubsystemPositionDirectionSpeed {
	//private static final double GEAR_RATIO = 100 / 1.0; // 100:1 planetary gearbox.
    //private static final double PULSES_PER_REVOLUTION = GEAR_RATIO * Talon.TALON_SRX_OUTPUTR_PULSES_PER_REVOLUTION;

    //private static final double PULSES_PER_DEGREE = PULSES_PER_REVOLUTION / 360;
    //private static final double VELOCITY_TIME_UNITS_PER_SEC = 1; // The Velocity control mode expects units of pulses per 100 milliseconds.
    protected Length MAX_POSITION;
	protected Length MIN_POSITION;

	private Talon motorController;

	public Arm() {
		super(LengthUOM.Inches, 
			  LengthUOM.Micrometers, 
			  LengthUOM.Micrometers,
			  RateUOM.InchesPerSecond,
			  RateUOM.InchesPerSecond,
			  RateUOM.InchesPerSecond); // TODO: These units should be custom for ARM (see Elevator's AxisConfiguration).  Micrometers should not break anything until this is finished.
		MAX_POSITION = LengthUOM.Inches.create(20); // NB: totally wrong, needs to know the circumference, less the region we cannot access...
		MIN_POSITION = LengthUOM.Inches.create(0);
		// PULSES_PER_UNIT_POSITION = PULSES_PER_REVOLUTION / 360;
		// PULSES_PER_UNIT_POSITION_PER_TIME = PULSES_PER_DEGREE * VELOCITY_TIME_UNITS_PER_SEC;

		motorController = new Talon(RobotMap.srxArm);
		
		// Configure the limits for DOWN
		motorController.setHardLimitSwitch(Direction.BACKWARD, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		// motorController.setSoftLimitSwitch(Direction.BACKWARD, true, ???);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.BACKWARD, true);
		// Configure the limits for UP
		motorController.setHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		// motorController.configureSoftLimitSwitch(Direction.FORWARD, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION)); NB: This is wrong and needs to be calculated.  See ElevatorAxisConfiguration
		// Configure the PID profiles
		motorController.setSlotIndexForHoldPosition(TalonProfileSlot.HoldingPosition);
		motorController.setSlotIndexForMove(TalonProfileSlot.Moving);
	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.1, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 2.0, 0.0, 0.0);
	    initialize();
	}
	protected Rate getDefaultSpeed() {
		return RateUOM.InchesPerSecond.create(5);
	}

	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	protected MotorControllerState readMotorControllerState() {
		return motorController.getState();
	}

	protected void setControllerDirectionAndSpeed(Direction direction, Rate speedParam) {
		motorController.move(direction, speedParam.convertTo(motorRateUnits).getValueAsInt());
	}

	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes arm in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new ArmHoldPosition());
	}

	@Override
	public void periodic() {}

	@Override
	protected Length getCurrentPositionInSensorUnits() {
		int rawPosition = motorController.readPosition();
/*
		double scaledPosition = rawPosition / armAxis.getMotorToSensorScalingFactor();
		// NB: Talon supports sensor inversion, so we won't need to do it here.
		return sensorUnitsToLength(scaledPosition);
 */
		return sensorUnitsToLength(rawPosition);
	}

	@Override
	protected void setPosition(Length position) {
/*		
		double motorPosition = toMotorUnits(position).getValue();
		double scaledMotorPosition = motorPosition * armAxis.getMotorToSensorScalingFactor();
		// NB: Talon supports motor inversion, so we won't need to do it here.		
		motorController.setPosition(toMotorUnits(position).getValueAsInt());
*/
		motorController.setPosition(toMotorUnits(position).getValueAsInt());
	}
	
}
