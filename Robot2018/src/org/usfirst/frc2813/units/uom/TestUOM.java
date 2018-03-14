package org.usfirst.frc2813.units.uom;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.values.Value;

class TestUOM {

	@Test
	void test() {
		try
		{
			UOM x = LengthUOM.CanonicalUOM;
			x = RateUOM.CanonicalUOM;
			x = TimeUOM.CanonicalUOM;
			UOM.dumpUnitsOfMeasure();
			System.out.println();
			System.out.println("TESTING:");
			System.out.println();
			java.util.Iterator<SystemOfMeasurement> allSystems = UOM.allUnits.keySet().iterator();
			while(allSystems.hasNext()) {
				SystemOfMeasurement system = allSystems.next();
				System.out.println("[" + system + "]");
				Iterator<UOM> units = UOM.allUnits.get(system).iterator();
				while(units.hasNext()) {
					UOM unitOfMeasure = units.next(); 
					Value cu = unitOfMeasure.getCanonicalValue();
					Value ou = cu.convertTo(unitOfMeasure);
					System.out.println(unitOfMeasure + " = " + cu + " = " + ou);
					if(!cu.equals(ou)) {
						fail("Canonical Units Converted Back To Original Units Were Not Equal");
					}
				}
				System.out.println();
			}
		} catch(Throwable t) {
			t.printStackTrace();
			fail("Unexpected exception");
		}
//		fail("Not yet implemented");
	}

}
