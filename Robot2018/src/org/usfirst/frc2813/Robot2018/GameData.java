package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.units.Direction;

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
			System.out.println(this + " received invalid GameData: " + gd);
		} 	
	}

	/**
	 * Is the game data valid?
	 * @return true if the gamedata is valid, false otherwise.
	 */
	public boolean isGameDataValid() {
		return isGameDataValid;
	}
	
	/*
	 * Get which side of the near switch belongs to our alliance.
	 */
	public Direction getNearSwitchPosition() { return nearSwitchPosition; }
	/*
	 * Get which side of scale belongs to our alliance.
	 */
	public Direction getScalePosition() { return scalePosition; }
	/*
	 * Get which side of the far switch belongs to our alliance.
	 */
	public Direction getFarSwitchPosition() { return farSwitchPosition; }
	/*
	 * Get which side of the specified target belongs to our alliance.
	 */
	public Direction getTargetPosition(PlacementTarget target) {
		switch(target) {
		case FAR_SWITCH:
			return getFarSwitchPosition();
		case NEAR_SWITCH:
			return getNearSwitchPosition();
		case SCALE:
			return getScalePosition();
		default:
			throw new IllegalArgumentException("Got unsupported target: " + target);
		}
	}
	
	public String toString() {
		if(isGameDataValid) {
			return getClass().getSimpleName() + " [NearSwitch=" + getNearSwitchPosition() + ", Scale=" + getScalePosition() + ", " + getFarSwitchPosition() + "]"; 
			
		} else {
			return getClass().getSimpleName() + " [Invalid]"; 
		}
	}
}
