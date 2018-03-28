package org.usfirst.frc2813.Robot2018.motor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

/*
 * This class is an immutable description of an axis as used by a motor, a subsystem's API, or a sensor.
 * The idea is to encapsulate everything about the axis so that the subsystem can completely initialize 
 * itself and handle all conversions, scaling, formatting, limits, validation, etc... automatically with
 * the minimal amount of data specified.
 */
public class MotorConfiguration implements IMotorConfiguration {
	
	// Common state
	private final String name;
	// Capabilities
	private final int capabilities;
	
	public static String getCapabilityName(int capability) {
        switch(capability) {
        case ControlDirection       : return "ControlDirection";
        case ControlPosition        : return "ControlPosition";
        case ControlRate            : return "ControlRate";
        case Forward                : return "Forward";
        case LocalForwardHardLimitSwitch : return "ForwardHardLimitSwitch";
        case ForwardSoftLimitSwitch : return "ForwardSoftLimitSwitch";
        case LimitPosition          : return "LimitPosition";
        case LimitRate              : return "LimitRate";
        case SensorToDriveScale     : return "SensorToDriveScale";
        case ReadDirection          : return "ReadDirection";
        case ReadPosition           : return "ReadPosition";
        case ReadRate               : return "ReadRate";
        case Reverse                : return "Reverse";
        case LocalReverseHardLimitSwitch : return "LocalReverseHardLimitSwitch";
        case ReverseSoftLimitSwitch : return "LocalReverseSoftLimitSwitch";
        case DefaultRate            : return "DefaultRate";
        case NeutralMode            : return "NeutralMode";
        case Disconnected           : return "Disconnected";
        case RemoteReverseHardLimitSwitch : return "RemoteReverseHardLimitSwitch";
        case RemoteForwardHardLimitSwitch : return "RemoteForwardHardLimitSwitch";
        default:
                return "Unknown Capability " + capability;
        }
	}
	
	/*
	 * Get the name of the axis
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/*
	 * Find out if a capability is supported
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getCapabilities()
	 */
	@Override
	public long getCapabilities() {
		return this.capabilities;
	}
	
	/*
	 * Find out if all capabilities are supported
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#hasAll(long)
	 */
	@Override
	public boolean hasAll(long capabilities) {
		return (this.capabilities & capabilities) == capabilities;
	}
	/*
	 * Find out if any capabilities are supported
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#hasAny(long)
	 */
	@Override
	public boolean hasAny(long capabilities) {
		return (this.capabilities & capabilities) != 0;
	}

	/*
	 * Assert that a capability is supported
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#requireAll(long)
	 */
	@Override
	public void requireAll(long capabilities) {
		if(!hasAll(capabilities)) {
			// TODO: Add friendly string representations?
			throw new UnsupportedOperationException("This axis does not support the requested function.");
		}
	}
	
	/*
	 * Assert that a capability is supported
	 */
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#requireAny(long)
	 */
	@Override
	public void requireAny(long capabilities) {
		if(!hasAny(capabilities)) {
			// TODO: Add friendly string representations?
			throw new UnsupportedOperationException("This axis does not support the requested function.");
		}
	}

	/*
	 * If ControlPosition or ControlDirection is supported, return the native units of measure for motor position/distance
	 */
	private final LengthUOM nativeMotorLengthUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeMotorLengthUOM()
	 */
	@Override
	public final LengthUOM getNativeMotorLengthUOM() {
		requireAny(ControlPosition|ControlDirection);
		return nativeMotorLengthUOM;
	}

	/*
	 * If ReadPosition is supported, return the native units of measure for sensor position/distance
	 */
	private final LengthUOM nativeSensorLengthUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeSensorLengthUOM()
	 */
	@Override
	public final LengthUOM getNativeSensorLengthUOM() {
		requireAll(ReadPosition);
		return nativeSensorLengthUOM;
	}

	/*
	 * If SensorToMotorScale is supported, return the scaling factor to convert from motor to sensor position/rate
	 */
	private final Double sensorToDriveScalingMultiplier;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getSensorToDriveScalingFactor()
	 */
	@Override
	public final double getSensorToDriveScalingMultiplier() {
		requireAll(SensorToDriveScale);
		return sensorToDriveScalingMultiplier;
	}
	
