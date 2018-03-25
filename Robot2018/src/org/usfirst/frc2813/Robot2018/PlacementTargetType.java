package org.usfirst.frc2813.Robot2018;

import org.usfirst.frc2813.Robot2018.autonomous.AutonomousCommandGroup;
import org.usfirst.frc2813.units.values.Length;

public enum PlacementTargetType {
	SWITCH(AutonomousCommandGroup.ELEVATOR_HEIGHT_FOR_SWITCH_CUBE_PLACEMENT),
	SCALE(AutonomousCommandGroup.ELEVATOR_HEIGHT_FOR_SCALE_CUBE_PLACEMENT),
	SCALE_INVERTED(AutonomousCommandGroup.ELEVATOR_HEIGHT_FOR_SCALE_CUBE_BACKWARD_PLACEMENT);
	
	public Length value;
	
	PlacementTargetType(Length height) {
		value = height;
	}
	
	public void moveAsync() {
		Robot.autonomousCommand.addElevatorMoveToPositionAsync(this.value);
	}
}