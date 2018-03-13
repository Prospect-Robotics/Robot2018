package org.usfirst.frc2813.util.unit.values;

import org.usfirst.frc2813.util.unit.uom.UOM;

/*
 * This is a base class for a value with a unit of measure.
 * One subclass is created per measurement type (length, time, rate) and
 * those objects are then interchangeable.
 */
public abstract class UnitValue {
	private final UOM uom;
	private final double value;
	/* 
	 * Create a new typed unit for a given measurement system
	 */
	public UnitValue(UOM uom, double value) {
		this.uom = uom;
		this.value = value;
	}
	/*
	 * Convert to to the specified units and return
	 */
	public UnitValue convertTo(UOM newUOM) {
		// Make sure it's a valid unit of measure
		if(newUOM == null) 
			throw new IllegalArgumentException("convertTo got null unit of measure.");
		// Make sure it's the same type of measurement
		if(!isCompatibleUOM(newUOM)) {
			throw new IllegalArgumentException("Cannot convertValue from " + getUOM().getCanonicalUOM() + " to " + newUOM.getCanonicalUOM() + ".");
		}
		// NB: We already check that it's from the same number system, and this is a private factory that will create another instance of the same class/type.
		return factory(newUOM, getCanonicalValue());
	}
	/*
	 * Get the value
	 */
	public double getValue() {
		return this.value;
	}
	/* Is the specified unit of measure for the same kind of units? */
	public boolean isCompatibleUOM(UOM uom) {
		return uom.getCanonicalUOM() == getCanonicalUOM();
	}
	/* Is the specified unit of measure for the same kind of units? */
	public boolean isCompatibleUOM(UnitValue unitValue) {
		return isCompatibleUOM(unitValue.getUOM());
	}
	/* Get the string representation */
	public boolean equals(UnitValue other) {
		if(null == other) {
			return false;
		}
		if(getCanonicalUOM() == other.getCanonicalUOM()) {
			return false;
		}
		return getCanonicalValue() == other.getCanonicalValue();
	}
	/* Get the canonical unit of measure */
	public UOM getCanonicalUOM() {
		return getUOM().getCanonicalUOM();
	}
	// Is this the canonical unit of measure?
	public boolean isCananicalUOM() {
		return getUOM() == getUOM().getCanonicalUOM();
	}	
	/* Get the canonical form of the value  */
	public UnitValue getCanonicalUnitValue() {
		return convertTo(getCanonicalUOM()); 
	}
	/* Get the canonical form of the value */
	public double getCanonicalValue() {
		return getCanonicalUnitValue().getValue(); 
	}
	// Get the short description in <units> <abbrev>
	public String getShortDescription() {
		return getValue() + " " + getUOM().getUnitNameAbbreviation();
	}
    // Show the type+value description
	public String toString() {
		return getShortDescription();
	}
    // Get the type label (convenience shortcut)
    public String getUOMTypeLabel() {
        return getUOM().getUOMTypeLabel();
    }
	/* Get the unit of measure, which should be constant for a class of units like DistanceUnit  */
	public UOM getUOM() {
		return uom;
	}
	/* Create a new unit of measure of a compatible type of units with the indicated value. */
	protected abstract UnitValue factory(UOM uom, double value);
}
