package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;

/*
 * class for passing units of distance type
 */
public class Rate extends Value<RateUOM,Rate> {
	private final RateUOM uom;
	// private constructor assumes the arguments are already checked
	public Rate(RateUOM uom, double value) {
		super(uom, value);
		this.uom = uom;
	}
	// Convert to to the specified units and return 
	public Rate convertTo(RateUOM newUOM) {
		// NB: We already check that it's from the same number system, and this is a private factory that will create another instance of the same class/type.
		return new Rate(newUOM, getValueIn(newUOM));
	}
}
