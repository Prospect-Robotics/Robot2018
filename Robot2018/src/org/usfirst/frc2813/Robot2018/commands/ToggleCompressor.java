package org.usfirst.frc2813.Robot2018.commands;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 * Compressor state toggle for debugging.
 */
public class ToggleCompressor extends GearheadsInstantCommand {
	private final Compressor compressor;
	/**
	 * Turn the compressor on and off
	 * @param compressor the compressor to be toggled
	 */
    public ToggleCompressor(Compressor compressor) {
        super();
        this.compressor=compressor;
        setRunWhenDisabled(true);
        setName(toString());
    }

    // Called once when the command executes
    protected void initialize() {
    	if(compressor.enabled()) {
    		System.out.println(this + " disabling the compressor.");
    		compressor.stop();
    	}else {
    		System.out.println(this + " enabling the compressor.");
    		compressor.start();
    		if(compressor.getPressureSwitchValue()) {
    			System.out.println(this + " has enabled the compressor, but it will not start until tank pressure drops below 90PSI.");
    		}
    	}
    }
    
    public String toString() {
    	return getClass().getSimpleName() + "(" + compressor + ")";
    }
}
