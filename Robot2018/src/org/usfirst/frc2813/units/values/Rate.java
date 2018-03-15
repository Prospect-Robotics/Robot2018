package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.RateUOM;

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
		return create(newUOM, getValueIn(newUOM));
	}

	// Used by superclass as a factory
	protected Rate create(RateUOM uom, double value) {
		return new Rate(uom, value);
	}
}
