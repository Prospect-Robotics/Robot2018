package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.Talon;
import org.usfirst.frc2813.Robot2018.commands.Arm.MoveArm;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 * Arm subsystem
 *
 * Arm rotates through 24 degrees.
 * Arm has state halted, speed and direction.
 * Arm can be moving up or down or be halted.
 *
 * Intake has state halted and direction.
 * Intake can be moving in, out or halted.
 *
 * Jaws can open and close.
 * Jaws have state tracking this. - FIXME: can jaws open if closed too long? Should we care?
 */
public abstract class GearheadsSubsystem extends Subsystem {
	
	/*
	 * Debugging helper method to return string representation of enam for Direction.
	 */
	public static String getDirectionLabel(Direction direction) {
		switch(direction) {
		case IN: return "IN";
		case OUT: return "OUT";
		case LEFT: return "LEFT";
		case RIGHT: return "RIGHT";
		case UP: return "UP";
		case DOWN: return "DOWN";
		case BACKWARD: return "BACKWARD";
		case FORWARD: return "FORWARD";
		case OPEN: return "OPEN";
		case CLOSE: return "CLOSE";
		case CENTER: return "CENTER";
		default:
			throw new IllegalArgumentException("Unknown value for direction: " + direction);
		}
	}
}
