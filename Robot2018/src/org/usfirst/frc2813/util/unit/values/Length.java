package org.usfirst.frc2813.util.unit.values;

import org.usfirst.frc2813.util.unit.uom.LengthUOM;
import org.usfirst.frc2813.util.unit.uom.UOM;

/*
 * class for passing units of distance type
 */
public class Length extends UnitValue {
	// private constructor assumes the arguments are already checked
	private Length(UOM uom, double value) {
		super(uom, value);
	}
	// public constructor takes lengths only
	public Length(LengthUOM uom, double value) {
		super(uom, value);
	}

	@Override
	protected Length factory(UOM uom, double value) {
		// NB: parameters already checked by caller
		return new Length((LengthUOM)uom, value);
	}

}
