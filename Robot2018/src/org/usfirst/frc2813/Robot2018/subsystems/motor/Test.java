package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.units.uom.LengthUOM;

/*
 * NB: This is a JUnit Test but junit classes aren't available on the target, causing the build to fail
 * So I removed the JUnit classes.  The function can still be called manually from somewhere as needed.
 */
class Test  {
//	@org.junit.jupiter.api.Test
	void test() {
//		ElevatorConfiguration.mathReport();
//		ArmConfiguration.mathReport();
//		Elevator.axisConfiguration.dumpDescription();
		Motor m = new Motor(new ElevatorConfiguration());
		m.moveToAbsolutePosition(LengthUOM.Inches.create(-5));
	}
}
