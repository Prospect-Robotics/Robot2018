package org.usfirst.frc2813.Robot2018.motor;

public enum TalonPID {
	Primary(0), Auxilliary(1);
	
	public final int pidIndex;
	
	private TalonPID(int pidIndex) {
		this.pidIndex = pidIndex;
	}
	
	public int getPIDIndex() {
		return pidIndex;
	}
	
	public boolean equals(TalonPID other) {
		return other.pidIndex == this.pidIndex;
	}
}
