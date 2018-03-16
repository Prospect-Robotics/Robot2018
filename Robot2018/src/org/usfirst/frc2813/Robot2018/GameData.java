package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.units.Direction;

/**
 * The game data from the driver station: Which side of the near switch, scale,
 * and far switch we have. Data is passed as a single string with each piece as
 * a {@code 'L'} or {@code 'R'}.
 */
public class GameData {
	private Direction nearSwitch, scale, farSwitch;
	public boolean isEnabled;

	/**
	 * 
	 * @param c
	 *            - Character to be converted
	 * @return
	 *         <ul>
	 *         <li>{@link Direcition#LEFT} if character is {@code 'L'}
	 *         <li>{@link Direcition#RIGHT} if character is {@code 'R'}
	 *         <li>{@link Direcition#RIGHT} if character is neither
	 *         </ul>
	 * @see Direction
	 */
	private static Direction splitChar(char c) {
		return (c == 'L') ? Direction.LEFT : Direction.RIGHT;
	}

	/**
	 * @param gd
	 *            - String for field layout ({@code "LRL"}, {@code "RLR"}, etc.)
	 * @see GameData#splitChar(char)
	 */
	GameData(String gd) {
		if (gd.equals("LLL") || gd.equals("LRL") || gd.equals("RLR") || gd.equals("RRR")) {
			isEnabled = true;
			nearSwitch = splitChar(gd.charAt(0));
			scale = splitChar(gd.charAt(1));
			farSwitch = splitChar(gd.charAt(2));
		} else {
			System.out.println("Invalid GameData: " + gd);
			isEnabled = false;
			nearSwitch = Direction.OFF;
			scale = Direction.OFF;
			farSwitch = Direction.OFF;
		}
	}

	/**
	 * 
	 * @return Value of the near switch as a Direction
	 * 
	 * @see Direction
	 * @see GameData#getScale()
	 * @see GameData#getFarSwitch()
	 */
	public Direction getNearSwitch() {
		return nearSwitch;
	}

	/**
	 * @return Value of the scale as a Direction
	 * 
	 * @see Direction
	 * @see GameData#getNearSwitch()
	 * @see GameData#getFarSwitch()
	 */
	public Direction getScale() {
		return scale;
	}

	/**
	 * @return Value of the far switch as a Direction
	 * 
	 * @see Direction
	 * @see GameData#getNearSwitch()
	 * @see GameData#getScale()
	 */
	public Direction getFarSwitch() {
		return farSwitch;
	}
}
