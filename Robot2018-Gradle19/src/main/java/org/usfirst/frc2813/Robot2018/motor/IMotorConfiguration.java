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
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

public interface IMotorConfiguration {

	//     Capabilities
	int ControlDirection = 1 << 0;
	int ControlPosition = 1 << 1;
	int ControlRate = 1 << 2;
	int Forward = 1 << 3;
	int LocalForwardHardLimitSwitch = 1 << 4;
	int ForwardSoftLimitSwitch = 1 << 5;
	int LimitPosition = 1 << 6;
	int LimitRate = 1 << 7;
	int SensorToDriveScale = 1 << 8;
	int ReadDirection = 1 << 9;
	int ReadPosition = 1 << 10;
	int ReadRate = 1 << 11;
	int Reverse = 1 << 12;
	int LocalReverseHardLimitSwitch = 1 << 13;
	int ReverseSoftLimitSwitch = 1 << 14;
	int DefaultRate = 1 << 15;
	int NeutralMode = 1 << 16;
	int RemoteForwardHardLimitSwitch = 1 << 17;
	int RemoteReverseHardLimitSwitch = 1 << 18;
	int Disconnected = 1 << 19;
	int StaticCurrentLimit = 1 << 20; 
	int MAX_CAPABILITY = 31;

	/*
	 * Get the name of the axis
	 */
	String getName();

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

	double getSensorToDriveScalingMultiplier();

	RateUOM getNativeDisplayRateUOM();

	RateUOM getNativeMotorRateUOM();

	RateUOM getNativeSensorRateUOM();

	Rate getMinimumForwardRate();

	Rate getMaximumForwardRate();

	Rate getMinimumReverseRate();

	Rate getMaximumReverseRate();

	RateUOM getPercentageRateUOM();

	Rate getDefaultRate();

	Boolean getSensorPhaseIsReversed();

	Boolean getMotorPhaseIsReversed();

	LengthUOM getNativeDisplayLengthUOM();

	Length getForwardLimit();

	Length getReverseLimit();

	Boolean getForwardHardLimitSwitchResetsEncoder();

	Boolean getReverseHardLimitSwitchResetsEncoder();

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

	RemoteLimitSwitchSource getRemoteReverseHardLimitSwitchSource();

	RemoteLimitSwitchSource getRemoteForwardHardLimitSwitchSource();

	Integer getRemoteReverseHardLimitSwitchDeviceId();

	Integer getRemoteForwardHardLimitSwitchDeviceId();

	Double getPeakOutputForward();
	Double getPeakOutputReverse();

	Integer getPeakCurrentLimit();
	Integer getContinuousCurrentLimit();
	Integer getPeakCurrentDurationMs();
}