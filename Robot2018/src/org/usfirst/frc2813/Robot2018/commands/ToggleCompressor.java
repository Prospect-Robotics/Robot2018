package org.usfirst.frc2813.Robot2018.commands;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

/**
 *
 */
public class ToggleCompressor extends InstantCommand {
	private final Compressor compressor;
    public ToggleCompressor(Compressor c) {
        super();
        compressor=c;
        setRunWhenDisabled(true);
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called once when the command executes
    protected void initialize() {
    	if(compressor.enabled()) {
    		compressor.stop();
    		System.out.println("[ToggleCompressor] Stopping the compressor.");
    	}else {
    		compressor.start();
    		if(compressor.getPressureSwitchValue())
    			System.out.println("[ToggleCompressor] The compressor has been enabled but will not start until tank pressure drops below 90PSI.");
    		//else if(compressor.closed)
    	}
    }

}
