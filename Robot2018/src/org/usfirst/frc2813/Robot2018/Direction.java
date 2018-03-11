package org.usfirst.frc2813.Robot2018;

/**
 * Standardize the use of directions. 
 * This class abstracts LOGICAL directions (not physical).
 * It is the agreed upon way we think about the directions 
 * of our devices (not our motors) when we work with our
 * higher level classes and code.
 * 
 * CRITICAL: 
 * Example: If we decide that IN is REVERSE and OUT is FORWARD,
 * but that is completely independent of whether the actual
 * motor wires are inverted, or the sensor phase is reversed.  
 */
public enum Direction{
	/** Alias for POSITIVE */
	FORWARD(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	BACKWARD(Canonical.NEGATIVE),
	/** Alias for NEGATIVE */
	REVERSE(Canonical.NEGATIVE),
	
	/** Alias for NEGATIVE */
	IN(Canonical.NEGATIVE), // TODO: This should probably be reversed.
	/** Alias for POSITIVE */
	OUT(Canonical.POSITIVE), // TODO: This should probably be reversed.

	/** Alias for POSITIVE */
	CLOCKWISE(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	COUNTERCLOCKWISE(Canonical.NEGATIVE),

	/** Alias for POSITIVE */
	UP(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	DOWN(Canonical.NEGATIVE),
	
	/** Alias for POSITIVE */
	RIGHT(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	LEFT(Canonical.NEGATIVE),
	
	/** Alias for POSITIVE */
	OPEN(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	CLOSE(Canonical.NEGATIVE),

	/** Alias for NEUTRAL */
	CENTER(Canonical.NEUTRAL),
	/** Alias for NEUTRAL */
	STOP(Canonical.NEUTRAL),
	ON(Canonical.POSITIVE),
	/** Alias for NEUTRAL */
	OFF(Canonical.NEUTRAL);
	
	Canonical canonicalDirection;
	Direction(Canonical canonicalDirection) {
		this.canonicalDirection = canonicalDirection;
	}
	
	public enum Canonical{
		/** Logical direction for going in a logically positive/forward/do direction **/
		POSITIVE(1),
		/** Logical direction for going in a logically negative/reversed/undo direction **/
		NEGATIVE(-1),
		/** Logical direction for not going anywhere (multiplier is zero!).  Logically stop. **/
		NEUTRAL(0);
		
		int value;
		Canonical(int value){
			this.value=value;
		}
	}
	
	/**
	 * Get the logical multiplier.  POSITIVE x1, NEGATIVE x-1, NEUTRAL x0
	 */
	public double getMultiplierAsDouble() {
		return canonicalDirection.value;
	}
	/**
	 * Get the logical multiplier.  POSITIVE x1, NEGATIVE x-1, NEUTRAL x0
	 */
	public int getMultiplier() {
		return canonicalDirection.value;
	}
	/**
	 * Is this a neutral direction
	 */
	public boolean isNeutral() {
		return canonicalDirection == Canonical.NEUTRAL;
	}
	/**
	 * Is this a positive direction
	 */
	public boolean isPositive() {
		return canonicalDirection == Canonical.POSITIVE;
	}
	/**
	 * Is this a negative direction
	 */
	public boolean isNegative() {
		return canonicalDirection == Canonical.NEGATIVE;
	}
	/**

	
	public String toString() {
		return label;
	}
	
	/**
	 * Two directions are equal if they are the same or have the same canonical representation
	 */
	public boolean equals(Direction other) {
		return other.canonicalDirection == this.canonicalDirection;
	}
}