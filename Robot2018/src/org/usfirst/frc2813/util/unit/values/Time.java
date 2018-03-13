package org.usfirst.frc2813.util.unit.values;

import org.usfirst.frc2813.util.unit.uom.TimeUOM;
import org.usfirst.frc2813.util.unit.uom.UOM;

/*
 * class for passing units of distance type
 */
public class Time extends UnitValue {
	// private constructor assumes the arguments are already checked
	private Time(UOM uom, double value) {
		super(uom, value);
	}
	// public constructor takes lengths only
	public Time(TimeUOM uom, double value) {
		super(uom, value);
	}

	@Override
	protected Time factory(UOM uom, double value) {
		// NB: parameters already checked by caller
		return new Time((TimeUOM)uom, value);
	}

}
