package org.usfirst.frc2813.units.uom;

import org.usfirst.frc2813.Robot2018.subsystems.ElevatorAxisConfiguration;

/*
 * NB: This is a JUnit Test but junit classes aren't available on the target, causing the build to fail
 * So I removed the JUnit classes.  The function can still be called manually from somewhere as needed.
 */
class TestUOM {

	void test() {
		try
		{
			LengthUOM x = ElevatorAxisConfiguration.ElevatorSRXMotorRevolution; // Make sure Elevator's units are included.
			UOM.dumpUnitsOfMeasure();
		} catch(Throwable t) {
			t.printStackTrace();
//			fail("Unexpected exception");
		}
	}

}
