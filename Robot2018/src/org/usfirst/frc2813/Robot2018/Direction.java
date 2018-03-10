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
public class Direction implements java.lang.Comparable<Direction> {

	private static final int POSITIVE_MULTIPLIER =  1;
	private static final int NEUTRAL_MULTIPLIER  =  0;
	private static final int NEGATIVE_MULTIPLIER = -1;
	
	/** Logical direction for going in a logically positive/forward/do direction **/
	public static final Direction POSITIVE = new Direction(POSITIVE_MULTIPLIER, "POSITIVE");
	/** Logical direction for going in a logically negative/reversed/undo direction **/
	public static final Direction NEGATIVE = new Direction(NEGATIVE_MULTIPLIER, "NEGATIVE");
	/** Logical direction for not going anywhere (multiplier is zero!).  Logically stop. **/
	public static final Direction NEUTRAL = new Direction(NEUTRAL_MULTIPLIER, "NEUTRAL");
		
	/** Alias for POSITIVE */
	public static final Direction FORWARD  = new Direction(POSITIVE, "FORWARD");
	/** Alias for NEGATIVE */
	public static final Direction BACKWARD = new Direction(NEGATIVE, "BACKWARD");
	/** Alias for NEGATIVE */
	public static final Direction REVERSE = new Direction(NEGATIVE, "REVERSE");
	
	/** Alias for NEGATIVE */
	public static final Direction IN      = new Direction(NEGATIVE, "IN"); // TODO: This should probably be reversed.
	/** Alias for POSITIVE */
	public static final Direction OUT     = new Direction(POSITIVE, "OUT"); // TODO: This should probably be reversed.

	/** Alias for POSITIVE */
	public static final Direction CLOCKWISE        = new Direction(POSITIVE, "CLOCKWISE");  
	/** Alias for NEGATIVE */
	public static final Direction COUNTERCLOCKWISE = new Direction(NEGATIVE, "COUNTERCLOCKWISE");

	/** Alias for POSITIVE */
	public static final Direction UP      = new Direction(POSITIVE, "UP");
	/** Alias for NEGATIVE */
	public static final Direction DOWN    = new Direction(NEGATIVE, "DOWN");
	
	/** Alias for POSITIVE */
	public static final Direction RIGHT   = new Direction(POSITIVE, "RIGHT");
	/** Alias for NEGATIVE */
	public static final Direction LEFT    = new Direction(NEGATIVE, "LEFT");
	
	/** Alias for POSITIVE */
	public static final Direction OPEN    = new Direction(POSITIVE, "OPEN");
	/** Alias for NEGATIVE */
	public static final Direction CLOSE   = new Direction(NEGATIVE, "CLOSE");

	/** Alias for NEUTRAL */
	public static final Direction CENTER  = new Direction(NEUTRAL, "CENTER");
	/** Alias for NEUTRAL */
	public static final Direction STOP    = new Direction(NEUTRAL, "STOP");
	/** Alias for NEUTRAL */
	public static final Direction OFF     = new Direction(NEUTRAL, "OFF");
	
	// The canonical form, or null if this is a canonical direction
	private final Direction canonicalDirection;
	// The multiplier for this direction
	private final int multiplier;
	// The label for this direction
	private final String label;
	
	/**
	 * Get the logical multiplier.  POSITIVE x1, NEGATIVE x-1, NEUTRAL x0
	 */
	public double getMultiplierAsDouble() {
		return multiplier;
	}
	/**
	 * Get the logical multiplier.  POSITIVE x1, NEGATIVE x-1, NEUTRAL x0
	 */
	public int getMultiplier() {
		return multiplier;
	}
	/**
	 * Is this a neutral direction
	 */
	public boolean isNeutral() {
		return canonicalDirection == NEUTRAL;
	}
	/**
	 * Is this a positive direction
	 */
	public boolean isPositive() {
		return canonicalDirection == FORWARD;
	}
	/**
	 * Is this a negative direction
	 */
	public boolean isNegative() {
		return canonicalDirection == REVERSE;
	}
	/**
	 * Get the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * Get the canonical form.  If this is a canonical direction, will return the same object.  Don't try to recurse.
	 */
	public Direction getCanonicalDirection() {
		return canonicalDirection;
	}
	/**
	 * Get the base label
	 */
	public String getCanonicalLabel() {
		return getCanonicalDirection().getLabel();
	}
	/**
	 * Create a new "canonical" direction.
	 * Do not make the constructor public.  Define new constants above.  Don't ever allocate new instances.
	 * @param multiplier The multiplier used to scale any directional values 
	 */
	private Direction(int multiplier, String label) {
		this.canonicalDirection = this;
		this.multiplier = multiplier;
		this.label = label;
	}	
	/**
	 * Create a new "alias" direction.
	 * Do not make the constructor public.  Define new constants above.  Don't ever allocate new instances.
	 * @param multiplier The multiplier used to scale any directional values 
	 */
	Direction(Direction canonicalDirection, String label) {
		this.canonicalDirection = canonicalDirection;
		this.multiplier = canonicalDirection.multiplier;
		this.label = label;
	}
	
	public String toString() {
		return label;
	}
	
	/**
	 * Two directions are equal if they are the same or have the same canonical representation
	 */
	public boolean equals(Direction other) {
		return other.getCanonicalDirection() == this.getCanonicalDirection();
	}

	/**
	 * Order by label
	 */
	@Override
	public int compareTo(Direction other) {
		return getLabel().compareTo(other.getLabel());
	}
}