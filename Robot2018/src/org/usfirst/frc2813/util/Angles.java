package org.usfirst.frc2813.util;

import org.usfirst.frc2813.units.Direction;

public class Angles {
	public static double getRelativeAngle(double angle, Direction direction, Direction rotation) {
		double retVal = angle;
		if (!(rotation == Direction.CLOCKWISE)) {
			retVal *= -1.0;
		}
		if (direction.isNegative()) {
			retVal *= -1.0;
		}
		return retVal;
	}
}
