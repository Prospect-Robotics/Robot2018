package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Rate;

/*
 * This class of units measures rates, ultimately in terms of the canonical distance and time units.
 */
public class RateUOM extends UOM<RateUOM,Rate> {
	
	public static   final  RateUOM CanonicalRateUOMForMovement = new RateUOM(SystemOfMeasurement.Rate, LengthUOM.Millimeters, TimeUOM.Milliseconds);
	// Metric System
	public static   final  RateUOM MillimetersPerMillisecond = CanonicalRateUOMForMovement;
	public static   final  RateUOM MillimetersPerDecisecond  = new RateUOM(LengthUOM.Millimeters,    TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM MillimetersPerSecond      = new RateUOM(LengthUOM.Millimeters,    TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MillimetersPerMinute      = new RateUOM(LengthUOM.Millimeters,    TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MillimetersPerHour        = new RateUOM(LengthUOM.Millimeters,    TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM CentimetersPerMillisecond = new RateUOM(LengthUOM.Centimeters,    TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM CentimetersPerDecisecond  = new RateUOM(LengthUOM.Centimeters,    TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM CentimetersPerSecond      = new RateUOM(LengthUOM.Centimeters,    TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM CentimetersPerMinute      = new RateUOM(LengthUOM.Centimeters,    TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM CentimetersPerHour        = new RateUOM(LengthUOM.Centimeters,    TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM DecimetersPerMillisecond  = new RateUOM(LengthUOM.Decimeters,     TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM DecimetersPerDecisecond   = new RateUOM(LengthUOM.Decimeters,     TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM DecimetersPerSecond       = new RateUOM(LengthUOM.Decimeters,     TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM DecimetersPerMinute       = new RateUOM(LengthUOM.Decimeters,     TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM DecimetersPerHour         = new RateUOM(LengthUOM.Decimeters,     TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM MetersPerMillisecond      = new RateUOM(LengthUOM.Meters,         TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM MetersPerDecisecond       = new RateUOM(LengthUOM.Meters,         TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM MetersPerSecond           = new RateUOM(LengthUOM.Meters,         TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MetersPerMinute           = new RateUOM(LengthUOM.Meters,         TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MetersPerHour             = new RateUOM(LengthUOM.Meters,         TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM KilometersPerMillisecond  = new RateUOM(LengthUOM.Kilometers,     TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM KilometersPerDecisecond   = new RateUOM(LengthUOM.Kilometers,     TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM KilometersPerSecond       = new RateUOM(LengthUOM.Kilometers,     TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM KilometersPerMinute       = new RateUOM(LengthUOM.Kilometers,     TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM KilometersPerHour         = new RateUOM(LengthUOM.Kilometers,     TimeUOM.Hours,          CanonicalRateUOMForMovement);

	// Imperial System
	public static   final  RateUOM ThousandthsPerMillisecond = new RateUOM(LengthUOM.Thousandths,    TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM ThousandthsPerDecisecond  = new RateUOM(LengthUOM.Thousandths,    TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM ThousandthsPerSecond      = new RateUOM(LengthUOM.Thousandths,    TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM ThousandthsPerMinute      = new RateUOM(LengthUOM.Thousandths,    TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM ThousandthsPerHour        = new RateUOM(LengthUOM.Thousandths,    TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM HundredthsPerMillisecond  = new RateUOM(LengthUOM.Hundredths,     TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM HundredthsPerDecisecond   = new RateUOM(LengthUOM.Hundredths,     TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM HundredthsPerSecond       = new RateUOM(LengthUOM.Hundredths,     TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM HundredthsPerMinute       = new RateUOM(LengthUOM.Hundredths,     TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM HundredthsPerHour         = new RateUOM(LengthUOM.Hundredths,     TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM TenthsPerMillisecond      = new RateUOM(LengthUOM.Tenths,         TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM TenthsPerDecisecond       = new RateUOM(LengthUOM.Tenths,         TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM TenthsPerSecond           = new RateUOM(LengthUOM.Tenths,         TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM TenthsPerMinute           = new RateUOM(LengthUOM.Tenths,         TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM TenthsPerHour             = new RateUOM(LengthUOM.Tenths,         TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM InchesPerMillisecond      = new RateUOM(LengthUOM.Inches,         TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM InchesPerDecisecond       = new RateUOM(LengthUOM.Inches,         TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM InchesPerSecond           = new RateUOM(LengthUOM.Inches,         TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM InchesPerMinute           = new RateUOM(LengthUOM.Inches,         TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM InchesPerHour             = new RateUOM(LengthUOM.Inches,         TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM FeetPerMillisecond        = new RateUOM(LengthUOM.Feet,           TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM FeetPerDecisecond         = new RateUOM(LengthUOM.Feet,           TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM FeetPerSecond             = new RateUOM(LengthUOM.Feet,           TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM FeetPerMinute             = new RateUOM(LengthUOM.Feet,           TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM FeetPerHour               = new RateUOM(LengthUOM.Feet,           TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM YardsPerMillisecond       = new RateUOM(LengthUOM.Yards,          TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM YardsPerDecisecond        = new RateUOM(LengthUOM.Yards,          TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM YardsPerSecond            = new RateUOM(LengthUOM.Yards,          TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM YardsPerMinute            = new RateUOM(LengthUOM.Yards,          TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM YardsPerHour              = new RateUOM(LengthUOM.Yards,          TimeUOM.Hours,          CanonicalRateUOMForMovement);

	public static   final  RateUOM MilesPerMillisecond       = new RateUOM(LengthUOM.Miles,          TimeUOM.Milliseconds,   CanonicalRateUOMForMovement);
	public static   final  RateUOM MilesPerDecisecond        = new RateUOM(LengthUOM.Miles,          TimeUOM.Deciseconds,    CanonicalRateUOMForMovement);
	public static   final  RateUOM MilesPerSecond            = new RateUOM(LengthUOM.Miles,          TimeUOM.Seconds,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MilesPerMinute            = new RateUOM(LengthUOM.Miles,          TimeUOM.Minutes,        CanonicalRateUOMForMovement);
	public static   final  RateUOM MilesPerHour              = new RateUOM(LengthUOM.Miles,          TimeUOM.Hours,          CanonicalRateUOMForMovement);
	
	// Add derived units of measure here...
	
	// Units of distance
	private final LengthUOM lengthUOM; 
	// Units of time
	private final TimeUOM timeUOM; 

	// Create a new canonical unit of rate
	private RateUOM(SystemOfMeasurement systemOfMeasurement, LengthUOM lengthUOM, TimeUOM timeUOM) {
		super(systemOfMeasurement, 
			lengthUOM.getUnitNameSingular() + "/" + timeUOM.getUnitNameSingular(),
			lengthUOM.getUnitNamePlural() + "/" + timeUOM.getUnitNameSingular(),
			lengthUOM.getUnitNameAbbreviation() + "/" + timeUOM.getUnitNameAbbreviation());
		this.lengthUOM = lengthUOM;
		this.timeUOM = timeUOM;
	}
	/*
	 * Create a new canonical unit of rate.
	 * We must determine the value in canonical unit's rate/time and since rate's canonical measures for length and time
	 * may not match the canonical units used for length and time, we musts convert first from canonical values to the 
	 * units used for rate's canonical representation. 
	 */
	public RateUOM(LengthUOM lengthUOM, TimeUOM timeUOM, RateUOM canonicalRateUOM, String customAbbreviation) {
		super(lengthUOM.getUnitNameSingular() + "/" + timeUOM.getUnitNameSingular(),
				  lengthUOM.getUnitNamePlural() + "/" + timeUOM.getUnitNameSingular(),
				  customAbbreviation,
				  canonicalRateUOM,
				  lengthUOM.getCanonicalValue().convertTo(canonicalRateUOM.getLengthUOM()).getValue()
				  / timeUOM.getCanonicalValue().convertTo(canonicalRateUOM.getTimeUOM()).getValue()
				  );
		this.timeUOM     = timeUOM;
		this.lengthUOM = lengthUOM;
	}
	/*
	 * Create a new canonical unit of rate.
	 * We must determine the value in canonical unit's rate/time and since rate's canonical measures for length and time
	 * may not match the canonical units used for length and time, we musts convert first from canonical values to the 
	 * units used for rate's canonical representation. 
	 */
	public RateUOM(LengthUOM lengthUOM, TimeUOM timeUOM, RateUOM canonicalRateUOM) {
		this(lengthUOM, timeUOM, canonicalRateUOM, lengthUOM.getUnitNameAbbreviation() + "/" + timeUOM.getUnitNameAbbreviation());
	}
	// Get the canonical unit of measure
	public LengthUOM getLengthUOM() {
		return lengthUOM;
	}
	/* Get the time unit of measure */
	public TimeUOM getTimeUOM() {
		return timeUOM;
	}
	// Create a new value of this type
	public Rate create(double value) {
		return new Rate(this, value);
	}
}