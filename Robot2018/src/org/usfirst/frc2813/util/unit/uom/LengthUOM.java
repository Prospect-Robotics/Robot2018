package org.usfirst.frc2813.util.unit.uom;

import org.usfirst.frc2813.util.unit.values.Length;
import org.usfirst.frc2813.util.unit.values.UnitValue;

/**
 * This class provides a length unit of measurement and concrete implementations of imperial and metric units in terms of a canonical reference unit of 1mm.
 * @author mike.taylor
 */

public class LengthUOM extends UOM {
	
	// Metric System
	public static final LengthUOM Millimeters = new LengthUOM("millimeter", "millimeters", "mm");
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
	// ...
	/*
	 * Create a new unit of distance 
	 */
	public LengthUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation, double canonicalUnitQuanity) {
		super(unitNameSingular, unitNamePlural, unitNameAbbreviation, canonicalUnitQuanity);
	}
	/* 
	 * Create a new canonical unit of distance 
	 */
	private LengthUOM(String unitNameSingular, String unitNamePlural, String unitNameAbbreviation) {
		super(unitNameSingular, unitNameSingular, unitNameAbbreviation);
	}
	/*
	 * Get the canonical unit of measure
	 */
	public LengthUOM getCanonicalUOM() {
		return CanonicalUOM;
	}
	/*
	 * Get the name of the type of unit 
	 */
	 public String getUOMTypeLabel() {
		 return "Length"; 
	 }
	 /*
	  * Create a new UnitValue with this UOM
	  */
	 public Length create(double value) {
		 return new Length(this, value);
	 }
}
