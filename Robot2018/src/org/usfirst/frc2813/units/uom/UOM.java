package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Time;
import org.usfirst.frc2813.units.values.Value;

/*
 * Abstract unit of measurement 
 */
public abstract class UOM<T_UOM extends UOM, T_UV extends Value> {
	// Name for the unit
	private final String unitNameSingular;
	private final String unitNamePlural;
	private final String unitNameAbbreviation;
	// Canonical unit
	private final UOM<T_UOM,T_UV> canonicalUOM;
	// Number of canonical units
	private final double canonicalUnitQuantity;
	// System of measurement
	private final SystemOfMeasurement systemOfMeasurement;

	/* ---------------------------------------------------------------------------------------------------------------
	 * Constructors
	 * --------------------------------------------------------------------------------------------------------------- */
	
	// Create a new canonical unit of measurement 
	protected UOM(SystemOfMeasurement systemOfMeasurement, String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		this.systemOfMeasurement = systemOfMeasurement;
		this.canonicalUOM = this;
		this.unitNameSingular = unitNameSingular;
		this.unitNamePlural = unitNamePlural;
		this.unitNameAbbreviation = unitNameAbbreviation;
		this.canonicalUnitQuantity = 1;
	}
	
	// Construct a new unit of measure in terms of a canonical unit of measure, including an integral scaling factor.  i.e cm is 10 mm.
	protected UOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, T_UOM canonicalUOM, double canonicalUnitQuantity) {
		this.systemOfMeasurement = canonicalUOM.getSystemOfMeasurement();
		this.canonicalUOM = canonicalUOM;
		this.unitNameSingular = unitNameSingular;
		this.unitNamePlural = unitNamePlural;
		this.unitNameAbbreviation = unitNameAbbreviation;
		this.canonicalUnitQuantity   = canonicalUnitQuantity;
		if(canonicalUnitQuantity <= 1) {
			throw new IllegalArgumentException("Non-canonical unit quantity must be >1, by definition.");
		}
	}
	/* ---------------------------------------------------------------------------------------------------------------
	 * Descriptive Information
	 * --------------------------------------------------------------------------------------------------------------- */

	// Is this the canonical unit of measure?
	public final boolean isCananicalUOM() {
		return this == getCanonicalUOM();
	}
	// Get the system of measurement
	public final SystemOfMeasurement getSystemOfMeasurement() {
		return systemOfMeasurement;
	}
	// Get the canonical unit of measure
	public final T_UOM getCanonicalUOM() {
		return (T_UOM)canonicalUOM;
	}
	// Get the name of the unit of measure
	public final String getUnitNameSingular() { 
		return unitNameSingular;
	}
	// Get the name of the unit of measure
	public final String getUnitNamePlural() {
		return unitNamePlural;
	}
	// Get the name of the unit of measure
	public final String getUnitNameAbbreviation() {
		return unitNameAbbreviation;
	}
	// Return the name of the unit of measure.
	public String toString() {
		return unitNamePlural;
	}
	// Get the number of canonical units represented by this unit of measure
	public final double getCanonicalUnitQuantity() {
		return canonicalUnitQuantity;
	}
	// Convert to the canonical units
	public final T_UV getCanonicalValue() {
		return (T_UV)canonicalUOM.create(canonicalUnitQuantity);
	}
	// Return true if the system of measurement is the same
	public final boolean isCompatibleWith(SystemOfMeasurement som) {
		return getSystemOfMeasurement() == som;
	}
	// Create a new value of this type
	public abstract T_UV create(double value);
	// Compare to values
	public final boolean equals(T_UOM otherUOM) {
		// Do we both represent the same canonical scalar value?
		return getCanonicalUnitQuantity() == otherUOM.getCanonicalUnitQuantity();
	}
}