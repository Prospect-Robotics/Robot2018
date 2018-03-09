// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Arm;

import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.subsystems.Arm;

/**
 * Open/Close the Robot arm jaws
 */
public class SetJaws extends InstantCommand {
	Direction state;

    public SetPosition(Direction state) {
        this.state = state;
    }

	//@Override
	protected void initialize() {
		Arm.setJaws(state);
	}
}
