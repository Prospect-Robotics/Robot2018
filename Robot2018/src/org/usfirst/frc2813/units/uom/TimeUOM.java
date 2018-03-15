package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Time;

public class TimeUOM extends UOM<TimeUOM, Time> {
	// Time based on microseconds
    public static final TimeUOM CanonicalTimeUOM = new TimeUOM(SystemOfMeasurement.Time, "microsecond", "microseconds", "us");
	public static final TimeUOM Microsecond  = CanonicalTimeUOM;
	public static final TimeUOM Milliseconds = new TimeUOM("millisecond", "milliseconds", "ms", CanonicalTimeUOM, 1000.0);
	public static final TimeUOM Deciseconds  = new TimeUOM("decisecond", "deciseconds", "ds", CanonicalTimeUOM, 100000.0);
	public static final TimeUOM Seconds      = new TimeUOM("second", "seconds", "sec", CanonicalTimeUOM, 1000000.0);
	public static final TimeUOM Minutes      = new TimeUOM("minute", "minutes", "min", CanonicalTimeUOM, 60000000.0);
	public static final TimeUOM Hours        = new TimeUOM("hour", "hours", "hr", CanonicalTimeUOM, 3600000000.0);
	
	// Create the canonical unit of time 
	private TimeUOM(SystemOfMeasurement systemOfMeasurement, String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(systemOfMeasurement, unitNameSingular, unitNamePlural, unitNameAbbreviation);
	}
	// Create a new unit of distance in time 
	public TimeUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, TimeUOM canonicalUOM, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation, canonicalUOM, canonicalUnitQuanity);
	}
	// Create a new value of this type
	public Time create(double value) {
		return new Time(this, value);
	}
}
