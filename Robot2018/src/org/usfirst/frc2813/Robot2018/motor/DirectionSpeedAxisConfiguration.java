package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.util.unit.uom.LengthUOM;
import org.usfirst.frc2813.util.unit.uom.RateUOM;
import org.usfirst.frc2813.util.unit.uom.TimeUOM;
import org.usfirst.frc2813.util.unit.values.Rate;

/*
 * This class is an immutable description of an axis as used by a motor, a subsystem's API, or a sensor.
 * The idea is to encapsulate everything about the axis so that the subsystem can completely initialize 
 * itself and handle all conversions, scaling, formatting, limits, validation, etc... automatically with
 * the minimal amount of data specified.
 */
public class DirectionSpeedAxisConfiguration {
	private final String name;
	private final LengthUOM nativeLengthUOM;
	private final TimeUOM nativeTimeUOM;
	private final RateUOM nativeRateUOM;
	private final boolean axisPhaseIsReversed;
	private final double axisScale;
	private final Rate minimumForwardRate;
	private final Rate maximumForwardRate;
	private final Rate minimumReverseRate;
	private final Rate maximumReverseRate;

	/*
	 * Get the native units for this axis
	 */
	protected DirectionSpeedAxisConfiguration(String name,
			LengthUOM nativeLengthUOM, TimeUOM nativeTimeUOM,
			RateUOM nativeRateUnitOfMeasuement, boolean axisPhaseIsReversed, double axisScale, Rate minimumForwardRate,
			Rate maximumForwardRate, Rate minimumReverseRate, Rate maximumReverseRate) {
		this.name = name; 
		this.nativeLengthUOM = nativeLengthUOM;
		this.nativeTimeUOM = nativeTimeUOM;
		this.nativeRateUOM = nativeRateUnitOfMeasuement;
		this.axisPhaseIsReversed = axisPhaseIsReversed;
		this.axisScale = axisScale;
		this.minimumForwardRate = minimumForwardRate;
		this.maximumForwardRate = maximumForwardRate;
		this.minimumReverseRate = minimumReverseRate;
		this.maximumReverseRate = maximumReverseRate;
	}

	public LengthUOM getNativeLengthUOM() {
		return nativeLengthUOM;
	}

	public TimeUOM getNativeTimeUOM() {
		return nativeTimeUOM;
	}

	public RateUOM getNativeRateUOM() {
		return nativeRateUOM;
	}

	public boolean isAxisPhaseIsReversed() {
		return axisPhaseIsReversed;
	}

	public double getAxisScale() {
		return axisScale;
	}

	public Rate getMinimumForwardRate() {
		return minimumForwardRate;
	}

	public Rate getMaximumForwardRate() {
		return maximumForwardRate;
	}

	public Rate getMinimumReverseRate() {
		return minimumReverseRate;
	}

	public Rate getMaximumReverseRate() {
		return maximumReverseRate;
	}
	
	public String toString() {
		return name;
	}
	protected String getLine() {
		return "----------------------------------------------------------------------------\n";
	}
	
	protected String getDescriptionHeader() {
		return getLine() 
				+ "/n[Axis Configuration Report - Direction & Speed]/n" 
				+ getLine();
	}
	protected String getDescriptionFields() {
		return String.format(
			"Name: %s\n" +
			"Length Unit: %s (%s)\n" +
			"Time Unit: %s (%s)\n" +
			"Rate Unit: %s (%s)\n" +
			"Axis Phase Is Reversed: %s\n" +
			"Axis Scaling Multiplier (gearing up/down): %g\n" +
			"Minimum Reverse Rate: %s (%s)\n" +
			"Maximum Reverse Rate: %s (%s)\n" +
			"Minimum Forward Rate: %s (%s)\n" +
			"Maximum Forward Rate: %s (%s)\n",
			getDescriptionHeader(),
			name,
			nativeLengthUOM, nativeLengthUOM.getCanonicalValue(),
			nativeTimeUOM, nativeTimeUOM.getCanonicalValue(),
			nativeRateUOM, nativeRateUOM.getCanonicalValue(),
			axisPhaseIsReversed,
			axisScale,
			minimumReverseRate, minimumReverseRate.getCanonicalValue(),
			maximumReverseRate, maximumReverseRate.getCanonicalValue(),
			minimumForwardRate, minimumForwardRate.getCanonicalValue(),
			maximumForwardRate, maximumForwardRate.getCanonicalValue());
	}
	public String getDescription() {
		return String.format(
				getDescriptionHeader() + 
				getDescriptionFields() + 
				getLine());
	}

	public void dumpDescription() {
		System.out.println(getDescription());
	}
}
