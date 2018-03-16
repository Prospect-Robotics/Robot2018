package org.usfirst.frc2813.Robot2018.motor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.command.Command;

/*
 * This class is an immutable description of an axis as used by a motor, a subsystem's API, or a sensor.
 * The idea is to encapsulate everything about the axis so that the subsystem can completely initialize 
 * itself and handle all conversions, scaling, formatting, limits, validation, etc... automatically with
 * the minimal amount of data specified.
 */
public class MotorConfiguration {
	
	// Common state
	private final String name;
	// Capabilities
	private final int capabilities;
	
	//     Capabilities
	public static final int ControlDirection                    = 1 <<   0;
	public static final int ControlPosition                     = 1 <<   1;
	public static final int ControlRate                         = 1 <<   2;
	public static final int Forward                             = 1 <<   3;
	public static final int ForwardHardLimitSwitch              = 1 <<   4;
	public static final int ForwardSoftLimitSwitch              = 1 <<   5;
	public static final int LimitPosition                       = 1 <<   6;
	public static final int LimitRate                           = 1 <<   7;
	public static final int SensorToDriveScale                  = 1 <<   8;
	public static final int ReadDirection                       = 1 <<   9;
	public static final int ReadPosition                        = 1 <<  10;
	public static final int ReadRate                            = 1 <<  11;
	public static final int Reverse                             = 1 <<  12;
	public static final int ReverseHardLimitSwitch              = 1 <<  13;
	public static final int ReverseSoftLimitSwitch              = 1 <<  14;
	public static final int DefaultRate                         = 1 <<  15;
	public static final int NeutralMode                         = 1 <<  16;
	public static final int Disconnected                        = 1 <<  31;

	public static final int MAX_CAPABILITY = 31;

	public static String getCapabilityName(int capability) {
        switch(capability) {
        case ControlDirection       : return "ControlDirection";
        case ControlPosition        : return "ControlPosition";
        case ControlRate            : return "ControlRate";
        case Forward                : return "Forward";
        case ForwardHardLimitSwitch : return "ForwardHardLimitSwitch";
        case ForwardSoftLimitSwitch : return "ForwardSoftLimitSwitch";
        case LimitPosition          : return "LimitPosition";
        case LimitRate              : return "LimitRate";
        case SensorToDriveScale     : return "SensorToDriveScale";
        case ReadDirection          : return "ReadDirection";
        case ReadPosition           : return "ReadPosition";
        case ReadRate               : return "ReadRate";
        case Reverse                : return "Reverse";
        case ReverseHardLimitSwitch : return "ReverseHardLimitSwitch";
        case ReverseSoftLimitSwitch : return "ReverseSoftLimitSwitch";
        case DefaultRate            : return "DefaultRate";
        case NeutralMode            : return "NeutralMode";
        case Disconnected           : return "Disconnected";
        default:
                return "Unknown Capability " + capability;
        }
	}
	
	/*
	 * Get the name of the axis
	 */
	public String getName() {
		return name;
	}

	/*
	 * Find out if a capability is supported
	 */
	public boolean has(long capability) {
		return hasAll(capability);
	}
	
	/*
	 * Find out if a capability is supported
	 */
	public long getCapabilities(long capabilities) {
		return (this.capabilities & capabilities);
	}
	
	/*
	 * Find out if all capabilities are supported
	 */
	public boolean hasAll(long capabilities) {
		return (this.capabilities & capabilities) == capabilities;
	}
	/*
	 * Find out if any capabilities are supported
	 */
	public boolean hasAny(long capabilities) {
		return (this.capabilities & capabilities) != 0;
	}

	/*
	 * Assert that a capability is supported
	 */
	public void requireAll(long capabilities) {
		if(!hasAll(capabilities)) {
			// TODO: Add friendly string representations?
			throw new UnsupportedOperationException("This axis does not support the requested function.");
		}
	}
	
	/*
	 * Assert that a capability is supported
	 */
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
	public final LengthUOM getNativeMotorLengthUOM() {
		requireAny(ControlPosition|ControlDirection);
		return nativeMotorLengthUOM;
	}

