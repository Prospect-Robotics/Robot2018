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
			System.out.println(content);
		}
	},
	WARNING {
		@Override
		void print(String content) {
			try {
				DriverStation.reportWarning(content,false);
			} catch(Throwable t) {
				System.out.println(content);
			}
		}
	},
	ERROR {
		@Override
		void print(String content) {
			try {
				DriverStation.reportError(content,false);
			} catch(Throwable t) {
				System.out.println(content);
			}
		}
	};
	abstract void print(String content);
}
