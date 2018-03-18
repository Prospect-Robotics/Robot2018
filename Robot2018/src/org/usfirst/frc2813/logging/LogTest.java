/**
 * 
 */
package org.usfirst.frc2813.logging;

/**
 * @author Adrian Guerra
 *
 */
public class LogTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.printFormat(LogType.ALWAYS,"%1$s hello %1$s world",1,2);
	}

}
