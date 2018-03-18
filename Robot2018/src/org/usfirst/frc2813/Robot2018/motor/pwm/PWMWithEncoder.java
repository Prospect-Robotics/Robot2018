package org.usfirst.frc2813.Robot2018.motor.pwm;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PWMSpeedController;

public class PWMWithEncoder extends AbstractMotorController implements IMotor {
	private final PWMSpeedController speedController;
	private final Encoder encoder;
	private Length relativeOffset = null;
	
	private static final long UNSUPPORTED_CAPABILITIES = 0  
		| IMotorConfiguration.LocalReverseHardLimitSwitch
		| IMotorConfiguration.LocalForwardHardLimitSwitch
		| IMotorConfiguration.RemoteForwardHardLimitSwitch
		| IMotorConfiguration.RemoteReverseHardLimitSwitch
		;

	private static final long SUPPORTED_CAPABILITIES = 0
		| IMotorConfiguration.ControlPosition
		| IMotorConfiguration.ForwardSoftLimitSwitch
		| IMotorConfiguration.LimitPosition
		| IMotorConfiguration.SensorToDriveScale
		| IMotorConfiguration.ReadPosition
		| IMotorConfiguration.Forward
		| IMotorConfiguration.ReverseSoftLimitSwitch
		| IMotorConfiguration.Reverse
		| IMotorConfiguration.ControlRate
		| IMotorConfiguration.ReadRate
		| IMotorConfiguration.NeutralMode
		| IMotorConfiguration.DefaultRate
		| IMotorConfiguration.LimitRate
		| IMotorConfiguration.Disconnected
		;
	
	public PWMWithEncoder(IMotorConfiguration configuration, PWMSpeedController sp, Encoder encoder) {
		super(configuration);
		this.speedController = sp;
		this.encoder = encoder;
		if(configuration.hasAny(UNSUPPORTED_CAPABILITIES)) {
			throw new IllegalArgumentException(this + ": PWM Motors with encoders do not support the following capabilities: " + MotorConfiguration.listCapabilities(configuration.getCapabilities() & UNSUPPORTED_CAPABILITIES, "", ", ", ""));
		}
		if (!configuration.hasAny(SUPPORTED_CAPABILITIES)) {
			throw new IllegalArgumentException(this + ": You haven't configured any supported features for this motor.");
		}
	}

	@Override
	public Length getCurrentPosition() {
		return relativeOffset.add(encoder.get());
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
		return true;
	}

	@Override
	public void configure() {
		speedController.setInverted(configuration.getMotorPhaseIsReversed());
		encoder.setReverseDirection(configuration.getSensorPhaseIsReversed());
		/*
		 * TODO: This conversion assumes the native sensor rate was setup for a length of 1 pulse.  
		 * If not, there's no way to figure out the value for one as it's already scaled to canonical 
		 * length internally and we don't know "how many" pulses it represented.
		 */
		
		// How far can we get in one second in our native sensor units, assuming that the native units were for a single pulse
		double distancePerNativeSensorUnitPerSecond = configuration.getNativeSensorRateUOM().getValue().getLength(TimeUOM.Seconds.create(1)).getValue();
		encoder.setDistancePerPulse(distancePerNativeSensorUnitPerSecond);
		// Reset the encoder and clear the relative offset
		relativeOffset = configuration.getNativeSensorLengthUOM().create(0); 
		encoder.reset();
	}

	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		// NB: encoder won't store an offset for us, but we can still store it.
		encoder.reset();
		relativeOffset = toSensorUnits(sensorPosition);
		return true;
	}

	protected boolean executeTransition(IMotorState proposedState) {
		double		newSetting = 0;
		switch(proposedState.getOperation()) {
		case DISABLED:
			newSetting = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			throw new UnsupportedOperationException("PID loop has not yet been implemented for PWM + Encoder.");
		case MOVING_TO_ABSOLUTE_POSITION:
			throw new UnsupportedOperationException("PID loop has not yet been implemented for PWM + Encoder.");
		case MOVING_TO_RELATIVE_POSITION:
			throw new UnsupportedOperationException("PID loop has not yet been implemented for PWM + Encoder.");
		case MOVING_IN_DIRECTION_AT_RATE:
			// NB: Assumes that the rate equates to a percentage.  This may not work at all.			
			newSetting = proposedState.getTargetRate().convertTo(configuration.getNativeSensorRateUOM()).getValue();
			break;
//			throw new UnsupportedOperationException("PID loop has not yet been implemented for PWM + Encoder.");
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
