package org.usfirst.frc2813.Robot2018.motor;

import java.util.List;

import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public interface IMotorConfiguration {

	//     Capabilities
	int ControlDirection = 1 << 0;
	int ControlPosition = 1 << 1;
	int ControlRate = 1 << 2;
	int Forward = 1 << 3;
	int ForwardHardLimitSwitch = 1 << 4;
	int ForwardSoftLimitSwitch = 1 << 5;
	int LimitPosition = 1 << 6;
	int LimitRate = 1 << 7;
	int SensorToDriveScale = 1 << 8;
	int ReadDirection = 1 << 9;
	int ReadPosition = 1 << 10;
	int ReadRate = 1 << 11;
	int Reverse = 1 << 12;
	int ReverseHardLimitSwitch = 1 << 13;
	int ReverseSoftLimitSwitch = 1 << 14;
	int DefaultRate = 1 << 15;
	int NeutralMode = 1 << 16;
	int Disconnected = 1 << 31;
	int MAX_CAPABILITY = 31;

	/*
	 * Get the name of the axis
	 */
	String getName();

	/*
	 * Find out if a capability is supported
	 */
	boolean has(long capability);

	/*
	 * Find out if a capability is supported
	 */
	long getCapabilities();

	/*
	 * Find out if all capabilities are supported
	 */
	boolean hasAll(long capabilities);

	/*
	 * Find out if any capabilities are supported
	 */
	boolean hasAny(long capabilities);

	/*
	 * Assert that a capability is supported
	 */
	void requireAll(long capabilities);

	/*
	 * Assert that a capability is supported
	 */
	void requireAny(long capabilities);

	LengthUOM getNativeMotorLengthUOM();

	LengthUOM getNativeSensorLengthUOM();

	double getSensorToDriveScalingFactor();

	RateUOM getNativeDisplayRateUOM();

	RateUOM getNativeMotorRateUOM();

	RateUOM getNativeSensorRateUOM();

	Rate getMinimumForwardRate();

	Rate getMaximumForwardRate();

	Rate getMinimumReverseRate();

	Rate getMaximumReverseRate();

	RateUOM getPercentageRateUOM();

	Rate getDefaultRate();

	boolean getSensorPhaseIsReversed();

	boolean getMotorPhaseIsReversed();

	LengthUOM getNativeDisplayLengthUOM();

	Length getForwardLimit();

	Length getReverseLimit();

	boolean getForwardHardLimitSwitchResetsEncoder();

	boolean getReverseHardLimitSwitchResetsEncoder();

	LimitSwitchNormal getForwardHardLimitSwitchNormal();

	LimitSwitchNormal getReverseHardLimitSwitchNormal();

	Length getForwardSoftLimit();

	Length getReverseSoftLimit();

	NeutralMode getNeutralMode();

	ICommandFactory<Motor> getDefaultCommandFactory();

	List<PIDConfiguration> getPIDConfigurations();

	void validateConfiguration();

	String toString();

	void dumpDescription();

	String getDescription();

}