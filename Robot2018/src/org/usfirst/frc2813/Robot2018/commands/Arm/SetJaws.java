// RobotBuilder Version: 2.0

package org.usfirst.frc2813.Robot2018.commands.Arm;

import edu.wpi.first.wpilibj.command.InstantCommand;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.util.unit.Direction;

/**
 * Open/Close the Robot arm jaws
 */
public class SetJaws extends InstantCommand {
	Direction state;

    public SetJaws(Direction state) {
        this.state = state;
    }

	//@Override
	protected void initialize() {
		Robot.jaws.setState(state);
	}
}
