package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Length;

/**
 * This class provides a length unit of measurement and concrete implementations of imperial and metric units in terms of a canonical reference unit of 1mm.
 * @author mike.taylor
 */
public class LengthUOM extends UOM<LengthUOM,Length> {
	public static final LengthUOM CanonicalLengthUOM = new LengthUOM(SystemOfMeasurement.Length, "micrometer", "micrometers", "um");
	// Metric System
	public static final LengthUOM Micrometers = CanonicalLengthUOM;
	public static final LengthUOM Millimeters = new LengthUOM("millimeter", "millimeters", "mm", CanonicalLengthUOM, 1000);
	public static final LengthUOM Centimeters = new LengthUOM("centimeter", "centimeters", "cm", CanonicalLengthUOM, 10000);
	public static final LengthUOM Decimeters  = new LengthUOM("decimeter", "decimeters", "dm", CanonicalLengthUOM, 100000);
	public static final LengthUOM Meters      = new LengthUOM("meter", "meters", "m", CanonicalLengthUOM, 1000000);
	public static final LengthUOM Kilometers  = new LengthUOM("kilometer", "kilometers", "km", CanonicalLengthUOM, 1000000000);

	// Imperial System	
	public static final LengthUOM Thousandths = new LengthUOM("thousandth", "thousandths", "thousandths", CanonicalLengthUOM, 25.4);
	public static final LengthUOM Hundredths  = new LengthUOM("hundredth", "hundredths", "hundredths", CanonicalLengthUOM, 254);
	public static final LengthUOM Tenths      = new LengthUOM("tenth", "tenths", "tenths", CanonicalLengthUOM, 2540);
	public static final LengthUOM Inches      = new LengthUOM("inch", "inches", "in", CanonicalLengthUOM, 25400);
	public static final LengthUOM Feet        = new LengthUOM("foot", "feet", "ft", CanonicalLengthUOM, 304800);
	public static final LengthUOM Yards       = new LengthUOM("yard", "yards", "y", CanonicalLengthUOM, 914400);
	public static final LengthUOM Miles       = new LengthUOM("mile", "miles", "m", CanonicalLengthUOM, 1609340000);

	// Talon SRX pulses have to be defined by the subsystem for with a specific scale based on the hardware.  The relationship between of pulses (4096/rotation) and the relationship of rotation to distance.
	// SRX MAG encoder pulses have to be defined by the subsystem for a specific scale, based on the hardware.  The relationship between of pulses (4096/rotation) and the relationship of rotation to distance.
	
	// Add derived units of measure here...

	// Create the canonical unit of distance in space 
	private LengthUOM(SystemOfMeasurement systemOfMeasurement, String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(systemOfMeasurement, unitNameSingular, unitNamePlural, unitNameAbbreviation);
	}
	// Create a new unit of distance in space 
	public LengthUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, LengthUOM canonicalUOM, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation, canonicalUOM, canonicalUnitQuanity);
	}
	// Create a new value of this type
	public Length create(double value) {
		return new Length(this, value);
	}
}
