package org.usfirst.frc2813.Robot2018.motor;

public enum PID {
	Primary(0), Auxilliary(1);
	
	public final int pidIndex;
	
	private PID(int pidIndex) {
		this.pidIndex = pidIndex;
	}
	
	public int getPIDIndex() {
		return pidIndex;
	}
	
	public boolean equals(PID other) {
		return other.pidIndex == this.pidIndex;
	}
}