	/*
	 * If ReadPosition is supported, return the native units of measure for sensor position/distance
	 */
	private final LengthUOM nativeSensorLengthUOM;
	public final LengthUOM getNativeSensorLengthUOM() {
		requireAll(ReadPosition);
		return nativeSensorLengthUOM;
	}

	/*
	 * If SensorToMotorScale is supported, return the scaling factor to convert from motor to sensor position/rate
	 */
	private final Double sensorToDriveScalingFactor;
	public final double getSensorToDriveScalingFactor() {
		requireAll(SensorToDriveScale);
		return sensorToDriveScalingFactor;
	}
	
	/*
	 * If ReadRate is supported, return the native display units for user interaction
	 */
	private final RateUOM nativeDisplayRateUOM;
	public final RateUOM getNativeDisplayRateUOM() {
		requireAll(ReadRate);
		return nativeDisplayRateUOM;
	}
	/*
	 * If ControlRate, return the motor native units for control
	 */
	private final RateUOM nativeMotorRateUOM; 
	public final RateUOM getNativeMotorRateUOM() {
		requireAny(ControlRate);
		return nativeMotorRateUOM;
	}
	/*
	 * If ReadRate is supported, return the native sensor units 
	 */
	private final RateUOM nativeSensorRateUOM;
	public final RateUOM getNativeSensorRateUOM() {
		requireAll(ReadRate);
		return nativeSensorRateUOM;
	}	
	/*
	 * If Forward and ControlRate and LimitRate are supported, return the minimum forward rate allowed 
	 */
	private final Rate minimumForwardRate;
	public final Rate getMinimumForwardRate() {
		requireAll(Forward|ControlRate|LimitRate);
		return minimumForwardRate;
	}
	/*
	 * If Forward and ControlRate and LimitRate are supported, return the maximum forward rate allowed 
	 */
	private final Rate maximumForwardRate;
	public final Rate getMaximumForwardRate() {
		requireAll(Forward|ControlRate|LimitRate);
		return maximumForwardRate;
	}
	/*
	 * If Reverse and ControlRate and LimitRate are supported, return the minimum reverse rate allowed. 
	 */
	private final Rate minimumReverseRate; 
	public Rate getMinimumReverseRate() {
		requireAll(Reverse|ControlRate|LimitRate);
		return minimumReverseRate;
	}
	/*
	 * If Reverse and ControlRate and LimitRate are supported, return the minimum reverse rate allowed. 
	 */
	private final Rate maximumReverseRate;
	public Rate getMaximumReverseRate() {
		requireAll(Reverse|ControlRate|LimitRate);
		return maximumReverseRate;
	}
	/*
	 * If ControlRate or LimitRate are supported, return the a unit of measure representing 1% of maximum output 
	 */
	private final RateUOM percentageRateUOM;
	public RateUOM getPercentageRateUOM() {
		requireAny(ControlRate|LimitRate);
		return percentageRateUOM;
	}
	/*
	 * If ControlRate is supported, return the default rate. 
	 */
	private final Rate defaultRate;
	public Rate getDefaultRate() {
		requireAll(ControlRate);
		return defaultRate;
	}

	/*
	 * If ReadRate or ReadPosition are supported, return whether the sensor is reversed
	 */
	private final Boolean sensorPhaseIsReversed;
	public final boolean getSensorPhaseIsReversed() {
		// NB: do not require Reverse, motor could be reversed even when we don't use reverse
		requireAny(ReadRate|ReadPosition);
		return sensorPhaseIsReversed;
	}
	/*
	 * If ControlRate or ControlPosition are supported, return whether the motor is reversed
	 */	
	private final Boolean motorPhaseIsReversed;
	public final boolean getMotorPhaseIsReversed() {
		// NB: do not require Reverse, motor could be reversed even when we don't use reverse
		requireAny(ControlRate|ControlPosition|ControlDirection);
		return motorPhaseIsReversed;
	}

