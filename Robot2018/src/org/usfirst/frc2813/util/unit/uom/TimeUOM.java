package org.usfirst.frc2813.util.unit.uom;

import org.usfirst.frc2813.util.unit.values.Time;
import org.usfirst.frc2813.util.unit.values.UnitValue;

public class TimeUOM extends UOM {
	public static final TimeUOM Milliseconds = new TimeUOM("millisecond", "milliseconds", "ms");
	public static final TimeUOM Deciseconds  = new TimeUOM("decisecond", "deciseconds", "ds", 1000);
	public static final TimeUOM Seconds      = new TimeUOM("second", "seconds", "s", 1000);
	public static final TimeUOM Minutes      = new TimeUOM("minute", "minutes", "m", 60 * 1000);
	public static final TimeUOM Hours        = new TimeUOM("hour", "hours", "h", 60 * 60 * 1000);
	public static final TimeUOM CanonicalUOM = Milliseconds;
	
	// Add derived units of measure here...
	/* 
	 * Create a new canonical unit of distance 
	 */
	private TimeUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(unitNameSingular, unitNameSingular, unitNameAbbreviation);
	}
	/*
	 * Create a new unit of distance 
	 */
	public TimeUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation);
	}
	/*
	 * Get the canonical unit of measure
	 */
	public TimeUOM getCanonicalUOM() {
		return CanonicalUOM;
	}
	/*
	 * Get the name of the type of unit 
	 */
	 public String getUOMTypeLabel() {
		 return "Time"; 
	 }
	/* Create a new value of this type */
	public Time create(double value) {
		return new Time(this, value);
	}
}
