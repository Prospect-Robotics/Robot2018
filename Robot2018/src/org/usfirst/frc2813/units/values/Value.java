package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.uom.UOM;

/*
 * This is a base class for a value with a unit of measure.
 * One subclass is created per measurement type (length, time, rate) and
 * those objects are then interchangeable.
 */
public abstract class Value<T_UOM extends UOM, T_UV extends Value> {
	private final T_UOM uom;
	private final double value;
	
	/* ---------------------------------------------------------------------------------------------------------------
	 * Constructors
	 * --------------------------------------------------------------------------------------------------------------- */
	
	/* Create a new typed unit for a given measurement system */
	public Value(T_UOM uom, double value) {
		this.uom = uom;
		this.value = value;
	}
	/* ---------------------------------------------------------------------------------------------------------------
	 * Unit of Measure Information
	 * --------------------------------------------------------------------------------------------------------------- */
	/* Get the unit of measure */
	public final T_UOM getUOM() {
		return uom;
	}
	/* Get the canonical unit of measure */
	public final T_UOM getCanonicalUOM() {
		return (T_UOM)uom.getCanonicalUOM();
	}
	/* Is this value in the canonical unit of measure? */
	public final boolean isCananicalUOM() {
		return uom == uom.getCanonicalUOM();
	}
	/* ---------------------------------------------------------------------------------------------------------------
	 * Unit Of Measure Information Shortcuts 
	 * --------------------------------------------------------------------------------------------------------------- */

	/* Get the name of the unit of measure */
	public final String getUnitNameSingular() {
		return uom.getUnitNameSingular();
	}
	/* Get the name of the unit of measure */
	public final String getUnitNamePlural() {
		return uom.getUnitNamePlural();
	}
	/* Get the name of the unit of measure */
	public final String getUnitNameAbbreviation() {
		return uom.getUnitNameAbbreviation();
	}
    /* Get the type label (convenience shortcut) */
    public final SystemOfMeasurement getSystemOfMeasurement() {
        return uom.getSystemOfMeasurement();
    }

	/* ---------------------------------------------------------------------------------------------------------------
	 * Type Checks 
	 * --------------------------------------------------------------------------------------------------------------- */

	/* Is the specified unit of measure of the same system of measurement? */
	public final boolean is(T_UOM uom) {
		return this.uom == uom;
	}

	/* Is the specified unit of measure of the same system of measurement? */
	public final boolean isCompatibleWith(T_UOM uom) {
		return uom.getSystemOfMeasurement() == getSystemOfMeasurement();
	}

	/* Is the specified unit of measure of the same system of measurement? */
	public final boolean isCompatibleWith(SystemOfMeasurement som) {
		return som == getSystemOfMeasurement();
	}

	/* Is the specified unit of measure of the same system of measurement? */
	public final boolean isCompatibleWith(T_UV value) {
		return isCompatibleWith(value.getSystemOfMeasurement());
	}
	
	/* ---------------------------------------------------------------------------------------------------------------
	 * Numeric Conversions
	 * --------------------------------------------------------------------------------------------------------------- */

	/* Get the value in the units of this type. */
	public final double getValue() {
		return this.value;
	}
	/* Get the value in the units of this type. */
	public final int getValueAsInt() {
		return (int)Math.round(this.value);
	}
	/* Get the value in the units of this type. */
	public final long getValueAsLong() {
		return Math.round(this.value);
	}
	/* Get the canonical form of the value */
	public final double getCanonicalValue() {
		return this.value * uom.getCanonicalUnitQuantity();
	}

	/* Get the value converted to a different UOM, without creating another object */
	public final double getValueIn(T_UOM uom) {
		// Make sure it's the same type of measurement
		if(!isCompatibleWith(uom)) {
			throw new IllegalArgumentException("Incompatible measurement systems: " + getSystemOfMeasurement() + " and " + uom.getSystemOfMeasurement() + ".");
		}
		return getCanonicalValue()/uom.getCanonicalUnitQuantity();
	}

	/* ---------------------------------------------------------------------------------------------------------------
	 * Conversions
	 * --------------------------------------------------------------------------------------------------------------- */

	// Convert to to the specified units and return 
	public abstract T_UV convertTo(T_UOM newUOM);

	// Get the canonical form of the value  
	public final T_UV convertToCanonicalUOM() {
		return convertTo(getCanonicalUOM()); 
	}	

	/* ---------------------------------------------------------------------------------------------------------------
	 * Descriptions and Diagnostics 
	 * --------------------------------------------------------------------------------------------------------------- */
	
	// Get the short description in <units> <abbrev>
	public final String getShortDescription() {
		return getValue() + " " + uom.getUnitNameAbbreviation();
	}

    // Show the type+value description
	public String toString() {
		return getShortDescription();
	}
	
	/* ---------------------------------------------------------------------------------------------------------------
	 * Comparisons 
	 * --------------------------------------------------------------------------------------------------------------- */
	
	// Two value are equal if they are of the same system of measure and represent the same absolute value (regardless of units used to express the value) */
	public boolean equals(T_UV other) {
		if(!isCompatibleWith(other)) {
			return false;
		}
		return getCanonicalValue() == other.getCanonicalValue();
	}
	
	/* ---------------------------------------------------------------------------------------------------------------
	 * Factory
	 * --------------------------------------------------------------------------------------------------------------- */
	protected abstract T_UV create(T_UOM uom, double value);
	
	/* ---------------------------------------------------------------------------------------------------------------
	 * Math 
	 * --------------------------------------------------------------------------------------------------------------- */

	public T_UV add(T_UV other) {
		return create(this.uom, this.value + other.getValueIn(uom));
	}

	public T_UV subtract(T_UV other) {
		return create(this.uom, this.value - other.getValueIn(uom));
	}

	public T_UV multiply(T_UV other) {
		return create(this.uom, this.value * other.getValueIn(uom));
	}
	
	public T_UV divide(T_UV other) {
		return create(this.uom, this.value / other.getValueIn(uom));
	}
	
	public T_UV add(double value) {
		return create(this.uom, this.value + value);
	}

	public T_UV subtract(double value) {
		return create(this.uom, this.value - value);
	}

	public T_UV multiply(double value) {
		return create(this.uom, this.value * value);
	}
	
	public T_UV divide(double value) {
		return create(this.uom, this.value / value);
	}
}
