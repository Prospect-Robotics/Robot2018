package org.usfirst.frc2813.units.uom;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.usfirst.frc2813.Robot2018.subsystems.Elevator;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Value;

class TestUOM {

	@Test
	void test() {
		try
		{
			LengthUOM x = Elevator.ElevatorSRXMotorRevolution; // Make sure Elevator's units are included.
			UOM.dumpUnitsOfMeasure();
		} catch(Throwable t) {
			t.printStackTrace();
			fail("Unexpected exception");
		}
//		fail("Not yet implemented");
	}

}
