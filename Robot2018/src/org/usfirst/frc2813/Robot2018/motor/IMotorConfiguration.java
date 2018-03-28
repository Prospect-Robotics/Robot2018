package org.usfirst.frc2813.Robot2018.motor;

import java.util.List;

import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.units.Direction;
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
	int Disconnected = 1 << 31;
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

	RateUOM getPercentageRateUOM();

	Rate getDefaultRate();

	boolean getSensorPhaseIsReversed();

	boolean getMotorPhaseIsReversed();

	LengthUOM getNativeDisplayLengthUOM();

	Length getForwardLimit();

	Length getReverseLimit();

	boolean getForwardHardLimitSwitchResetsEncoder();

	boolean getReverseHardLimitSwitchResetsEncoder();
	
	/**
	 * Do we HAVE a hard limit in this direction AND we reset the encoder when we hit a hard limit in this direction?
	 * @param direction the direction to check
	 * @return true/false
	 */
	boolean getHasHardLimitSwitchResetsEncoder(Direction direction);

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

	int getRemoteReverseHardLimitSwitchDeviceId();

	int getRemoteForwardHardLimitSwitchDeviceId();
	/**
	 * For testing auto, we'll ignore this device if it's disconnected
	 * @return true if the motor is being skipped because of a failure or it's not ready yet. 
	 */
	boolean isDisconnected();
	/**
	 * Get the physical limit of the hardware
	 * @param direction the direction to check
	 * @return the physical limit, as specified in the configuration.
	 */
	public Length getPhysicalLimit(Direction direction);
	/**
	 * Get the soft limit switch position for the given direction, or null if we don't have one.
	 * @param direction the direction to check
	 * @see getHasSoftLimit
	 * @return The soft limit if one exists, or null if we don't have one 
	 */
	public Length getSoftLimit(Direction direction);
	/**
	 * Do we have a hard limit in the indicated direction
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasHardLimit(Direction direction);
	/**
	 * Do we have a soft limit in the indicated direction 
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasSoftLimit(Direction direction);
	/**
	 * Do we have a hard or soft limit providing safety in the indicated direction 
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasHardOrSoftLimit(Direction direction);
	/**
	 * Get the minimum forward rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumForwardRate();
	/**
	 * Get the maximum forward rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumForwardRate();
	/**
	 * Get the maximum reverse rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumReverseRate();
	/**
	 * Get the minimum reverse rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumReverseRate();
	/**
	 * Get the maximum rate
	 * @param direction the direction to check
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumRate(Direction direction);
	/**
	 * Get the minimum rate
	 * @param direction the direction to check
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumRate(Direction direction);
	
	/**
	 * Get the hard limit value
	 * @param direction the direction to check
	 * @return The hard limit in a direction, if a hard limit switch exists. 
	 * @see IMotor#getHasHardLimit
	 */
	public Length getHardLimit(Direction direction);
	
	/** 
	 * Unit conversion.  NOTE: Truncates to int.
	 * @param v the value to convert
	 * @return The converted value.
	 */
	public Length toSensorUnits(Length v);
	/** 
	 * Unit conversion	
	 * @param v the value to convert
	 * @return The converted value.
	 */
	public Length toDisplayUnits(Length v);
	/** 
	 * Unit conversion.  NOTE: Truncates to int.
	 * @param v the value to convert
	 * @return The converted value.
	 */
	public Rate toSensorUnits(Rate v);
	/** 
	 * Unit conversion	
	 * @param v the value to convert
	 * @return The converted value.
	 */
	public Rate toDisplayUnits(Rate r);
	/** 
	 * Unit wrapping	
	 * @param v the value that is already in the correct units
	 * @return The encapsulated value
	 */
	public Length createSensorLength(double v);
	/** 
	 * Unit wrapping	
	 * @param v the value that is already in the correct units
	 * @return The encapsulated value
	 */
	public Length createDisplayLength(double v);
	/** 
	 * Unit wrapping	
	 * @param v the value that is already in the correct units
	 * @return The encapsulated value
	 */
	public Rate createSensorRate(double v);
	/** 
	 * Unit wrapping	
	 * @param v the value that is already in the correct units
	 * @return The encapsulated value
	 */
	public Rate createDisplayRate(double v);
	/**
	 * Shortcut - do we have a hard limit in the indicated direction that we have exceeded?
	 * NOTE: This does NOT tell you the limit switch, it tells you whether we think we exceeded the range!
	 * @see IMotor#getCurrentHardLimitSwitchStatus(Direction)
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is beyond the hard limit position in the configuration
	 */
	public boolean isHardLimitExceeded(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we have a hard limit in the indicated direction that we have reached?
	 * NOTE: This does NOT tell you the limit switch, it tells you whether we think we exceeded the range!
	 * @param motor the motor to be checked
	 * @param direction the direction to check
	 * @return true if the current position is at or beyond the hard limit position in the configuration
	 */
	public boolean isHardLimitReached(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we think we should have passed a hard limit, but the switch isn't on?
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is at or beyond the hard limit position in the configuration, but the switch isn't active.  false if there's no limit or it's good.
	 */
	public boolean isHardLimitNeedingCalibration(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have exceeded?
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is beyond the soft limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isSoftLimitExceeded(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have reached?
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is at or beyond the soft limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isSoftLimitReached(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have exceeded?
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is at or beyond the physical limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isPhysicalLimitExceeded(IMotor motor, Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have reached?
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return true if the current position is at the physical limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isPhysicalLimitReached(IMotor motor, Direction direction);
	/**
	 * Read the current state of the soft limit switch.
	 * @param switchDirection which limit switch are you asking about?
	 * @param motor the motor to be checked
	 * @return true if a soft limit switch exists AND is active, as defined by the configuration.
	 */
	public boolean getCurrentSoftLimitSwitchStatus(IMotor motor, Direction switchDirection);
	/**
	 * Check if either direction has a sensor calibration issue
	 * @param motor the motor to be checked
	 */
	public void checkForLimitErrors(IMotor motor);
	/**
	 * Display a value in both sensor and display units for debugging
	 * @return a display string showing the sensor and "subsystem" units. 
	 */
	public String getDescriptionWithBothUnits(Length v);
	/**
	 * Display a value in both sensor and display units for debugging
	 * @return a display string showing the sensor and "subsystem" units. 
	 */
	public String getDescriptionWithBothUnits(Rate v);

	/** 
	 * If a reset is necessary, return the new value. 
	 * @param motor the motor to be checked
	 * @return A sensor value if reset is necessary, null otherwise.
	 */
	public Length getSensorPositionIfResetIsNecessary(IMotor motor);
	/** 
	 * If a reset is necessary, return the new value. 
	 * @param direction the direction to check
	 * @param motor the motor to be checked
	 * @return A sensor value if reset is necessary, null otherwise.
	 */
	public Length getSensorPositionIfResetIsNecessary(IMotor motor, Direction direction);

}
