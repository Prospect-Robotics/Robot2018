package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.LengthUOM;

/*
 * class for passing units of distance type
 */
public class Length extends Value<LengthUOM,Length> {
	private final LengthUOM uom;
	
	// public constructor takes lengths only
	public Length(LengthUOM uom, double value) {
		super(uom, value);
		this.uom = uom;
	}
	// Convert to to the specified units and return 
	public Length convertTo(LengthUOM newUOM) {
		// NB: We already check that it's from the same number system, and this is a private factory that will create another instance of the same class/type.
		return create(newUOM, getValueIn(newUOM));
	}

	// Used by superclass as a factory
	protected Length create(LengthUOM uom, double value) {
		return new Length(uom, value);
	}
}
