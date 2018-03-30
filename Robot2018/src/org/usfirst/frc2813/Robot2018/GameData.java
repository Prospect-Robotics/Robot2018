package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.util.Formatter;

/**
 * The game data from the driver station:
 * Which side of the near switch, scale, and far switch we have.
 * Data is passed as a single string with each piece as a 'L' or 'R'.
 */
public class GameData {
	private final Direction nearSwitchPosition;
	private final Direction scalePosition; 
	private final Direction farSwitchPosition;
	private final boolean   isGameDataValid;
	
	private static Direction splitChar(char c) {
		return (c == 'L') ? Direction.LEFT : Direction.RIGHT;
	}
	
	GameData(String gd) {
		if (gd.equals("LLL") || gd.equals("LRL") || gd.equals("RLR") || gd.equals("RRR")) {
			this.isGameDataValid = true;
			this.nearSwitchPosition = splitChar(gd.charAt(0));
			this.scalePosition = splitChar(gd.charAt(1));
			this.farSwitchPosition = splitChar(gd.charAt(2));
			System.out.println(this);
		}
		else {
			this.isGameDataValid = false;
			this.nearSwitchPosition = Direction.OFF;
			this.scalePosition = Direction.OFF;
			this.farSwitchPosition = Direction.OFF;
			Logger.print(LogType.ERROR, this, " received invalid GameData: ", gd);
		} 	
	}

	/**
	 * Is the game data valid?
	 * @return true if the gamedata is valid, false otherwise.
	 */
	public boolean isGameDataValid() {
		return isGameDataValid;
	}
	
	/**
	 * Get which side of the near switch belongs to our alliance.
	 */
	public Direction getNearSwitchPosition() { return nearSwitchPosition; }
	/**
	 * Get which side of scale belongs to our alliance.
	 */
	public Direction getScalePosition() { return scalePosition; }
	/**
	 * Get which side of the far switch belongs to our alliance.
	 */
	public Direction getFarSwitchPosition() { return farSwitchPosition; }
	
	public String toString() {
		if(isGameDataValid) {
			return Formatter.concat(
					"-----------------------------------\n",
					"GAME CONFIGURATION:\n",
					"    Near Switch....", 
					getNearSwitchPosition(), "\n",
					"    Scale..........",
					getScalePosition(), "\n",
					"    Far Switch.....",
					getFarSwitchPosition(), "\n",
					"-----------------------------------\n"
					);
		}
		return Formatter.concat(
				"<<<< INVALID >>>><<<< INVALID >>>> GAME CONFIGURATION <<<< INVALID >>>><<<< INVALID >>>>\n");
	}
}
