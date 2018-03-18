/**
 * 
 */
package org.usfirst.frc2813.logging;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * @author Adrian Guerra
 *
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
			DriverStation.reportWarning(content,false);
		}
	},
	ERROR {
		@Override
		void print(String content) {
			DriverStation.reportError(content,false);
		}
	};
	abstract void print(String content);
}
