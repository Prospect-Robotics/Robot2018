package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
	
	public static Map<SystemOfMeasurement,List<UOM>> allUnits = new HashMap<SystemOfMeasurement,List<UOM>>();

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
		registerUnitOfMeasurement(this);
	}
	
	// Construct a new unit of measure in terms of a canonical unit of measure, including an integral scaling factor.  i.e cm is 10 mm.
	protected UOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, UOM<T_UOM,T_UV> canonicalUOM, double canonicalUnitQuantity) {
		this.canonicalUOM = canonicalUOM;
		this.systemOfMeasurement = canonicalUOM.getSystemOfMeasurement();
		this.unitNameSingular = unitNameSingular;
		this.unitNamePlural = unitNamePlural;
		this.unitNameAbbreviation = unitNameAbbreviation;
		this.canonicalUnitQuantity   = canonicalUnitQuantity;
		registerUnitOfMeasurement(this);
	}

	/* ---------------------------------------------------------------------------------------------------------------
	 * Registry of units of measure
	 * --------------------------------------------------------------------------------------------------------------- */
	
	private static void registerUnitOfMeasurement(UOM uom) {
		List<UOM> unitsOfSystem = allUnits.get(uom.getSystemOfMeasurement());
		if(unitsOfSystem == null) {
			allUnits.put(uom.getSystemOfMeasurement(), unitsOfSystem = new ArrayList<UOM>());
		}
		unitsOfSystem.add(uom);
	}

	public static void dumpUnitsOfMeasure() {
		java.util.Iterator<SystemOfMeasurement> allSystems = allUnits.keySet().iterator();
		while(allSystems.hasNext()) {
			SystemOfMeasurement system = allSystems.next();
			System.out.println("[" + system + "]");
			Iterator<UOM> units = allUnits.get(system).iterator();
			while(units.hasNext()) {
				UOM unitOfMeasure = units.next(); 
				System.out.println(unitOfMeasure + " = " + unitOfMeasure.getCanonicalValue());
			}
			System.out.println();
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
		return canonicalUOM.create(canonicalUnitQuantity);
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