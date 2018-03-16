package org.usfirst.frc2813.units;

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
	POSITIVE(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	NEGATIVE(Canonical.NEGATIVE),
	/** Alias for NEUTRAL */
	NEUTRAL(Canonical.NEUTRAL),
	/** Alias for POSITIVE */
	FORWARD(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	BACKWARD(Canonical.NEGATIVE),
	/** Alias for NEGATIVE */
	REVERSE(Canonical.NEGATIVE),
	
	/** Alias for NEGATIVE */
	IN(Canonical.NEGATIVE),
	/** Alias for POSITIVE */
	OUT(Canonical.POSITIVE),

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

	/* Alias for POSITIVE */
	ON(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	OFF(Canonical.NEGATIVE),

	/* Alias for POSITIVE */
	STOP(Canonical.POSITIVE),
	/** Alias for NEGATIVE */
	GO(Canonical.NEGATIVE),
	
	/** Alias for NEUTRAL */
	CENTER(Canonical.NEUTRAL);
	
	
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
	 * Two directions are equal if they are the same or have the same canonical representation
	 */
	public boolean equals(Direction other) {
		return other.canonicalDirection == this.canonicalDirection;
	}
	/*
	 * If we know which value is the preferred logical inverse i.e. up -> down, return it.
	 * Some cases are ambiguous but the idea is clearer debug messages and behaving logically.  
	 * Otherwise returns the canonical value for the inverse.
	 * Neutral directions always return themselves.
	 */
	public Direction getInverse() {
		// No logical inverses for neutral
		if(canonicalDirection == Canonical.NEUTRAL) {
			return this;
		}
		
		// NB: Default case of POSITIVE and NEGATIVE handled by fallthrough case
		switch(this) {
		case FORWARD:
			return REVERSE;
		case BACKWARD:
			return FORWARD;
		case REVERSE:
			return FORWARD;
		case IN:
			return OUT;
		case OUT:
			return IN;
		case CLOCKWISE:
			return COUNTERCLOCKWISE;
		case COUNTERCLOCKWISE:
			return CLOCKWISE;
		case UP:
			return DOWN;
		case DOWN:
			return UP;
		case RIGHT:
			return LEFT;
		case LEFT:
			return RIGHT;
		case ON:
			return OFF;
		case OFF:
			return ON;
		case STOP:
			return GO;
		case GO:
			return STOP;
		default:
			if(canonicalDirection == Canonical.POSITIVE) {
				return NEGATIVE;
			} else {
				return POSITIVE;
			}
		}
	}
}