package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.talon.Talon;
import org.usfirst.frc2813.Robot2018.motor.talon.TalonProfileSlot;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * Generalized motor subsystem.
 * 
 * The motor is given a name and a configuration.  Then you can use the
 * capabilities described in the configuration.
 *   
 * General Motor commands are found in the org.usfirst.frc2813.Robot2018.commands.motor package 
 *
 */
public final class Motor extends SubsystemPositionDirectionSpeed {

	private final Talon motorController;
	private final MotorConfiguration configuration;

	public Motor(MotorConfiguration configuration, WPI_VictorSPX talonSRX) {
		super(
				configuration.getNativeDisplayLengthUOM(), 
				configuration.getNativeSensorLengthUOM(), 
				configuration.getNativeMotorLengthUOM(),
				configuration.getNativeDisplayRateUOM(), 
				configuration.getNativeSensorRateUOM(), 
				configuration.getNativeMotorRateUOM() // NB: Should be passing up AxisConfiguration
				);
		this.configuration = configuration;
		this.motorController = null; // TODO
		configure();
	}

	public Motor(MotorConfiguration configuration, VictorSPX talonSRX) {
		super(
				configuration.getNativeDisplayLengthUOM(), 
				configuration.getNativeSensorLengthUOM(), 
				configuration.getNativeMotorLengthUOM(),
				configuration.getNativeDisplayRateUOM(), 
				configuration.getNativeSensorRateUOM(), 
				configuration.getNativeMotorRateUOM() // NB: Should be passing up AxisConfiguration
				);
		this.configuration = configuration;
		this.motorController = null; // TODO
		configure();
	}
	
	public Motor(MotorConfiguration configuration, TalonSRX talonSRX) {
		super(
				configuration.getNativeDisplayLengthUOM(), 
				configuration.getNativeSensorLengthUOM(), 
				configuration.getNativeMotorLengthUOM(),
				configuration.getNativeDisplayRateUOM(), 
				configuration.getNativeSensorRateUOM(), 
				configuration.getNativeMotorRateUOM() // NB: Should be passing up AxisConfiguration
				);
		this.configuration = configuration;
		this.motorController = new Talon(talonSRX);
		configure();
	}
	protected Rate getDefaultSpeed() {
		return configuration.getDefaultRate();
	}
	
	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}

	protected MotorState readMotorControllerState() {
		return motorController.getState();
	}

	protected void configure() {
		// Set forward hard limits
		if(configuration.hasAll(MotorConfiguration.Forward|MotorConfiguration.LimitPosition|MotorConfiguration.ForwardHardLimitSwitch)) {
			motorController.setHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.FeedbackConnector, configuration.getForwardHardLimitSwitchNormal());
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
		} else {
			motorController.setHardLimitSwitch(Direction.FORWARD, LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		}
		// Set reverse hard limits
		if(configuration.hasAll(MotorConfiguration.Reverse|MotorConfiguration.LimitPosition|MotorConfiguration.ReverseHardLimitSwitch)) {
			motorController.setHardLimitSwitch(Direction.REVERSE, LimitSwitchSource.FeedbackConnector, configuration.getReverseHardLimitSwitchNormal());
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
		} else {
			motorController.setHardLimitSwitch(Direction.REVERSE, LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);
			motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);
		}
		// Set forward soft limit
		if(configuration.hasAll(MotorConfiguration.Forward|MotorConfiguration.LimitPosition|MotorConfiguration.ForwardSoftLimitSwitch)) {
			motorController.setSoftLimitSwitch(Direction.FORWARD, true, configuration.getForwardLimit().convertTo(configuration.getNativeSensorLengthUOM()).getValueAsInt());
		} else {
			motorController.setSoftLimitSwitch(Direction.FORWARD, false);
		}
		// Set reverse soft limit
		if(configuration.hasAll(MotorConfiguration.Reverse|MotorConfiguration.LimitPosition|MotorConfiguration.ReverseSoftLimitSwitch)) {
			motorController.setSoftLimitSwitch(Direction.REVERSE, true, configuration.getReverseLimit().convertTo(configuration.getNativeSensorLengthUOM()).getValueAsInt());
		} else {
			motorController.setSoftLimitSwitch(Direction.REVERSE, false);
		}
		if(configuration.hasAny(MotorConfiguration.NeutralMode)) {
			motorController.setNeutralMode(configuration.getNeutralMode());
		}
// HW BUG WORKAROUND
motorController.setHardLimitSwitch(Direction.UP, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.UP, true);

		// Configure the PID profiles
  	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.8, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 0.75, 0.01, 40.0);
	    initialize();
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
		if(configuration.getDefaultCommandFactory() != null) {
			setDefaultCommand(configuration.getDefaultCommandFactory().createCommand(this));
		}
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
		Length scaledPosition = sensorUnitsToLength(rawPosition * configuration.getSensorToDriveScalingFactor());
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
		Rate scaledRate = toSensorUnits(sensorRate.multiply(configuration.getSensorToDriveScalingFactor()));
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
		Length scaledPosition = sensorPosition.multiply(configuration.getSensorToDriveScalingFactor());
		System.out.println("SET POS [POS=" + position + " SENSOR_U=" + sensorPosition + " SCALED=" + scaledPosition);
		// NB: Talon supports motor inversion, so we won't need to do it here.		
		motorController.setPosition(scaledPosition.getValueAsInt());
	}

	public void changeSpeed(Rate speed) {
		if(state == MotorState.MOVING) {
			// Keep moving, call the official function
			moveInDirectionAtSpeed(direction, speed);
		} else {
			// Make a note of the speed (may or may not get used)
			speed = speed;
			oldSpeed = speed; 
		}
	}
	public String toString() {
		return configuration.getName();
	}
	/*
	 * Returns the speed if we are moving, otherwise null
	 */
	public Rate getSpeed() {
		if(state == MotorState.MOVING) {
			return speed;
		} else {
			return null;
		}
	}
	/*
	 * Returns the position if we are moving, otherwise null.
	 */
	public Length getPosition() {
		if(state == MotorState.SET_POSITION) {
			return position;
		} else {
			return null;
		}
	}
	/*
	 * Returns the direction if we are moving in a direction (NOT holding a position or moving to a position!)
	 */
	public Direction getDirection() {
		if(state == MotorState.MOVING) {
			return direction;
		} else {
			return null;
		}
	}
}
