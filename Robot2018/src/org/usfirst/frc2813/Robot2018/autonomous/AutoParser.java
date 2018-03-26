package org.usfirst.frc2813.Robot2018.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoParser extends CommandGroup {
	
    public AutoParser() {
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
    
    public AutoParser(File f) throws FileNotFoundException {
    	this();
    	parse(f);
    }

	public void parse(File f) throws FileNotFoundException {
		try(Scanner s = new Scanner(f)) {
			parse(s);
		}
	}

	private void parse(Scanner s) {
		while(s.hasNext()) {
			
		}
	}
}
