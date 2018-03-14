package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Time;
import org.usfirst.frc2813.units.values.Value;

/*
 * This class of units measures rates, ultimately in terms of the canonical distance and time units.
 */
public class RateUOM extends UOM<RateUOM,Rate> {
	// Metric System
	public static   final  RateUOM MillimetersPerMillisecond = new RateUOM(SystemOfMeasurement.Rate, LengthUOM.Millimeters, TimeUOM.Milliseconds);
	public static   final  RateUOM MillimetersPerDecisecond  = new RateUOM(LengthUOM.Millimeters, TimeUOM.Deciseconds);
	public static   final  RateUOM MillimetersPerSecond      = new RateUOM(LengthUOM.Millimeters, TimeUOM.Seconds);
	public static   final  RateUOM MillimetersPerMinute      = new RateUOM(LengthUOM.Millimeters, TimeUOM.Minutes);
	public static   final  RateUOM MillimetersPerHour        = new RateUOM(LengthUOM.Millimeters, TimeUOM.Hours);

	public static   final  RateUOM CentimetersPerMillisecond = new RateUOM(LengthUOM.Centimeters, TimeUOM.Milliseconds);
	public static   final  RateUOM CentimetersPerDecisecond  = new RateUOM(LengthUOM.Centimeters, TimeUOM.Deciseconds);
	public static   final  RateUOM CentimetersPerSecond      = new RateUOM(LengthUOM.Centimeters, TimeUOM.Seconds);
	public static   final  RateUOM CentimetersPerMinute      = new RateUOM(LengthUOM.Centimeters, TimeUOM.Minutes);
	public static   final  RateUOM CentimetersPerHour        = new RateUOM(LengthUOM.Centimeters, TimeUOM.Hours);

	public static   final  RateUOM DecimetersPerMillisecond  = new RateUOM(LengthUOM.Decimeters,  TimeUOM.Milliseconds);
	public static   final  RateUOM DecimetersPerDecisecond   = new RateUOM(LengthUOM.Decimeters,  TimeUOM.Deciseconds);
	public static   final  RateUOM DecimetersPerSecond       = new RateUOM(LengthUOM.Decimeters,  TimeUOM.Seconds);
	public static   final  RateUOM DecimetersPerMinute       = new RateUOM(LengthUOM.Decimeters,  TimeUOM.Minutes);
	public static   final  RateUOM DecimetersPerHour         = new RateUOM(LengthUOM.Decimeters,  TimeUOM.Hours);

	public static   final  RateUOM MetersPerMillisecond      = new RateUOM(LengthUOM.Meters,      TimeUOM.Milliseconds);
	public static   final  RateUOM MetersPerDecisecond       = new RateUOM(LengthUOM.Meters,      TimeUOM.Deciseconds);
	public static   final  RateUOM MetersPerSecond           = new RateUOM(LengthUOM.Meters,      TimeUOM.Seconds);
	public static   final  RateUOM MetersPerMinute           = new RateUOM(LengthUOM.Meters,      TimeUOM.Minutes);
	public static   final  RateUOM MetersPerHour             = new RateUOM(LengthUOM.Meters,      TimeUOM.Hours);

	public static   final  RateUOM KilometersPerMillisecond  = new RateUOM(LengthUOM.Kilometers,  TimeUOM.Milliseconds);
	public static   final  RateUOM KilometersPerDecisecond   = new RateUOM(LengthUOM.Kilometers,  TimeUOM.Deciseconds);
	public static   final  RateUOM KilometersPerSecond       = new RateUOM(LengthUOM.Kilometers,  TimeUOM.Seconds);
	public static   final  RateUOM KilometersPerMinute       = new RateUOM(LengthUOM.Kilometers,  TimeUOM.Minutes);
	public static   final  RateUOM KilometersPerHour         = new RateUOM(LengthUOM.Kilometers,  TimeUOM.Hours);

	//     Imperial System
	public static   final  RateUOM ThousandthsPerMillisecond = new RateUOM(LengthUOM.Thousandths, TimeUOM.Milliseconds);
	public static   final  RateUOM ThousandthsPerDecisecond  = new RateUOM(LengthUOM.Thousandths, TimeUOM.Deciseconds);
	public static   final  RateUOM ThousandthsPerSecond      = new RateUOM(LengthUOM.Thousandths, TimeUOM.Seconds);
	public static   final  RateUOM ThousandthsPerMinute      = new RateUOM(LengthUOM.Thousandths, TimeUOM.Minutes);
	public static   final  RateUOM ThousandthsPerHour        = new RateUOM(LengthUOM.Thousandths, TimeUOM.Hours);

	public static   final  RateUOM HundredthsPerMillisecond  = new RateUOM(LengthUOM.Hundredths,  TimeUOM.Milliseconds);
	public static   final  RateUOM HundredthsPerDecisecond   = new RateUOM(LengthUOM.Hundredths,  TimeUOM.Deciseconds);
	public static   final  RateUOM HundredthsPerSecond       = new RateUOM(LengthUOM.Hundredths,  TimeUOM.Seconds);
	public static   final  RateUOM HundredthsPerMinute       = new RateUOM(LengthUOM.Hundredths,  TimeUOM.Minutes);
	public static   final  RateUOM HundredthsPerHour         = new RateUOM(LengthUOM.Hundredths,  TimeUOM.Hours);

