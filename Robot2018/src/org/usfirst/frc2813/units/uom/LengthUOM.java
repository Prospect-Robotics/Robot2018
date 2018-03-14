package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Value;

/**
 * This class provides a length unit of measurement and concrete implementations of imperial and metric units in terms of a canonical reference unit of 1mm.
 * @author mike.taylor
 */
public class LengthUOM extends UOM<LengthUOM,Length> {
	
	// Metric System
	public static final LengthUOM Millimeters = new LengthUOM(SystemOfMeasurement.Length, "millimeter", "millimeters", "mm");
	public static final LengthUOM Centimeters = new LengthUOM("centimeter", "centimeters", "cm", 10);
	public static final LengthUOM Decimeters  = new LengthUOM("decimeter", "decimeters", "dm", 100);
	public static final LengthUOM Meters      = new LengthUOM("meter", "meters", "m", 1000);
	public static final LengthUOM Kilometers  = new LengthUOM("kilometer", "kilometers", "km", 1000000);

	// Imperial System	
	public static final LengthUOM Thousandths = new LengthUOM("thousandth", "thousandths", "1/1000 in", 0.0254);
	public static final LengthUOM Hundredths  = new LengthUOM("hundredth", "hundredths", "1/100 in", 0.254);
	public static final LengthUOM Tenths      = new LengthUOM("tenth", "tenths", "1/10 in", 2.54);
	public static final LengthUOM Inches      = new LengthUOM("inch", "inches", "in", 25.4);
	public static final LengthUOM Feet        = new LengthUOM("foot", "feet", "ft", 304.8);
	public static final LengthUOM Yards       = new LengthUOM("yard", "yards", "y", 914.4);
	public static final LengthUOM Miles       = new LengthUOM("mile", "miles", "m", 1609340);

	// Talon SRX pulses have to be defined by the subsystem for with a specific scale based on the hardware.  The relationship between of pulses (4096/rotation) and the relationship of rotation to distance.
	// SRX MAG encoder pulses have to be defined by the subsystem for a specific scale, based on the hardware.  The relationship between of pulses (4096/rotation) and the relationship of rotation to distance.
	
	// Add derived units of measure here...
	
	public static final LengthUOM CanonicalUOM = Millimeters;

	// Create the canonical unit of distance in space 
	private LengthUOM(SystemOfMeasurement systemOfMeasurement, String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(systemOfMeasurement, unitNameSingular, unitNamePlural, unitNameAbbreviation);
	}
	// Create a new unit of distance in space 
	public LengthUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation, CanonicalUOM, canonicalUnitQuanity);
	}
	// Create a new value of this type
	public Length create(double value) {
		return new Length(this, value);
	}
}
