package org.usfirst.frc2813.util;

import org.usfirst.frc2813.units.Direction;

/**
 * Utility class with static angular math helpers.
 */
public class GeometryHelpers {
	/**
	 * Calculate the relative offset of an angle in the same way our gyro does given the direction
	 * and rotation of travel
	 * @param angle - positive degrees
	 * @param direction - forward or backward
	 * @param rotation - clockwise or counter clockwise
	 * @return signed angle we should arrive after after such an operation
	 */
	public static double computeRelativeAngle(double angle, Direction direction, Direction rotation) {
		double retVal = angle;
		if (!(rotation == Direction.CLOCKWISE)) {
			retVal *= -1.0;
		}
		if (direction.isNegative()) {
			retVal *= -1.0;
		}
		return retVal;
	}

	/**
	 * Compute the distance traveled over an arc section given angle in degrees and radius
	 * @param angle
	 * @param radius
	 * @return
	 */
    public static double computeArcLength(double angle, double radius) {
        return radius*(2*Math.PI*angle/360);
    }

    /**
     * Perform linear interpolation of x on (x1,y1)->(x2,y2)
     */
	public static double interpolate(double x1, double y1, double x2, double y2, double x) {
		return y1 + (y2 - y1) / (x2 - x1) * (x - x1);
	}
}
