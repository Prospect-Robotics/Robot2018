package org.usfirst.frc2813.Robot2018.motor;

public class PIDConfiguration {

	private final PIDProfileSlot pidProfileSlot;
	private final double p;
	private final double i;
	private final double d;
	private final double f;
	
	public PIDConfiguration(PIDProfileSlot pidProfileSlot, double p, double i, double d, double f) {
		this.pidProfileSlot = pidProfileSlot;
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
	}
	
	public PIDProfileSlot getPIDProfileSlot() {
		return pidProfileSlot;
	}
	public double getP() {
		return p;
	}
	public double getI() {
		return i;
	}
	public double getD() {
		return d;
	}
	public double getF() {
		return f;
	}
	public String toString() {
		return "PIDConfiguration[Idx=" + pidProfileSlot + " P=" + p + " I=" + i + " D=" + d + " F=" + f + "]";
	}
}
