package org.usfirst.frc2813.util.unit.uom;

import org.usfirst.frc2813.util.unit.values.UnitValue;

/*
 * Abstract unit of measurement 
 */
public abstract class UOM {
	// Name for the unit
	private final String unitNameSingular;
	private final String unitNamePlural;
	private final String unitNameAbbreviation;
	// Number of canonical units
	private final double canonicalUnitQuantity;
	/* 
	 * Create a new canonical unit of measurement 
	 */
	protected UOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		this.unitNameSingular = unitNameSingular;
		this.unitNamePlural = unitNamePlural;
		this.unitNameAbbreviation = unitNameAbbreviation;
		this.canonicalUnitQuantity = 1;
	}
	/*
	 * Construct a new unit of measure in terms of a canonical unit of measure, including an integral scaling factor.  i.e cm is 10 mm.
	 */
	protected UOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, double canonicalUnitQuantity) {
		this.unitNameSingular = unitNameSingular;
		this.unitNamePlural = unitNamePlural;
		this.unitNameAbbreviation = unitNameAbbreviation;
		this.canonicalUnitQuantity   = canonicalUnitQuantity;
		if(canonicalUnitQuantity <= 1) {
			throw new IllegalArgumentException("Non-canonical unit quantity must be >1, by definition.");
		}
	}
	/*
	 * Get the name of the unit of measure
	 */
	public String getUnitNameSingular() {
		return unitNameSingular;
	}
	/*
	 * Get the name of the unit of measure
	 */
	public String getUnitNamePlural() {
		return unitNamePlural;
	}
	/*
	 * Get the name of the unit of measure
	 */
	public String getUnitNameAbbreviation() {
		return unitNameAbbreviation;
	}
	/*
	 * Return the name of the unit of measure.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return unitNamePlural;
	}
	/*
	 * Convert to the canonical units
	 */
	public double getCanonicalUnitQuantity() {
		return canonicalUnitQuantity;
	}
	/*
	 * Is this the canonical unit of measure?
	 */
	public boolean isCananicalUOM() {
		return this == getCanonicalUOM();
	}
	/*
	 * Get the canonical unit of measure
	 */
	public abstract UOM getCanonicalUOM();
	/*
	 * Convert to the canonical units
	 */
	public UnitValue getCanonicalValue() {
		return getCanonicalUOM().create(getCanonicalUnitQuantity());
	}
	/*
	 * Get the name of the type of unit 
	 */
	public abstract String getUOMTypeLabel();
	/*
	 * Return true if the type is the same
	 */
	public boolean isCompatibleUOM(UOM uom) {
		return uom != null && getCanonicalUOM() == uom.getCanonicalUOM();
	}
	/* Two units of measure are equal if they represent the same scalar value in the same type of measurement.  Two distances with different units are still equal if they represent the same length. */
	public boolean equals(UOM otherUOM) {
		// Do we both represent the same type of measurement? 
		if(!isCompatibleUOM(otherUOM)) {
			return false;
		}
		// Do we both represent the same canonical scalar value?
		return getCanonicalUnitQuantity() == otherUOM.getCanonicalUnitQuantity();
	}
	/* Create a new value of this type */
	public abstract UnitValue create(double value);
}