	/*
	 * If ReadRate is supported, return the native display units for user interaction
	 */
	private final RateUOM nativeDisplayRateUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeDisplayRateUOM()
	 */
	@Override
	public final RateUOM getNativeDisplayRateUOM() {
		requireAll(ReadRate);
		return nativeDisplayRateUOM;
	}
	/*
	 * If ControlRate, return the motor native units for control
	 */
	private final RateUOM nativeMotorRateUOM; 
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeMotorRateUOM()
	 */
	@Override
	public final RateUOM getNativeMotorRateUOM() {
		requireAny(ControlRate);
		return nativeMotorRateUOM;
	}
	/*
	 * If ReadRate is supported, return the native sensor units 
	 */
	private final RateUOM nativeSensorRateUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeSensorRateUOM()
	 */
	@Override
	public final RateUOM getNativeSensorRateUOM() {
		requireAll(ReadRate);
		return nativeSensorRateUOM;
	}	
	/*
	 * If Forward and ControlRate and LimitRate are supported, return the minimum forward rate allowed 
	 */
	private final Rate minimumForwardRate;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getMinimumForwardRate()
	 */
	@Override
	public final Rate getMinimumForwardRate() {
		requireAll(Forward|ControlRate|LimitRate);
		return minimumForwardRate;
	}
	/*
	 * If Forward and ControlRate and LimitRate are supported, return the maximum forward rate allowed 
	 */
	private final Rate maximumForwardRate;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getMaximumForwardRate()
	 */
	@Override
	public final Rate getMaximumForwardRate() {
		requireAll(Forward|ControlRate|LimitRate);
		return maximumForwardRate;
	}
	/*
	 * If Reverse and ControlRate and LimitRate are supported, return the minimum reverse rate allowed. 
	 */
	private final Rate minimumReverseRate; 
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getMinimumReverseRate()
	 */
	@Override
	public Rate getMinimumReverseRate() {
		requireAll(Reverse|ControlRate|LimitRate);
		return minimumReverseRate;
	}
	/*
	 * If Reverse and ControlRate and LimitRate are supported, return the minimum reverse rate allowed. 
	 */
	private final Rate maximumReverseRate;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getMaximumReverseRate()
	 */
	@Override
	public Rate getMaximumReverseRate() {
		requireAll(Reverse|ControlRate|LimitRate);
		return maximumReverseRate;
	}
	/*
	 * If ControlRate or LimitRate are supported, return the a unit of measure representing 1% of maximum output 
	 */
	private final RateUOM percentageRateUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getPercentageRateUOM()
	 */
	@Override
	public RateUOM getPercentageRateUOM() {
		requireAny(ControlRate|LimitRate);
		return percentageRateUOM;
	}
	/*
	 * If ControlRate is supported, return the default rate. 
	 */
	private final Rate defaultRate;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getDefaultRate()
	 */
	@Override
	public Rate getDefaultRate() {
		requireAll(ControlRate);
		return defaultRate;
	}

	/*
	 * If ReadRate or ReadPosition are supported, return whether the sensor is reversed
	 */
	private final Boolean sensorPhaseIsReversed;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getSensorPhaseIsReversed()
	 */
	@Override
	public final boolean getSensorPhaseIsReversed() {
		// NB: do not require Reverse, motor could be reversed even when we don't use reverse
		requireAny(ReadRate|ReadPosition);
		return sensorPhaseIsReversed;
	}
	/*
	 * If ControlRate or ControlPosition are supported, return whether the motor is reversed
	 */	
	private final Boolean motorPhaseIsReversed;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getMotorPhaseIsReversed()
	 */
	@Override
	public final boolean getMotorPhaseIsReversed() {
		// NB: do not require Reverse, motor could be reversed even when we don't use reverse
		requireAny(ControlRate|ControlPosition|ControlDirection);
		return motorPhaseIsReversed;
	}

	/*
	 * If ReadPosition is supported, return the native display units for length/position
	 */
	private final LengthUOM nativeDisplayLengthUOM;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNativeDisplayLengthUOM()
	 */
	@Override
	public final LengthUOM getNativeDisplayLengthUOM() {
		requireAny(ReadPosition);
		return nativeDisplayLengthUOM;
	}

	/*
	 * If Forward and ControlPosition and LimitPosition, return the forward limit
	 */
	private final Length forwardLimit;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getForwardLimit()
	 */
	@Override
	public final Length getForwardLimit() {
		requireAll(Forward|ControlPosition|LimitPosition);
		return forwardLimit;
	}

	/*
	 * If Reverse and ControlPosition and LimitPosition, return the forward limit
	 */
	private final Length reverseLimit;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getReverseLimit()
	 */
	@Override
	public final Length getReverseLimit() {
		requireAll(Reverse|ControlPosition|LimitPosition);
		return reverseLimit;
	}
	/*
	 * If Forward and ForwardHardLimitSwitch return forwardHardLimitSwitchResetsEncoder 
	 */
	private final Boolean forwardHardLimitSwitchResetsEncoder;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getForwardHardLimitSwitchResetsEncoder()
	 */
	@Override
	public final boolean getForwardHardLimitSwitchResetsEncoder() {
		requireAll(Forward|LocalForwardHardLimitSwitch);
		return forwardHardLimitSwitchResetsEncoder;
	}
	/*
	 * If Reverse and Reverse and ReverseHardLimitSwitch return reverseHardLimitSwitchResetsEncoder, 
	 */
	private final Boolean reverseHardLimitSwitchResetsEncoder;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getReverseHardLimitSwitchResetsEncoder()
	 */
	@Override
	public final boolean getReverseHardLimitSwitchResetsEncoder() {
		requireAll(Reverse|LocalReverseHardLimitSwitch);
		return reverseHardLimitSwitchResetsEncoder;
	}
	
