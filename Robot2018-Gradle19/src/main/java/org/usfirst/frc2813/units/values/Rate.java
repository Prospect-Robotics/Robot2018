package org.usfirst.frc2813.units.values;

import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;

/*
 * class for passing units of distance type
 */
public class Rate extends Value<RateUOM,Rate> {
	// private constructor assumes the arguments are already checked
	public Rate(RateUOM uom, double value) {
		super(uom, value);
	}
	// Convert to to the specified units and return 
	public Rate convertTo(RateUOM newUOM) {
		// NB: We already check that it's from the same number system, and this is a private factory that will create another instance of the same class/type.
		return create(newUOM, getValueIn(newUOM));
	}
	// Used by superclass as a factory
	protected Rate create(RateUOM uom, double value) {
		return new Rate(uom, value);
	}
	/*
	 * Rate * Time = Distance
	 */
	public Length getLength(Time time) {
		// Convert time to same units
		Time   convertedTime = time.convertTo(getTimeUOM());
		Length distance      = getLengthUOM().create((/*rate*/getCanonicalValue() * /*time*/convertedTime.getCanonicalValue()) / getLengthUOM().getCanonicalUnitQuantity());
//		System.out.println(this + " x " + time + " = " + distance);
		return distance;
	}	
	/*
	 * Distance / Rate = Time
	 */
	public Time getTime(Length length) {
		// Convert length to same units
		Length convertedLength = length.convertTo(getUOM().getLengthUOM());
		Time   time = getTimeUOM().create(convertedLength.getCanonicalValue()/getCanonicalValue()/getTimeUOM().getCanonicalUnitQuantity());
//		System.out.println(length + " / " + rate + " = " + time);
		return time;
	}
	// Get TimeUOM 
	public TimeUOM getTimeUOM() {
		return getUOM().getTimeUOM();
	}
	// Get LengthUOM 
	public LengthUOM getLengthUOM() {
		return getUOM().getLengthUOM();
	}
	/**
	 * Clamp a rate to a limit range
	 */
	public static Rate clampToLimit(Rate lowerLimit, Rate upperLimit, Rate input) {
		Rate r = input;
		if(upperLimit != null && input.getCanonicalValue() > upperLimit.getCanonicalValue()) {
			r = upperLimit;
		} 
		if(lowerLimit != null && input.getCanonicalValue() < lowerLimit.getCanonicalValue()) {
			r = lowerLimit;
		}
		return r;
	}
}
