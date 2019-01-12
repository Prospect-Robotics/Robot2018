package org.usfirst.frc2813.Robot2018.motor;

public enum MotorInversion {
	Normal(false), Inverted(true);
	
	public final boolean isInverted;
	
	MotorInversion(boolean isInverted) {
		this.isInverted = isInverted;	
	}
	
	public boolean isInverted() {
		return isInverted;
	}
	
	public int getMultiplier() {
		return isInverted ? -1 : 1;
	}
}