	/*
	 * If Forward and ForwardHardLimitSwitch, return the behavior for forward hard limit switch
	 */
	private final LimitSwitchNormal forwardHardLimitSwitchNormal;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getForwardHardLimitSwitchNormal()
	 */
	@Override
	public final LimitSwitchNormal getForwardHardLimitSwitchNormal() {
		requireAll(Forward|LocalForwardHardLimitSwitch);
		return forwardHardLimitSwitchNormal;
	}
	/*
	 * If Reverse and ReverseHardLimitSwitch, return the behavior for forward hard limit switch
	 */
	private final LimitSwitchNormal reverseHardLimitSwitchNormal;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getReverseHardLimitSwitchNormal()
	 */
	@Override
	public final LimitSwitchNormal getReverseHardLimitSwitchNormal() {
		requireAll(Reverse|LocalReverseHardLimitSwitch);
		return reverseHardLimitSwitchNormal;
	}
	/*
	 * If Forward and ForwardSoftLimitSwitch, return forward soft limit 
	 */
	private final Length forwardSoftLimit;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getForwardSoftLimit()
	 */
	@Override
	public final Length getForwardSoftLimit() {
		requireAll(Forward|ForwardSoftLimitSwitch);
		return forwardSoftLimit;
	}
	/*
	 * If Reverse and ReverseSoftLimitSwitch, return reverse soft limit 
	 */
	private final Length reverseSoftLimit;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getReverseSoftLimit()
	 */
	@Override
	public final Length getReverseSoftLimit() {
		requireAll(Reverse|ReverseSoftLimitSwitch);
		return reverseSoftLimit;
	}
	private final com.ctre.phoenix.motorcontrol.NeutralMode neutralMode;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getNeutralMode()
	 */
	@Override
	public final NeutralMode getNeutralMode() {
		requireAll(NeutralMode);
		requireAny(ControlRate|ControlPosition);
		return neutralMode;
	}
	
	

	RemoteLimitSwitchSource remoteForwardHardLimitSwitchSource;
	@Override
	public RemoteLimitSwitchSource getRemoteForwardHardLimitSwitchSource() {
		requireAll(Forward|RemoteForwardHardLimitSwitch);
		return remoteForwardHardLimitSwitchSource;
	}

	RemoteLimitSwitchSource remoteReverseHardLimitSwitchSource;
	@Override
	public RemoteLimitSwitchSource getRemoteReverseHardLimitSwitchSource() {
		requireAll(Reverse|RemoteReverseHardLimitSwitch);
		return remoteReverseHardLimitSwitchSource;
	}

	Integer remoteForwardHardLimitSwitchDeviceId;
	@Override
	public int getRemoteForwardHardLimitSwitchDeviceId() {
		requireAll(Forward|RemoteForwardHardLimitSwitch);
		return remoteForwardHardLimitSwitchDeviceId;
	}

	Integer remoteReverseHardLimitSwitchDeviceId;
	@Override
	public int getRemoteReverseHardLimitSwitchDeviceId() {
		requireAll(Reverse|RemoteReverseHardLimitSwitch);
		return remoteForwardHardLimitSwitchDeviceId;
	}
	
