/**
 * 
 */
package org.usfirst.frc2813.logging;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author Adrian Guerra
 *
 * NB: If you want to test code standalone that uses the logger, replace referneces to DriverStation.reportXXX with System.out.println
 *     It has to be commented, not switched.  If the DriverStation is loaded it will crash outside of RoboRIO.
 */
enum PrintLevel {
	DEFAULT {
		@Override
		void print(String content) {
			try {
				System.out.println(content);
			}
			catch(Throwable e) {
				// Don't do anything
			}
		}
	},
	WARNING {
		@Override
		void print(String content) {
			try {
				System.out.println(content);
			}
			catch(Throwable e) {
				// Don't do anything
			}
		}
	},
	ERROR {
		@Override
		void print(String content) {
			try {
				System.out.println(content);
			}
			catch(Throwable e) {
				// Don't do anything
			}
		}
	};
	abstract void print(String content);
}
