package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;

/*
 * class for passing units of distance type
 */
public class Time extends Value<TimeUOM,Time> {
	// public constructor takes lengths only
	public Time(TimeUOM uom, double value) {
		super(uom, value);
	}
	// Convert to to the specified units and return 
	public Time convertTo(TimeUOM newUOM) {
		// NB: We already check that it's from the same number system, and this is a private factory that will create another instance of the same class/type.
		return new Time(newUOM, getValueIn(newUOM));
	}
}