	private final ICommandFactory<Motor> defaultCommandFactory;
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getDefaultCommandFactory()
	 */
	@Override
	public final ICommandFactory<Motor> getDefaultCommandFactory() {
		return defaultCommandFactory;
	}
	private final List<PIDConfiguration> pidConfigurations = new ArrayList<PIDConfiguration>();
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getPIDConfigurations()
	 */
	@Override
	public final List<PIDConfiguration> getPIDConfigurations() {
		return Collections.unmodifiableList(pidConfigurations);
	}
	/*
	 * Get the native units for this axis
	 */
	public MotorConfiguration(
			String name,
			int capabilities,
			LengthUOM nativeDisplayLengthUOM, // requireAny(ReadPosition|ControlPosition);
			LengthUOM nativeMotorLengthUOM, // requireAny(ControlPosition|ControlDirection)
			Boolean motorPhaseIsReversed, // requireAny(ControlRate|ControlPosition|ControlDirection)
			Boolean sensorPhaseIsReversed, // requireAny(ReadRate|ReadPosition)
			LengthUOM nativeSensorLengthUOM, // requireAll(ReadPosition)
			RateUOM nativeDisplayRateUOM,  // requireAll(ReadRate)
			RateUOM nativeMotorRateUOM, // requireAny(ControlRate);
			RateUOM nativeSensorRateUOM, //requireAll(ReadRate)
			Rate minimumForwardRate, // requireAll(Forward|ControlRate|LimitRate)
			Rate maximumForwardRate, // requireAll(Forward|ControlRate|LimitRate)
			Rate minimumReverseRate, // requireAll(Reverse|ControlRate|LimitRate)
			Rate maximumReverseRate, // requireAll(Reverse|ControlRate|LimitRate)
			Double sensorToDriveScalingMultiplier, // requireAll(SensorToDriveScale)
			Length forwardLimit, // requireAll(Forward|ControlPosition|LimitPosition)
			Length reverseLimit, // requireAll(Reverse|ControlPosition|LimitPosition)
			LimitSwitchNormal forwardHardLimitSwitchNormal, // requireAll(Forward|ForwardSoftLimitSwitch)
			Boolean forwardHardLimitSwitchResetsEncoder, // requireAll(Forward|ForwardHardLimitSwitch)
			LimitSwitchNormal reverseHardLimitSwitchNormal, // requireAll(Reverse|ReverseSoftLimitSwitch)
			Boolean reverseHardLimitSwitchResetsEncoder, // requireAll(Reverse|ReverseHardLimitSwitch)
			Length forwardSoftLimit, // requireAll(Forward|ForwardSoftLimitSwitch)
			Length reverseSoftLimit, // requireAll(Reverse|ReverseSoftLimitSwitch)
			Rate defaultRate, // requireAll(ControlRate)
			NeutralMode neutralMode, // requireAll(NeutralMode), requireAny(ControlRate|ControlPosition)
			RateUOM percentageRateUOM, // require(ControlRate|LimitRate)
			RemoteLimitSwitchSource remoteForwardHardLimitSwitchSource, // requireAll(Forward|RemoteForwardHardLimitSwitch)
			Integer remoteForwardHardLimitSwitchDeviceId, // requireAll(Forward|RemoteForwardHardLimitSwitch)
			RemoteLimitSwitchSource remoteReverseHardLimitSwitchSource, // requireAll(Reverse|ReverseForwardHardLimitSwitch)
			Integer remoteReverseHardLimitSwitchDeviceId, // requireAll(Reverse|ReverseForwardHardLimitSwitch)
			ICommandFactory<Motor> defaultCommandFactory, // no requirements
			List<PIDConfiguration> pidConfigurations
			)
	{
		this.name = name;
		this.capabilities = capabilities;
		this.nativeMotorLengthUOM = nativeMotorLengthUOM;
		this.sensorPhaseIsReversed = sensorPhaseIsReversed;
		this.motorPhaseIsReversed = motorPhaseIsReversed;
		this.nativeSensorLengthUOM = nativeSensorLengthUOM;
		this.sensorToDriveScalingMultiplier = sensorToDriveScalingMultiplier;
		this.nativeDisplayRateUOM = nativeDisplayRateUOM;
		this.nativeMotorRateUOM = nativeMotorRateUOM;
		this.nativeSensorRateUOM = nativeSensorRateUOM;
		this.minimumForwardRate = minimumForwardRate;
		this.maximumForwardRate = maximumForwardRate;
		this.minimumReverseRate = minimumReverseRate;
		this.maximumReverseRate = maximumReverseRate;
		this.nativeDisplayLengthUOM = nativeDisplayLengthUOM;
		this.forwardLimit = forwardLimit;
		this.reverseLimit = reverseLimit;
		this.forwardHardLimitSwitchNormal = forwardHardLimitSwitchNormal;
		this.forwardHardLimitSwitchResetsEncoder = forwardHardLimitSwitchResetsEncoder;
		this.reverseHardLimitSwitchNormal = reverseHardLimitSwitchNormal;
		this.reverseHardLimitSwitchResetsEncoder = reverseHardLimitSwitchResetsEncoder;
		this.forwardSoftLimit = forwardSoftLimit;
		this.reverseSoftLimit = reverseSoftLimit;
		this.defaultRate = defaultRate;
		this.neutralMode = neutralMode;
		this.percentageRateUOM = percentageRateUOM;
		this.defaultCommandFactory = defaultCommandFactory;
		this.remoteForwardHardLimitSwitchSource = remoteForwardHardLimitSwitchSource;
		this.remoteForwardHardLimitSwitchDeviceId = remoteForwardHardLimitSwitchDeviceId;
		this.remoteReverseHardLimitSwitchSource = remoteReverseHardLimitSwitchSource;
		this.remoteReverseHardLimitSwitchDeviceId = remoteReverseHardLimitSwitchDeviceId;
		if(pidConfigurations != null) {
			this.pidConfigurations.addAll(pidConfigurations);
		}
		validateConfiguration();
	}
	public static String listCapabilities(long capabilities, String prefix, String separator, String suffix) {
		StringBuffer buf = new StringBuffer();
		boolean wroteAtLeastOne = false; 
		for(int i = 0; i <= MAX_CAPABILITY; i++) {
			int c = (1 << i);
			if((capabilities & c) != 0) {
				buf.append(prefix != null ? prefix : "").append((wroteAtLeastOne && separator != null) ? separator : "").append(getCapabilityName(c)).append(suffix != null ? suffix : "");
				wroteAtLeastOne = true; 
			}
		}
		return buf.toString();
	}
	public static String listCapabilitiesCSV(int capabilities) {
		return listCapabilities(capabilities, "", ",", "");
	}

	private void checkParameter(String name, Object value, int requireAny, int requireAll) {
		if (value != null) { 
			if(requireAny != 0 && !hasAny(requireAny)) {
				throw new IllegalArgumentException("Indicated capabilities do not require " + name + ".\nThe following capabilities use " + name + ":\n" + listCapabilitiesCSV(requireAny));
			}
			if(requireAll != 0 && !hasAll(requireAll)) {
				throw new IllegalArgumentException("Indicated capabilities do not require " + name + ".\nThe " + name + " parameter is ONLY required if you have ALL of these capabilities:\n" + listCapabilitiesCSV(requireAll));
			}
		} else {
			if(requireAny != 0 && hasAny(requireAny)) {
				throw new IllegalArgumentException("Indicated capabilities require " + name + ".\nThe following capabilities use " + name + ":\n" + listCapabilitiesCSV(requireAny));
			}
			if(requireAll != 0 && hasAll(requireAll)) {
				throw new IllegalArgumentException("Indicated capabilities require " + name + ".\nThe parameter " + name + " is required if you have all the capabilities:\n" + listCapabilitiesCSV(requireAll));
			}
		}
	}

	private void validateCapabilityDependency(int capability, int requireAny, int requireAll) {
		
		if(hasAll(capability) && !hasAll(requireAll)) {
			for(int i = 0; i <= MAX_CAPABILITY; i++) {
				int c = (1 << i);
				if((requireAll & c) != 0 && (capabilities & c) == 0) {
					throw new IllegalArgumentException("The capability " + getCapabilityName(capability) + " depends on " + getCapabilityName(c));	
				}
			}
		}
		
		if(hasAll(capability) && requireAny != 0 && !hasAny(requireAny)) {
			throw new IllegalArgumentException("The " + getCapabilityName(capability) + " capability requires at least one of:\n" + listCapabilitiesCSV(requireAny));
		}
	}

