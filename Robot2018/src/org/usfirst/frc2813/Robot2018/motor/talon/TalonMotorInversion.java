package org.usfirst.frc2813.Robot2018.motor.talon;

public enum TalonMotorInversion {
	Normal(false), Inverted(true);
	
	public final boolean isInverted;
	
	TalonMotorInversion(boolean isInverted) {
		this.isInverted = isInverted;	
	}
	
	public boolean isInverted() {
		return isInverted;
	}
	
	public int getMultiplier() {
		return isInverted ? -1 : 1;
	}
}
