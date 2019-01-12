package org.usfirst.frc2813.Robot2018.motor;

public enum SensorPhase {
	Normal(false), Reversed(true);
	
	public final boolean isReversed;
	
	SensorPhase(boolean isReversed) {
		this.isReversed = isReversed;	
	}
	
	public boolean isReversed() {
		return isReversed;
	}
	
	public int getMultiplier() {
		return isReversed ? -1 : 1;
	}
}