	private void validateCapabilityDependency(int capability, int requireAny, int requireAll, int requireNone) {
		validateCapabilityDependency(capability,requireAny,requireAll);
		if(requireNone != 0 && hasAny(requireNone)) {
			throw new IllegalArgumentException("The " + getCapabilityName(capability) + " capability is incompatible with:\n" + listCapabilitiesCSV(requireNone & capabilities));
		}
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#validateConfiguration()
	 */
	@Override
	public void validateConfiguration() {
		validateCapabilityDependency(Forward, ControlDirection|ReadDirection, 0);
		validateCapabilityDependency(Reverse, ControlDirection|ReadDirection, 0);
		validateCapabilityDependency(ControlDirection, 0, ReadDirection|Forward|Reverse);
		validateCapabilityDependency(LocalForwardHardLimitSwitch, 0, Forward|ControlDirection, RemoteForwardHardLimitSwitch);
		validateCapabilityDependency(ForwardSoftLimitSwitch, 0, Forward|ControlDirection);
		validateCapabilityDependency(LocalReverseHardLimitSwitch, 0, Reverse|ControlDirection, RemoteReverseHardLimitSwitch);
		validateCapabilityDependency(ReverseSoftLimitSwitch, 0, Reverse|ControlDirection);
		validateCapabilityDependency(ControlPosition, 0, SensorToDriveScale|ReadPosition);
		validateCapabilityDependency(ControlRate, 0, 0); // maybe open loop control
		validateCapabilityDependency(LimitPosition, 0, ControlPosition|ReadPosition);
		validateCapabilityDependency(LimitRate, 0, ControlRate);
		validateCapabilityDependency(SensorToDriveScale, ReadRate|ReadPosition, 0);
		validateCapabilityDependency(ReadDirection, 0, 0);
		validateCapabilityDependency(ReadPosition, 0, SensorToDriveScale);
		validateCapabilityDependency(ReadRate, 0, 0);
		validateCapabilityDependency(ReadRate, 0, ControlRate);
		validateCapabilityDependency(NeutralMode, ControlRate|ControlPosition, 0);
		validateCapabilityDependency(RemoteForwardHardLimitSwitch, 0, Forward|ControlDirection, LocalForwardHardLimitSwitch);
		validateCapabilityDependency(RemoteReverseHardLimitSwitch, 0, Reverse|ControlDirection, LocalForwardHardLimitSwitch);
		// Now validate settings
		checkParameter("nativeDisplayLengthUOM", nativeDisplayLengthUOM, 0, 0);
		checkParameter("nativeMotorLengthUOM", nativeMotorLengthUOM, ControlPosition|ControlDirection, 0);  
		checkParameter("motorPhaseIsReversed", motorPhaseIsReversed, ControlRate|ControlPosition|ControlDirection, 0);
		checkParameter("sensorPhaseIsReversed", sensorPhaseIsReversed, ReadRate|ReadPosition, 0);
		checkParameter("nativeSensorLengthUOM", nativeSensorLengthUOM, ReadPosition, 0);
		checkParameter("nativeDisplayRateUOM", nativeDisplayRateUOM, 0, 0);
		checkParameter("nativeMotorRateUOM", nativeMotorRateUOM, ControlRate, 0);
		checkParameter("nativeSensorRateUOM", nativeSensorRateUOM, ReadRate, 0);
		checkParameter("minimumForwardRate", minimumForwardRate, 0, Forward|ControlRate|LimitRate);
		checkParameter("maximumForwardRate", maximumForwardRate, 0, Forward|ControlRate|LimitRate);
		checkParameter("minimumReverseRate", minimumReverseRate, 0, Reverse|ControlRate|LimitRate);
		checkParameter("maximumReverseRate", maximumReverseRate, 0, Reverse|ControlRate|LimitRate);
		checkParameter("sensorToDriveScalingFactor", sensorToDriveScalingMultiplier, SensorToDriveScale, 0);
		checkParameter("forwardLimit", forwardLimit, 0, Forward|ControlPosition|LimitPosition);
		checkParameter("reverseLimit", reverseLimit, 0, Reverse|ControlPosition|LimitPosition);
		checkParameter("forwardHardLimitSwitchNormal", forwardHardLimitSwitchNormal, 0, Forward|LocalForwardHardLimitSwitch);
		checkParameter("forwardHardLimitSwitchResetsEncoder", forwardHardLimitSwitchResetsEncoder, 0, Forward|LocalForwardHardLimitSwitch);
		checkParameter("reverseHardLimitSwitchNormal", reverseHardLimitSwitchNormal, 0, Reverse|LocalReverseHardLimitSwitch);
		checkParameter("reverseHardLimitSwitchResetsEncoder", reverseHardLimitSwitchResetsEncoder, 0, Reverse|LocalReverseHardLimitSwitch);
		checkParameter("forwardSoftLimit", forwardSoftLimit, 0, Forward|ForwardSoftLimitSwitch);
		checkParameter("reverseSoftLimit", reverseSoftLimit, 0, Reverse|ReverseSoftLimitSwitch);
		checkParameter("defaultRate", defaultRate, 0, ControlRate);
		checkParameter("neutralMode", neutralMode, ControlRate|ControlPosition, NeutralMode);
		checkParameter("percentageRateUOM", percentageRateUOM, ControlRate|LimitRate, 0);
		checkParameter("defaultCommandFactory", defaultCommandFactory, 0, 0); // no requirements
		checkParameter("remoteForwardHardLimitSwitchSource", remoteForwardHardLimitSwitchSource, 0, RemoteForwardHardLimitSwitch); // no requirements
		checkParameter("remoteForwardHardLimitSwitchDeviceId", remoteForwardHardLimitSwitchDeviceId, 0, RemoteForwardHardLimitSwitch); // no requirements
		checkParameter("remoteReverseHardLimitSwitchSource", remoteReverseHardLimitSwitchSource, 0, RemoteReverseHardLimitSwitch); // no requirements
		checkParameter("remoteReverseHardLimitSwitchDeviceId", remoteReverseHardLimitSwitchDeviceId, 0, RemoteReverseHardLimitSwitch); // no requirements
		if(hasAll(ForwardSoftLimitSwitch) && forwardSoftLimit.getValue() > forwardLimit.getValue() && !hasAll(LocalForwardHardLimitSwitch)) {
			throw new IllegalArgumentException("forwardSoftLimit " + forwardSoftLimit + " exceeds forwardLimit " + forwardLimit + ".  Soft limits must be within physical range of motion.");
		}
		if(hasAll(ReverseSoftLimitSwitch) && reverseSoftLimit.getValue() < reverseLimit.getValue()&& !hasAll(LocalReverseHardLimitSwitch)) {
			throw new IllegalArgumentException("reverseSoftLimit " + reverseSoftLimit + " exceeds reverseLimit " + reverseLimit + ".  Soft limits must be within physical range of motion.");
		}
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#dumpDescription()
	 */
	@Override
	public void dumpDescription() {
		System.out.println(getDescription());
	}

	@SuppressWarnings("rawtypes")
	private String describeUOM(UOM uom) {
		return (uom != null ? (uom + " (" + uom.getCanonicalValue() + ")") : null);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String describeValue(Value value, UOM alt) {
		return (value != null ? (value + " (" + value.convertTo(alt) + ")") : null);
	}
	/* (non-Javadoc)
	 * @see org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration#getDescription()
	 */
	@Override
	public String getDescription() {
		StringBuffer buf = new StringBuffer();
		buf
		.append("----------------------------------------------------------------------------\n")
		.append("                            Motor Configuration                             \n")
		.append("----------------------------------------------------------------------------\n")
		.append("Name.................................." + getName() + "\n")
		.append("\n")
		.append("Capabilities.........................." + listCapabilities(capabilities, "", "\n                                      ", "") + "\n")
		.append("\n")
		.append("Length Units:\n")
		.append("\n")
		.append("nativeDisplayLengthUOM................" + describeUOM(nativeDisplayLengthUOM) + "\n")
		.append("nativeMotorLengthUOM.................." + describeUOM(nativeMotorLengthUOM) + "\n")
		.append("nativeSensorLengthUOM................." + describeUOM(nativeSensorLengthUOM) + "\n")
		.append("\n")
		.append("Rate Units:\n")
		.append("\n")
		.append("nativeDisplayRateUOM.................." + describeUOM(nativeDisplayRateUOM) + "\n")
		.append("nativeMotorRateUOM...................." + describeUOM(nativeMotorRateUOM) + "\n")
		.append("nativeSensorRateUOM..................." + describeUOM(nativeSensorRateUOM) + "\n")
		.append("percentageRateUOM....................." + describeUOM(percentageRateUOM) + "\n")
		.append("\n")
		.append("Rates:\n")
		.append("\n")
		.append("defaultRate..........................." + describeValue(defaultRate, nativeMotorRateUOM) + "\n")
		.append("minimumForwardRate...................." + describeValue(minimumForwardRate, nativeMotorRateUOM) + "\n")
		.append("maximumForwardRate...................." + describeValue(maximumForwardRate, nativeMotorRateUOM) + "\n")
		.append("minimumReverseRate...................." + describeValue(minimumReverseRate, nativeMotorRateUOM) + "\n")
		.append("maximumReverseRate...................." + describeValue(maximumReverseRate, nativeMotorRateUOM) + "\n")
		.append("\n")
		.append("Limits:\n")
		.append("\n")
		.append("forwardLimit.........................." + describeValue(forwardLimit, nativeMotorLengthUOM) + "\n")
		.append("reverseLimit.........................." + describeValue(reverseLimit, nativeMotorLengthUOM) + "\n")
		.append("\n")
		.append("Limits:\n")
		.append("\n")
		.append("forwardSoftLimit......................" + describeValue(forwardSoftLimit, nativeMotorLengthUOM) + "\n")
		.append("reverseSoftLimit......................" + describeValue(reverseSoftLimit, nativeMotorLengthUOM) + "\n")
		.append("forwardHardLimitSwitchBehavior........" + forwardHardLimitSwitchNormal + "\n")
		.append("forwardHardLimitSwitchResetsEncoder..." + forwardHardLimitSwitchResetsEncoder + "\n")
		.append("reverseHardLimitSwitchBehavior........" + reverseHardLimitSwitchNormal + "\n")
		.append("reverseHardLimitSwitchResetsEncoder..." + reverseHardLimitSwitchResetsEncoder + "\n")
		.append("\n")
		.append("Scaling:\n")
		.append("\n")
		.append("sensorPhaseIsReversed................." + sensorPhaseIsReversed + "\n")
		.append("motorPhaseIsReversed.................." + motorPhaseIsReversed + "\n")
		.append("sensorToDriveScalingMultiplier........" + sensorToDriveScalingMultiplier + "\n")
		.append("\n")
		.append("Miscellaneous:\n")
		.append("\n")
		.append("neutralMode..........................." + neutralMode + "\n")
		.append("defaultCommandFactory................." + defaultCommandFactory + "\n")
		.append("----------------------------------------------------------------------------\n")
		;
		return buf.toString();
	}
	
	@Override
	public boolean getHasSoftLimit(Direction direction) {
		if(direction.isPositive()) {
			return hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.ForwardSoftLimitSwitch); 
		} else {
			return hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.ReverseSoftLimitSwitch);
		}
	}
	
	@Override
	public Length getSoftLimit(Direction direction) {
		if(getHasSoftLimit(direction)) {
			return direction.isPositive() ? getForwardSoftLimit() : getReverseSoftLimit();
		}
		return null;
	}	

	@Override
	public boolean isDisconnected() {
		return hasAll(IMotorConfiguration.Disconnected);
	}
	@Override
	public boolean getHasHardOrSoftLimit(Direction direction) {
		return getHasHardLimit(direction) || getHasSoftLimit(direction);
	}
	
	@Override
	public Length getHardLimit(Direction direction) {
		if(getHasHardLimit(direction)) {
			return direction.isPositive() ? getForwardLimit() : getReverseLimit(); 
		}
		return null;
	}

	@Override
	public Length getPhysicalLimit(Direction direction) {
		return direction.isPositive() ? getForwardLimit() : getReverseLimit();
	}

	@Override
	public boolean getHasHardLimit(Direction direction) {
		if(direction.isPositive()) {
			return hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition) 
					&& hasAny(IMotorConfiguration.LocalForwardHardLimitSwitch|IMotorConfiguration.RemoteForwardHardLimitSwitch)
					&& getForwardHardLimitSwitchNormal() != LimitSwitchNormal.Disabled;
					
		} else {
			return hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition) 
					&& hasAny(IMotorConfiguration.LocalReverseHardLimitSwitch|IMotorConfiguration.RemoteReverseHardLimitSwitch)
					&& getReverseHardLimitSwitchNormal() != LimitSwitchNormal.Disabled;
		}
	}

	@Override
	public Rate getMaximumRate(Direction direction) {
		return direction.isPositive()
				? getMaximumForwardRate()
				: getMaximumReverseRate();
	}
	@Override
	public Rate getMinimumRate(Direction direction) {
		return direction.isPositive()
				? getMinimumForwardRate()
				: getMinimumReverseRate();
	}
	@Override
	public boolean getHasHardLimitSwitchResetsEncoder(Direction direction) {
		return getHasHardLimit(direction) && direction.isPositive()
				? getForwardHardLimitSwitchResetsEncoder()
				: getReverseHardLimitSwitchResetsEncoder();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Units Helpers
	 * ---------------------------------------------------------------------------------------------- */
	
	@Override
	public final Length toSensorUnits(Length v) {
		return v == null ? null : createSensorLength(v.convertTo(getNativeSensorLengthUOM()).getValue()); // NB: Round to the nearest pulse
	}
	@Override
	public final Length toDisplayUnits(Length v) {
		return v == null ? null : v.convertTo(getNativeDisplayLengthUOM());
	}
	@Override
	public final Rate toSensorUnits(Rate v) {
		return v == null ? null : createSensorRate(v.convertTo(getNativeSensorRateUOM()).getValue()); // NB: Round to the nearest pulse/100us
	}
	@Override
	public final Rate toDisplayUnits(Rate v) {
		return v == null ? null : v.convertTo(getNativeDisplayRateUOM());
	}
	@Override
	public final Length createSensorLength(double v) {
		return getNativeSensorLengthUOM().create(Math.round(v)); // NB: Round "encoder pulses" to the nearest pulse on the way in
	}
	@Override
	public final Length createDisplayLength(double v) {
		return getNativeDisplayLengthUOM().create(v);
	}
	@Override
	public final Rate createSensorRate(double v) { // NB: Round to the nearest pulse rate
		return getNativeSensorRateUOM().create(Math.round(v));
	}
	@Override
	public final Rate createDisplayRate(double v) {
		return getNativeDisplayRateUOM().create(v);
	}
	@Override
	public boolean isHardLimitExceeded(IMotor motor, Direction direction) {
		return getHasHardLimit(direction) && Length.isLimitExceeded(direction, toSensorUnits(getHardLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean isHardLimitReached(IMotor motor, Direction direction) {
		return getHasHardLimit(direction) && Length.isLimitReached(direction, toSensorUnits(getHardLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean isHardLimitNeedingCalibration(IMotor motor, Direction direction) {
		return getHasHardLimit(direction) && Length.isLimitReached(direction, toSensorUnits(getHardLimit(direction)), motor.getCurrentPosition()) 
				&& !motor.getCurrentHardLimitSwitchStatus(direction);
	}
	@Override
	public boolean isSoftLimitExceeded(IMotor motor, Direction direction) {
		return getHasSoftLimit(direction) && Length.isLimitExceeded(direction, toSensorUnits(getSoftLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean isSoftLimitReached(IMotor motor, Direction direction) {
		return getHasSoftLimit(direction) && Length.isLimitReached(direction, toSensorUnits(getSoftLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean isPhysicalLimitExceeded(IMotor motor, Direction direction) {
		return Length.isLimitExceeded(direction, toSensorUnits(getPhysicalLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean isPhysicalLimitReached(IMotor motor, Direction direction) {
		return Length.isLimitReached(direction, toSensorUnits(getPhysicalLimit(direction)), motor.getCurrentPosition());
	}
	@Override
	public boolean getCurrentSoftLimitSwitchStatus(IMotor motor, Direction direction) {
		if(getHasSoftLimit(direction)) {
			return Length.isLimitExceeded(direction, toSensorUnits(getSoftLimit(direction)), motor.getCurrentPosition());
		}
		return false;
	}
	@Override
	public String getDescriptionWithBothUnits(Length v) {
		return toSensorUnits(v) + " (or " + toDisplayUnits(v) + ")";
	}
	@Override
	public String getDescriptionWithBothUnits(Rate v) {
		return toSensorUnits(v) + " (or " + toDisplayUnits(v) + ")";
	}
	
	/*
	 * Check the indicated hard limit switch and report if it needs calibration
	 */
	private void checkForHardLimitError(IMotor motor, Direction targetDirection) {
		if(isHardLimitNeedingCalibration(motor, targetDirection)) {
			Logger.info(motor + " WARNING WARNING WARNING - " + targetDirection + " HARD LIMIT has drifted.  We're past the limit, but the switch is not active.  Limit: " + getDescriptionWithBothUnits(getHardLimit(targetDirection)) + " Position: " + getDescriptionWithBothUnits(motor.getCurrentPosition()));
		}
		/* NB: We check for sensor is active, but sensor position != 0 elsewhere.  It can actually have a range.  We keep setting to zero until the motor
		       moves away from the limit switch.  There may be a range of sensor positions that still trigger the switch.  So we can't just check to see if it's 
		       position == "the limit", we do that another way.  See autoResetSensorPositionIfNecessary
		*/
	}
	/*
	 * Check the indicated hard limit switch and report if it needs calibration
	 */
	private void checkForSoftLimitError(IMotor motor, Direction targetDirection) {
		if(isSoftLimitExceeded(motor, targetDirection)) {
			Logger.info(motor + " WARNING WARNING WARNING - " + targetDirection + " soft limit has been exceeded.  Limit: " + getDescriptionWithBothUnits(getSoftLimit(targetDirection)) + " Position: " + getDescriptionWithBothUnits(motor.getCurrentPosition()));
		}
	}
	/*
	 * Check the indicated specified limit has been exceeded
	 */
	private void checkForPhysicalLimitError(IMotor motor, Direction targetDirection) {
		if(isPhysicalLimitExceeded(motor, targetDirection)) {
			Logger.info(motor + " WARNING WARNING WARNING - " + targetDirection + " configured limit has been exceeded.  Update the limits.  Limit: " + getDescriptionWithBothUnits(getPhysicalLimit(targetDirection)) + " Position: " + getDescriptionWithBothUnits(motor.getCurrentPosition()));
		}
	}
	@Override
	public void checkForLimitErrors(IMotor motor) {
		checkForHardLimitError(motor, Direction.FORWARD);
		checkForHardLimitError(motor, Direction.REVERSE);
		checkForSoftLimitError(motor, Direction.FORWARD);
		checkForSoftLimitError(motor, Direction.REVERSE);
		checkForPhysicalLimitError(motor, Direction.FORWARD);
		checkForPhysicalLimitError(motor, Direction.REVERSE);
	}
	protected static int SENSOR_RESET_TOLERANCE_PULSES = 50;
	
	@Override
	public Length getSensorPositionIfResetIsNecessary(IMotor motor, Direction direction) {
		Length newEncoderValue = null;
		Length currentPosition = toSensorUnits(motor.getCurrentPosition());
		IMotorState targetState = motor.getTargetState();
		MotorOperation operation = targetState.getOperation();
		if(getHasHardLimitSwitchResetsEncoder(direction) && motor.getCurrentHardLimitSwitchStatus(direction)) {
			// Is the current sensor position error greater than SENSOR_RESET_TOLERANCE_PULSES, or is it good enough?
			if(Math.abs(currentPosition.getValue() - toSensorUnits(getHardLimit(direction)).getValue()) > SENSOR_RESET_TOLERANCE_PULSES) {
				String message = "";
				message = motor + " " + direction + " limit switch encountered and position is not correct, ";
				// Error is bad...
				if(targetState.getHasTargetDirection() && !targetState.getTargetDirection().equals(direction)) {
					// .. but we're moving away
					message = message + "but we are moving away from the limit switch so we are NOT updating the sensor.";
				} else {
					// and we're going towards the hard limit...
					message = message + "and we will change the sensor value from " + currentPosition + " to " + toSensorUnits(getForwardLimit()) + "."; 
					newEncoderValue = toSensorUnits(getForwardLimit());
				}
				Logger.info(message);
			}
		}
		return newEncoderValue;
	}

	@Override
	public Length getSensorPositionIfResetIsNecessary(IMotor motor) {
		// NB: Favoring reverse here, because it's most common for us
		Length result = getSensorPositionIfResetIsNecessary(motor, Direction.REVERSE);
		if(result == null) {
			result = getSensorPositionIfResetIsNecessary(motor, Direction.FORWARD);
		}
		return result;
	}
	
}
