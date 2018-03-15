package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.Iterator;
import java.util.logging.Logger;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.TalonSensorPhase;
import org.usfirst.frc2813.Robot2018.motor.axis.AxisConfiguration;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Elevator subsystem. Moves up and down.
 *
 * Elevator can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in inches.
 * speed is in inches per second.
 */
public class Elevator extends SubsystemPositionDirectionSpeed {
	private Talon motorController;
	public static final ElevatorAxisConfiguration elevatorAxis = new ElevatorAxisConfiguration();
//	protected double PULSES_PER_UNIT_POSITION = LengthUOM.Inches.getCanonicalValue().convertTo(elevatorAxis.getNativeMotorLengthUOM()).getValue();

	public Elevator() {
		super(
				elevatorAxis.getNativeDisplayLengthUOM(), 
				elevatorAxis.getNativeSensorLengthUOM(), 
				elevatorAxis.getNativeMotorLengthUOM(),
				elevatorAxis.getNativeDisplayRateUOM(), 
				elevatorAxis.getNativeSensorRateUOM(), 
				elevatorAxis.getNativeMotorRateUOM() // NB: Should be passing up AxisConfiguration
				);
		motorController = new Talon(RobotMap.srxElevator, Logger.getLogger("ElevatorMC"));		
	    DEFAULT_SPEED = RateUOM.InchesPerSecond.create(12); // In subsystem units inches/second
//	    MAX_POSITION = elevatorAxis.getForwardLimit(); // In subsystem units inches
//		MIN_POSITION = elevatorAxis.getReverseLimit(0); // In subsystem units inches
		// Configure the limits for DOWN
		motorController.configureHardLimitSwitch(Direction.DOWN, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		motorController.disableSoftLimitSwitch(Direction.DOWN);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, true);

// HW BUG WORKAROUND
motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.UP, true);
//		// Configure the limits for UP
//		motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
//		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, false);
// HW BUG WORKAROUND
//		motorController.configureSoftLimitSwitch(Direction.UP, (int)Math.round(MAX_POSITION.convertTo(elevatorAxis.getNativeMotorLengthUOM()).getValue()));

		// Configure the PID profiles
  	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.8, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 0.75, 0.01, 40.0);
	    initialize();
	}
	
	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	protected MotorControllerState readMotorControllerState() {
		return motorController.getState();
	}


	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new ElevatorHoldPosition());
	}

	@Override
	public void periodic() {}
	
	/// Dump debug output
	public void dumpState() {
		getCurrentPositionInSensorUnits();
		//super.dumpState();
		//motorController.dumpState();
		
	}
	// Just testing
	public void encoderRelativePositionTestingMode() {
		disable();
		motorController.setEncoderPosition(0);
		disable();
		dumpState();
	}

	/*
	 * Get the current position from the motor controller
	 * NOTE: This is the only place we should be reading the sensor and doing scaling 
	 */
	@Override
	protected Length getCurrentPositionInSensorUnits() {
		int rawPosition = motorController.readPosition();
		Length scaledPosition = sensorUnitsToLength(rawPosition * elevatorAxis.getSensorToDriveScalingFactor());
		System.out.println("POSITION [RAW=" + rawPosition + " SCALED=" + scaledPosition + " SUBSYSTEM=" + toSubsystemUnits(scaledPosition) + "]");
		// NB: Talon supports sensor inversion, so we won't need to do it here.
		return scaledPosition;
	}
	/*
	 * Tell the motor controller a new position.  
	 * NOTE: This is the ONLY place we should be doing conversions and scaling 
	 */
	protected void setControllerDirectionAndSpeed(Direction direction, Rate speedParam) {
		Rate sensorRate = toSensorUnits(speedParam);
		Rate scaledRate = toSensorUnits(sensorRate.multiply(elevatorAxis.getSensorToDriveScalingFactor()));
//		double scaledMotorPosition = motorRate * elevatorAxis.getMotorToDriveScalingFactor();
		System.out.println("SET RATE [DIR=" + direction + " SPEED=" + speedParam + " SENSOR_U=" + sensorRate + " SCALED=" + scaledRate + "]");
		motorController.move(direction, scaledRate.getValueAsInt());
//		System.out.println("UNITS: " + toMotorUnits(speedParam).getValueAsInt());
	}
	/*
	 * Tell the motor controller a new position.  
	 * NOTE: This is the ONLY place we should be doing conversions and scaling 
	 */
	@Override
	protected void setPosition(Length position) {
		Length sensorPosition = toSensorUnits(position);
		Length scaledPosition = sensorPosition.multiply(elevatorAxis.getSensorToDriveScalingFactor());
		System.out.println("SET POS [POS=" + position + " SENSOR_U=" + sensorPosition + " SCALED=" + scaledPosition);
		// NB: Talon supports motor inversion, so we won't need to do it here.		
		motorController.setPosition(scaledPosition.getValueAsInt());
	}
}
