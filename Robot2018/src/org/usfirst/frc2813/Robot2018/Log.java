package org.usfirst.frc2813.Robot2018;

/**
 * Very simple logger. Takes a name and saves you passing the name each time.
 */
// try to get an interface working...
//public interface Log {
//	static String label;
//	public default void print(String s) {
//		System.out.println(this.getClass().toString() + ": " + s);
//	}
//}
public class Log {
	private String label;
	public Log(String name) {
		label = name + ": ";
	}
	public void print(String s) {
		System.out.println(label + s);
	}
}