package org.usfirst.frc2813.Robot2018.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoLRLR extends CommandGroup {
	//LRLR: ROBOT on left, target is right side of SWITCH, target is on left side of SCALE
	//LRLR: Drive to LEFT side of SCALE passing LEFT side of SWITCH to place cube on SCALE

    public AutoLRLR() {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
}
