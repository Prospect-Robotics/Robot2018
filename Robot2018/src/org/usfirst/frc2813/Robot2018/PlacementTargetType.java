package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

public enum PlacementTargetType {
	/** Elevator height for placing cubes on the switch. */
	SWITCH(LengthUOM.Inches.create(3)),

	/** Elevator height for placing cubes on the scale. */
	SCALE(LengthUOM.Inches.create(76)),
	
	/** Elevator height for shooting cubes on the scale when robot backwards */
	SCALE_INVERTED(LengthUOM.Inches.create(76));
	
	public Length value;
	
	PlacementTargetType(Length height) {
		value = height;
	}
	
	public void moveAsync() {
		Robot.autonomousCommand.elevator.addMoveToPositionAsync(this.value);
	}
}