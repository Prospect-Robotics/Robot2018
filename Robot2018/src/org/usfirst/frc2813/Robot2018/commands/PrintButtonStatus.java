package org.usfirst.frc2813.Robot2018.commands;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class PrintButtonStatus extends InstantCommand {
	private boolean pressedReleased;
	private boolean upDown;
    public PrintButtonStatus(boolean pressedReleased, boolean upDown) {
        super();
        this.pressedReleased=pressedReleased;
        this.upDown=upDown;
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	System.out.printf("Button Status: %5b; Pressed?: %5b\n", upDown, pressedReleased);
    }

}
