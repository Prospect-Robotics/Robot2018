package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.util.unit.uom.LengthUOM;
import org.usfirst.frc2813.util.unit.uom.RateUOM;
import org.usfirst.frc2813.util.unit.uom.TimeUOM;
import org.usfirst.frc2813.util.unit.values.Length;
import org.usfirst.frc2813.util.unit.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/*
 * Extends DirectionSpeedAxisConfiguration and adds information about limits.
 * Whether they define hard limits, soft limits, or user input valiation depends on context.
 */
public class PositionDirectionSpeedAxisConfiguration extends DirectionSpeedAxisConfiguration {
	private final Length forwardLimit;
	private final Length reverseLimit;
	private final boolean enableReverseHardLimit;
	private final boolean enableForwardHardLimit;
	private final boolean enableReverseSoftLimit;
	private final boolean enableForwardSoftLimit;
	private final boolean setPositionOnReverseHardLimit;
	private final boolean setPositionOnForwardHardLimit;
	private final LimitSwitchNormal forwardLimitSwitchNormal;
	private final LimitSwitchNormal reverseLimitSwitchNormal;
	/*
	 * Get the native units for this axis
	 */
	protected PositionDirectionSpeedAxisConfiguration(
			String name, 
			LengthUOM nativeLengthUOM, 
			TimeUOM nativeTimeUOM,
			RateUOM nativeRateUOM, 
			boolean axisPhaseIsReversed, 
			double axisScale, 
			Rate minimumForwardRate,
			Rate maximumForwardRate, 
			Rate minimumReverseRate, 
			Rate maximumReverseRate, 
			Length forwardLimit,
			Length reverseLimit, 
			boolean enableForwardHardLimit,
			boolean enableReverseHardLimit, 
			boolean enableForwardSoftLimit, 
			boolean enableReverseSoftLimit, 
			boolean setPositionOnReverseHardLimit,
			boolean setPositionOnForwardHardLimit, 
			LimitSwitchNormal forwardLimitSwitchNormal, 
			LimitSwitchNormal reverseLimitSwitchNormal) 
	{
		super(name, nativeLengthUOM, nativeTimeUOM, nativeRateUOM, axisPhaseIsReversed, axisScale, minimumForwardRate, maximumForwardRate, minimumReverseRate, maximumReverseRate);
		this.forwardLimit = forwardLimit;
		this.reverseLimit = reverseLimit;
		this.enableForwardHardLimit = enableForwardHardLimit;
		this.enableReverseHardLimit = enableReverseHardLimit;
		this.enableForwardSoftLimit = enableForwardSoftLimit;
		this.enableReverseSoftLimit = enableReverseSoftLimit;
		this.setPositionOnForwardHardLimit = setPositionOnForwardHardLimit;
		this.setPositionOnReverseHardLimit = setPositionOnReverseHardLimit;
		this.forwardLimitSwitchNormal = forwardLimitSwitchNormal;
		this.reverseLimitSwitchNormal = reverseLimitSwitchNormal; 
	}
	public Length getForwardLimit() {
		return forwardLimit;
	}
	public Length getReverseLimit() {
		return reverseLimit;
	}
	public boolean isEnableReverseHardLimit() {
		return enableReverseHardLimit;
	}
	public boolean isEnableForwardHardLimit() {
		return enableForwardHardLimit;
	}
	public boolean isEnableReverseSoftLimit() {
		return enableReverseSoftLimit;
	}
	public boolean isEnableForwardSoftLimit() {
		return enableForwardSoftLimit;
	}
	public boolean isSetPositionOnReverseHardLimit() {
		return setPositionOnReverseHardLimit;
	}
	public boolean isSetPositionOnForwardHardLimit() {
		return setPositionOnForwardHardLimit;
	}
	public LimitSwitchNormal getForwardLimitSwitchNormal() {
		return forwardLimitSwitchNormal;
	}
	public LimitSwitchNormal getReverseLimitSwitchNormal() {
		return reverseLimitSwitchNormal;
	}
	protected String getDescriptionHeader() {
		return getLine() 
				+ "/n[Axis Configuration Report - Position, Direction & Speed]/n" 
				+ getLine();
	}
	protected String getDescriptionFields() {
		return String.format("%s" +
				"Forward Limit: %s (%s)\n" +
				"Reverse Limit: %s (%s)\n" +
				"Forward Hard Limit Enabled: %s\n" +
				"Reverse Hard Limit Enabled: %s\n" +
				"Forward Soft Limit Enabled: %s\n" +
				"Reverse Soft Limit Enabled: %s\n" +
				"Auto Set Sensor On Forward Hard Limit Reached: %s\n" +
				"Auto Set Sensor On Reverse Hard Limit Reached: %s\n",
				"Forward Hard Limit Normal: %s\n" +
				"Reverse Hard Limit Normal: %s\n",
				super.getDescriptionFields(),
				forwardLimit, 
				reverseLimit,
				enableForwardHardLimit, 
				enableReverseHardLimit,
				enableForwardSoftLimit, 
				enableReverseSoftLimit,
				setPositionOnForwardHardLimit,
				setPositionOnReverseHardLimit,
				forwardLimitSwitchNormal, 
				reverseLimitSwitchNormal); 
	}
	public String getDescription() {
		return String.format(
				getDescriptionHeader() + 
				getDescriptionFields() + 
				getLine());
	}
}
