package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.units.Direction;

/**
 * The game data from the driver station:
 * Which side of the near switch, scale, and far switch we have.
 * Data is passed as a single string with each piece as a 'L' or 'R'.
 */
public class GameData {
	private Direction nearSwitch, scale, farSwitch;
	public boolean isEnabled;
	private static Direction splitChar(char c) {
		return (c == 'L') ? Direction.LEFT : Direction.RIGHT;
	}
	GameData(String gd) {
		if (gd.equals("LLL") || gd.equals("LRL") || gd.equals("RLR") || gd.equals("RRR")) {
			isEnabled = true;
			nearSwitch = splitChar(gd.charAt(0));
			scale = splitChar(gd.charAt(1));
			farSwitch = splitChar(gd.charAt(2));
		}
		else {
			System.out.println("Invalid GameData: " + gd);
			isEnabled = false;
			nearSwitch = Direction.OFF;
			scale = Direction.OFF;
			farSwitch = Direction.OFF;
		} 	
	}
	public Direction getNearSwitch() { return nearSwitch; }
	public Direction getScale() { return scale; }
	public Direction getFarSwitch() { return farSwitch; }
}
