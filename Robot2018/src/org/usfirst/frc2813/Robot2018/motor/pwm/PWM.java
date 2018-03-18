package org.usfirst.frc2813.Robot2018.motor.pwm;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.Victor;

public class PWM extends AbstractMotorController implements IMotor {
	private final PWMSpeedController speedController;
	private static final long UNSUPPORTED_CAPABILITIES =  
		IMotorConfiguration.ControlPosition
		| IMotorConfiguration.LocalForwardHardLimitSwitch
		| IMotorConfiguration.ForwardSoftLimitSwitch
		| IMotorConfiguration.LimitPosition
		| IMotorConfiguration.SensorToDriveScale
		| IMotorConfiguration.ReadPosition
		| IMotorConfiguration.LocalReverseHardLimitSwitch
		| IMotorConfiguration.ReverseSoftLimitSwitch
		| IMotorConfiguration.RemoteForwardHardLimitSwitch
		| IMotorConfiguration.RemoteReverseHardLimitSwitch
		| IMotorConfiguration.LimitRate
		;

	private static final long SUPPORTED_CAPABILITIES =
			  IMotorConfiguration.Forward
			| IMotorConfiguration.Reverse
			| IMotorConfiguration.ControlRate
			| IMotorConfiguration.ReadRate
			| IMotorConfiguration.NeutralMode
			| IMotorConfiguration.DefaultRate
			| IMotorConfiguration.Disconnected
			;
	
	public PWM(IMotorConfiguration configuration, PWMSpeedController sp) {
		super(configuration);
		this.speedController = sp;
		if(configuration.hasAny(UNSUPPORTED_CAPABILITIES)) {
			throw new IllegalArgumentException(this + ": PWM Motors do not support the following capabilities: " + MotorConfiguration.listCapabilities(configuration.getCapabilities() & UNSUPPORTED_CAPABILITIES, "", ", ", ""));
		}
		if (!configuration.hasAny(SUPPORTED_CAPABILITIES)) {
			throw new IllegalArgumentException(this + ": You haven't configured any supported features for this motor.");
		}
	}

	@Override
	public Length getCurrentPosition() {
		throw new UnsupportedOperationException(this + " does not support sensors, so you cannot read them.");
	}

	@Override
	public boolean getCurrentLimitSwitchStatus(Direction switchDirection) {
		throw new UnsupportedOperationException(this + " does not support limit switches.");
	}

	@Override
	public boolean supportsMotorInversion() {
		return true;
	}

	@Override
	public boolean supportsSensorInversion() {
		return false;
	}

	@Override
	public void configure() {
		// Nothing to do here.
		if(configuration.getMotorPhaseIsReversed()) {
			speedController.setInverted(true);
		}
	}

	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		throw new UnsupportedOperationException("PWM controllers do not support sensors, so you can't reset them.");
	}
	
	protected boolean executeTransition(IMotorState proposedState) {
		double		newSetting = 0;
		switch(proposedState.getOperation()) {
		case DISABLED:
			newSetting = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			throw new UnsupportedOperationException("PWM controllers do not support holding position.  There are no sensors.");
		case MOVING_TO_ABSOLUTE_POSITION:
			throw new UnsupportedOperationException("PWM controllers do not support absolute positioning.  There are no sensors.");
		case MOVING_TO_RELATIVE_POSITION:
			throw new UnsupportedOperationException("PWM controllers do not support relative positioning.  There are no sensors.");
		case MOVING_IN_DIRECTION_AT_RATE:
			// NB: Assumes that the rate equates to a percentage.  This may not work at all.
			newSetting = proposedState.getTargetRate().convertTo(configuration.getNativeSensorRateUOM()).getValue();
			break;
		}
		if(newSetting < -1 || newSetting > 1) {
			// return false;
			throw new IllegalArgumentException("PWM value must be between -1.0 and 1.0");
		}
		speedController.set(newSetting);
		return true;
	}

	@Override
	public Rate getCurrentRate() {
		return configuration.getNativeSensorRateUOM().create(speedController.get());
	}

}
