package org.usfirst.frc2813.Robot2018.motor;

public enum TalonSensorPhase {
	Normal(false), Reversed(true);
	
	public final boolean isReversed;
	
	TalonSensorPhase(boolean isReversed) {
		this.isReversed = isReversed;	
	}
	
	public boolean isReversed() {
		return isReversed;
	}
	
	public int getMultiplier() {
		return isReversed ? -1 : 1;
	}
}
