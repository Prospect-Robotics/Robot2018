package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.TimeUOM;

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
		return create(newUOM, getValueIn(newUOM));
	}

	// Used by superclass as a factory
	protected Time create(TimeUOM uom, double value) {
		return new Time(uom, value);
	}
}