	/*
	 * If ReadPosition is supported, return the native display units for length/position
	 */
	private final LengthUOM nativeDisplayLengthUOM;
	public final LengthUOM getNativeDisplayLengthUOM() {
		requireAny(ReadPosition);
		return nativeDisplayLengthUOM;
	}

	/*
	 * If Forward and ControlPosition and LimitPosition, return the forward limit
	 */
	private final Length forwardLimit;
	public final Length getForwardLimit() {
		requireAll(Forward|ControlPosition|LimitPosition);
		return forwardLimit;
	}

	/*
	 * If Reverse and ControlPosition and LimitPosition, return the forward limit
	 */
	private final Length reverseLimit;
	public final Length getReverseLimit() {
		requireAll(Reverse|ControlPosition|LimitPosition);
		return reverseLimit;
	}
	/*
	 * If Forward and ForwardHardLimitSwitch return forwardHardLimitSwitchResetsEncoder 
	 */
	private final Boolean forwardHardLimitSwitchResetsEncoder;
	public final boolean getForwardHardLimitSwitchResetsEncoder() {
		requireAll(Forward|ForwardHardLimitSwitch);
		return forwardHardLimitSwitchResetsEncoder;
	}
	/*
	 * If Reverse and Reverse and ReverseHardLimitSwitch return reverseHardLimitSwitchResetsEncoder, 
	 */
	private final Boolean reverseHardLimitSwitchResetsEncoder;
	public final boolean getReverseHardLimitSwitchResetsEncoder() {
		requireAll(Reverse|ReverseHardLimitSwitch);
		return reverseHardLimitSwitchResetsEncoder;
	}
	
