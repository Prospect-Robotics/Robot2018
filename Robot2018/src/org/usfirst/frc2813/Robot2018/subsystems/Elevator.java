package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.axis.AxisConfiguration;
import org.usfirst.frc2813.units.Direction;
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
	public static final ElevatorAxisConfiguration axisConfiguration = new ElevatorAxisConfiguration();

	public Elevator() {
		super(
				axisConfiguration.getNativeDisplayLengthUOM(), 
				axisConfiguration.getNativeSensorLengthUOM(), 
				axisConfiguration.getNativeMotorLengthUOM(),
				axisConfiguration.getNativeDisplayRateUOM(), 
				axisConfiguration.getNativeSensorRateUOM(), 
				axisConfiguration.getNativeMotorRateUOM() // NB: Should be passing up AxisConfiguration
				);
		motorController = new Talon(RobotMap.srxElevator);
		// Set forward hard limits
		if(axisConfiguration.hasAll(AxisConfiguration.Forward|AxisConfiguration.LimitPosition|AxisConfiguration.ForwardHardLimitSwitch)) {
			motorController.setHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.FeedbackConnector, axisConfiguration.getForwardHardLimitSwitchNormal());
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, axisConfiguration.getForwardHardLimitSwitchResetsEncoder());
		} else {
			motorController.setHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		}
		// Set reverse hard limits
		if(axisConfiguration.hasAll(AxisConfiguration.Reverse|AxisConfiguration.LimitPosition|AxisConfiguration.ReverseHardLimitSwitch)) {
			motorController.setHardLimitSwitch(Direction.REVERSE, LimitSwitchSource.FeedbackConnector, axisConfiguration.getReverseHardLimitSwitchNormal());
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, axisConfiguration.getReverseHardLimitSwitchResetsEncoder());
		} else {
			motorController.setHardLimitSwitch(Direction.REVERSE, LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);
		}
		// Set forward soft limit
		if(axisConfiguration.hasAll(AxisConfiguration.Forward|AxisConfiguration.LimitPosition|AxisConfiguration.ForwardSoftLimitSwitch)) {
			motorController.setSoftLimitSwitch(Direction.FORWARD, true, axisConfiguration.getForwardLimit().convertTo(axisConfiguration.getNativeSensorLengthUOM()).getValueAsInt());
		} else {
			motorController.setSoftLimitSwitch(Direction.FORWARD, false);
		}
		// Set reverse soft limit
		if(axisConfiguration.hasAll(AxisConfiguration.Reverse|AxisConfiguration.LimitPosition|AxisConfiguration.ReverseSoftLimitSwitch)) {
			motorController.setSoftLimitSwitch(Direction.REVERSE, true, axisConfiguration.getReverseLimit().convertTo(axisConfiguration.getNativeSensorLengthUOM()).getValueAsInt());
		} else {
			motorController.setSoftLimitSwitch(Direction.REVERSE, false);
		}
		if(axisConfiguration.hasAny(AxisConfiguration.NeutralMode)) {
			motorController.setNeutralMode(axisConfiguration.getNeutralMode());
		}
// HW BUG WORKAROUND
motorController.setHardLimitSwitch(Direction.UP, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.UP, true);

		// Configure the PID profiles
  	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.8, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 0.75, 0.01, 40.0);
	    initialize();
	}
	protected Rate getDefaultSpeed() {
		return axisConfiguration.getDefaultRate();
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
		Length scaledPosition = sensorUnitsToLength(rawPosition * axisConfiguration.getSensorToDriveScalingFactor());
		System.out.println("POSITION [RAW=" + rawPosition + " SCALED=" + scaledPosition + " SUBSYSTEM=" + toSubsystemUnits(scaledPosition) + "]");
		// NB: Talon supports sensor inversion, so we won't need to do it here.
		return scaledPosition;
	}
	/*
	 * Tell the motor controller a new position.  
	 * NOTE: This is the ONLY place we should be doing rate conversions and scaling  
	 */
	protected void setControllerDirectionAndSpeed(Direction direction, Rate speedParam) {
		Rate sensorRate = toSensorUnits(speedParam);
		Rate scaledRate = toSensorUnits(sensorRate.multiply(axisConfiguration.getSensorToDriveScalingFactor()));
		System.out.println("SET RATE [DIR=" + direction + " SPEED=" + speedParam + " SENSOR_U=" + sensorRate + " SCALED=" + scaledRate + "]");
		motorController.move(direction, scaledRate.getValueAsInt());
	}
	/*
	 * Tell the motor controller a new position.  
	 * NOTE: This is the ONLY place we should be doing conversions and scaling 
	 */
	@Override
	protected void setPosition(Length position) {
		Length sensorPosition = toSensorUnits(position);
		Length scaledPosition = sensorPosition.multiply(axisConfiguration.getSensorToDriveScalingFactor());
		System.out.println("SET POS [POS=" + position + " SENSOR_U=" + sensorPosition + " SCALED=" + scaledPosition);
		// NB: Talon supports motor inversion, so we won't need to do it here.		
		motorController.setPosition(scaledPosition.getValueAsInt());
	}
}
