// RobotBuilder Version: 2.0

// FIXME! This is a command from the intake subsystem, but is in the arm package. Move it!
package org.usfirst.frc2813.Robot2018.commands.Arm;

import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.subsystems.Arm;

/**
 * Open the Robot arm jaws
 */
public class OpenJaws extends InstantCommand {
	//@Override
	protected void initialize() {
		Arm.openJaws();
	}
}
