package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Time;
import org.usfirst.frc2813.units.values.Value;

public class TimeUOM extends UOM<TimeUOM, Time> {
	public static final TimeUOM Milliseconds = new TimeUOM(SystemOfMeasurement.Time, "millisecond", "milliseconds", "ms");
	public static final TimeUOM Deciseconds  = new TimeUOM("decisecond", "deciseconds", "ds", 1000);
	public static final TimeUOM Seconds      = new TimeUOM("second", "seconds", "s", 1000);
	public static final TimeUOM Minutes      = new TimeUOM("minute", "minutes", "m", 60 * 1000);
	public static final TimeUOM Hours        = new TimeUOM("hour", "hours", "h", 60 * 60 * 1000);
	public static final TimeUOM CanonicalUOM = Milliseconds;
	
	// Create the canonical unit of time 
	private TimeUOM(SystemOfMeasurement systemOfMeasurement, String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(systemOfMeasurement, unitNameSingular, unitNamePlural, unitNameAbbreviation);
	}
	// Create a new unit of distance in time 
	public TimeUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation, Milliseconds, canonicalUnitQuanity);
	}
	// Create a new value of this type
	public Time create(double value) {
		return new Time(this, value);
	}
}