	public static   final  RateUOM TenthsPerMillisecond      = new RateUOM(LengthUOM.Tenths,      TimeUOM.Milliseconds);
	public static   final  RateUOM TenthsPerDecisecond       = new RateUOM(LengthUOM.Tenths,      TimeUOM.Deciseconds);
	public static   final  RateUOM TenthsPerSecond           = new RateUOM(LengthUOM.Tenths,      TimeUOM.Seconds);
	public static   final  RateUOM TenthsPerMinute           = new RateUOM(LengthUOM.Tenths,      TimeUOM.Minutes);
	public static   final  RateUOM TenthsPerHour             = new RateUOM(LengthUOM.Tenths,      TimeUOM.Hours);

	public static   final  RateUOM InchesPerMillisecond      = new RateUOM(LengthUOM.Inches,      TimeUOM.Milliseconds);
	public static   final  RateUOM InchesPerDecisecond       = new RateUOM(LengthUOM.Inches,      TimeUOM.Deciseconds);
	public static   final  RateUOM InchesPerSecond           = new RateUOM(LengthUOM.Inches,      TimeUOM.Seconds);
	public static   final  RateUOM InchesPerMinute           = new RateUOM(LengthUOM.Inches,      TimeUOM.Minutes);
	public static   final  RateUOM InchesPerHour             = new RateUOM(LengthUOM.Inches,      TimeUOM.Hours);

	public static   final  RateUOM FeetPerMillisecond        = new RateUOM(LengthUOM.Feet,        TimeUOM.Milliseconds);
	public static   final  RateUOM FeetPerDecisecond         = new RateUOM(LengthUOM.Feet,        TimeUOM.Deciseconds);
	public static   final  RateUOM FeetPerSecond             = new RateUOM(LengthUOM.Feet,        TimeUOM.Seconds);
	public static   final  RateUOM FeetPerMinute             = new RateUOM(LengthUOM.Feet,        TimeUOM.Minutes);
	public static   final  RateUOM FeetPerHour               = new RateUOM(LengthUOM.Feet,        TimeUOM.Hours);

	public static   final  RateUOM YardsPerMillisecond       = new RateUOM(LengthUOM.Yards,       TimeUOM.Milliseconds);
	public static   final  RateUOM YardsPerDecisecond        = new RateUOM(LengthUOM.Yards,       TimeUOM.Deciseconds);
	public static   final  RateUOM YardsPerSecond            = new RateUOM(LengthUOM.Yards,       TimeUOM.Seconds);
	public static   final  RateUOM YardsPerMinute            = new RateUOM(LengthUOM.Yards,       TimeUOM.Minutes);
	public static   final  RateUOM YardsPerHour              = new RateUOM(LengthUOM.Yards,       TimeUOM.Hours);

	public static   final  RateUOM MilesPerMillisecond       = new RateUOM(LengthUOM.Miles,       TimeUOM.Milliseconds);
	public static   final  RateUOM MilesPerDecisecond        = new RateUOM(LengthUOM.Miles,       TimeUOM.Deciseconds);
	public static   final  RateUOM MilesPerSecond            = new RateUOM(LengthUOM.Miles,       TimeUOM.Seconds);
	public static   final  RateUOM MilesPerMinute            = new RateUOM(LengthUOM.Miles,       TimeUOM.Minutes);
	public static   final  RateUOM MilesPerHour              = new RateUOM(LengthUOM.Miles,       TimeUOM.Hours);
	
	// Add derived units of measure here...
	
	public static final RateUOM CanonicalUOM = MillimetersPerMillisecond;
	// Units of distance
	private final LengthUOM lengthUOM; 
	// Units of time
	private final TimeUOM timeUOM; 

	// Create a new canonical unit of rate
	private RateUOM(SystemOfMeasurement systemOfMeasurement, LengthUOM lengthUOM, TimeUOM timeUOM) {
		super(systemOfMeasurement, 
			lengthUOM.getUnitNameSingular() + "/" + lengthUOM.getUnitNameSingular(),
			lengthUOM.getUnitNamePlural() + "/" + lengthUOM.getUnitNamePlural(),
			lengthUOM.getUnitNameAbbreviation() + "/" + lengthUOM.getUnitNameAbbreviation());
		this.lengthUOM = lengthUOM;
		this.timeUOM = timeUOM;
	}
	/*
	 * Create a new canonical unit of distance 
	 */
	public RateUOM(LengthUOM lengthUOM, TimeUOM timeUOM) {
		super(lengthUOM.getUnitNameSingular() + "/" + lengthUOM.getUnitNameSingular(),
				  lengthUOM.getUnitNamePlural() + "/" + lengthUOM.getUnitNamePlural(),
				  lengthUOM.getUnitNameAbbreviation() + "/" + lengthUOM.getUnitNameAbbreviation(),
				  CanonicalUOM,
				  100000 /* TODO */);
		this.timeUOM     = timeUOM;
		this.lengthUOM = lengthUOM;
	}
	// Get the canonical unit of measure
	public LengthUOM getDistanceUOM() {
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