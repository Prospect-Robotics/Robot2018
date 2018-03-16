package org.usfirst.frc2813.Robot2018.motor;

public class PIDConfiguration {

	private final String name;
	private final int profileIndex;
	private final double p;
	private final double i;
	private final double d;
	private final double f;
	
	public PIDConfiguration(String name, int profileIndex, double p, double i, double d, double f) {
		this.name = name;
		this.profileIndex = profileIndex;
		this.p = p;
		this.i = i;
		this.d = d;
		this.f = f;
	}
	
	public String getName() {
		return name;
	}
	public int getProfileIndex() {
		return profileIndex;
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
		return "PIDConfiguration[" + name + " Idx=" + profileIndex + " P=" + p + " I=" + i + " D=" + d + " F=" + f + "]";
	}
}