	/*
	 * If Forward and ForwardHardLimitSwitch, return the behavior for forward hard limit switch
	 */
	private final LimitSwitchNormal forwardHardLimitSwitchNormal;
	public final LimitSwitchNormal getForwardHardLimitSwitchNormal() {
		requireAll(Forward|ForwardHardLimitSwitch);
		return forwardHardLimitSwitchNormal;
	}
	/*
	 * If Reverse and ReverseHardLimitSwitch, return the behavior for forward hard limit switch
	 */
	private final LimitSwitchNormal reverseHardLimitSwitchNormal;
	public final LimitSwitchNormal getReverseHardLimitSwitchNormal() {
		requireAll(Reverse|ReverseHardLimitSwitch);
		return reverseHardLimitSwitchNormal;
	}
	/*
	 * If Forward and ForwardSoftLimitSwitch, return forward soft limit 
	 */
	private final Length forwardSoftLimit;
	public final Length getForwardSoftLimit() {
		requireAll(Forward|ForwardSoftLimitSwitch);
		return forwardSoftLimit;
	}
	/*
	 * If Reverse and ReverseSoftLimitSwitch, return reverse soft limit 
	 */
	private final Length reverseSoftLimit;
	public final Length getReverseSoftLimit() {
		requireAll(Reverse|ReverseSoftLimitSwitch);
		return reverseSoftLimit;
	}
	private final com.ctre.phoenix.motorcontrol.NeutralMode neutralMode;
	public final NeutralMode getNeutralMode() {
		requireAll(NeutralMode);
		requireAny(ControlRate|ControlPosition);
		return neutralMode;
	}
	private final ICommandFactory<Motor> defaultCommandFactory;
	public final ICommandFactory<Motor> getDefaultCommandFactory() {
		return defaultCommandFactory;
	}
	private final List<PIDConfiguration> pidConfigurations = new ArrayList<PIDConfiguration>();
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
			Double sensorToDriveScalingFactor, // requireAll(SensorToDriveScale)
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
		this.sensorToDriveScalingFactor = sensorToDriveScalingFactor;
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
		if(pidConfigurations != null) {
			this.pidConfigurations.addAll(pidConfigurations);
		}
		validateConfiguration();
	}
	public static String listCapabilities(int capabilities, String prefix, String separator, String suffix) {
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
		
		if(has(capability) && !hasAll(requireAll)) {
			for(int i = 0; i <= MAX_CAPABILITY; i++) {
				int c = (1 << i);
				if((requireAll & c) != 0 && (capabilities & c) == 0) {
					throw new IllegalArgumentException("The capability " + getCapabilityName(capability) + " depends on " + getCapabilityName(c));	
				}
			}
		}
		
		if(has(capability) && requireAny != 0 && !hasAny(requireAny)) {
			throw new IllegalArgumentException("The " + getCapabilityName(capability) + " capability requires at least one of:\n" + listCapabilitiesCSV(requireAny));
		}
	}

	public void validateConfiguration() {
		validateCapabilityDependency(Forward, ControlDirection|ReadDirection, 0);
		validateCapabilityDependency(Reverse, ControlDirection|ReadDirection, 0);
		validateCapabilityDependency(ControlDirection, 0, ReadDirection|Forward|Reverse);
		validateCapabilityDependency(ForwardHardLimitSwitch, 0, Forward|ControlDirection);
		validateCapabilityDependency(ForwardSoftLimitSwitch, 0, Forward|ControlDirection);
		validateCapabilityDependency(ReverseHardLimitSwitch, 0, Reverse|ControlDirection);
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
		checkParameter("sensorToDriveScalingFactor", sensorToDriveScalingFactor, SensorToDriveScale, 0);
		checkParameter("forwardLimit", forwardLimit, 0, Forward|ControlPosition|LimitPosition);
		checkParameter("reverseLimit", reverseLimit, 0, Reverse|ControlPosition|LimitPosition);
		checkParameter("forwardHardLimitSwitchNormal", forwardHardLimitSwitchNormal, 0, Forward|ForwardHardLimitSwitch);
		checkParameter("forwardHardLimitSwitchResetsEncoder", forwardHardLimitSwitchResetsEncoder, 0, Forward|ForwardHardLimitSwitch);
		checkParameter("reverseHardLimitSwitchNormal", reverseHardLimitSwitchNormal, 0, Reverse|ReverseHardLimitSwitch);
		checkParameter("reverseHardLimitSwitchResetsEncoder", reverseHardLimitSwitchResetsEncoder, 0, Reverse|ReverseHardLimitSwitch);
		checkParameter("forwardSoftLimit", forwardSoftLimit, 0, Forward|ForwardSoftLimitSwitch);
		checkParameter("reverseSoftLimit", reverseSoftLimit, 0, Reverse|ReverseSoftLimitSwitch);
		checkParameter("defaultRate", defaultRate, 0, ControlRate);
		checkParameter("neutralMode", neutralMode, ControlRate|ControlPosition, NeutralMode);
		checkParameter("percentageRateUOM", percentageRateUOM, ControlRate|LimitRate, 0);
		checkParameter("defaultCommandFactory", defaultCommandFactory, 0, 0); // no requirements
		if(has(ForwardSoftLimitSwitch) && forwardSoftLimit.getValue() > forwardLimit.getValue() && !has(ForwardHardLimitSwitch)) {
			throw new IllegalArgumentException("forwardSoftLimit " + forwardSoftLimit + " exceeds forwardLimit " + forwardLimit + ".  Soft limits must be within physical range of motion.");
		}
		if(has(ReverseSoftLimitSwitch) && reverseSoftLimit.getValue() < reverseLimit.getValue()&& !has(ReverseHardLimitSwitch)) {
			throw new IllegalArgumentException("reverseSoftLimit " + reverseSoftLimit + " exceeds reverseLimit " + reverseLimit + ".  Soft limits must be within physical range of motion.");
		}
	}

	public String toString() {
		return name;
	}

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
		.append("sensorToDriveScalingFactor............" + sensorToDriveScalingFactor + "\n")
		.append("\n")
		.append("Miscellaneous:\n")
		.append("\n")
		.append("neutralMode..........................." + neutralMode + "\n")
		.append("defaultCommandFactory................." + defaultCommandFactory + "\n")
		.append("----------------------------------------------------------------------------\n")
		;
		return buf.toString();
	}
}
