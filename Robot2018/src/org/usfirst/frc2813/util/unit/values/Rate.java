package org.usfirst.frc2813.util.unit.values;

import org.usfirst.frc2813.util.unit.uom.RateUOM;
import org.usfirst.frc2813.util.unit.uom.TimeUOM;
import org.usfirst.frc2813.util.unit.uom.UOM;

/*
 * class for passing units of distance type
 */
public class Rate extends UnitValue {
	// private constructor assumes the arguments are already checked
	public Rate(RateUOM uom, double value) {
		super(uom, value);
	}

	@Override
	protected Rate factory(UOM uom, double value) {
		// NB: parameters already checked by caller
		return new Rate((RateUOM)uom, value);
	}
}
