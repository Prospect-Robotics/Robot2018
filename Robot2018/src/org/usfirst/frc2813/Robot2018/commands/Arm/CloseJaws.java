// RobotBuilder Version: 2.0
package org.usfirst.frc2813.Robot2018.commands.Arm;

import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.subsystems.Arm;

/**
 * Close the Robot arm jaws
 */
public class CloseJaws extends InstantCommand {
	//@Override
	protected void initialize() {
		Arm.closeJaws();
	}
}
