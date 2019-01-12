package org.usfirst.frc2813.Robot2018.commands;

import edu.wpi.first.wpilibj.Compressor;

/**
 * Compressor state toggle for debugging.
 */
public final class ToggleCompressor extends TargetedCommand<Compressor> {
	/**
	 * Turn the compressor on and off
	 * @param compressor the compressor to be toggled
	 */
    public ToggleCompressor(Compressor compressor) {
        super(compressor);
        setRunWhenDisabled(true);
        setName(toString());
    }

    // Called once when the command executes
    protected void ghcInitialize() {
    	if(getTarget().enabled()) {
    		action("initialize", "disabling the compressor.");
    		getTarget().stop();
    	} else {
    		action("initialize", "enabling the compressor.");
    		getTarget().start();
    		if(getTarget().getPressureSwitchValue()) {
    			action("initialize", "has enabled the compressor, but it will not start until tank pressure drops below 90PSI.");
    		}
    	}
    }

	@Override
	protected boolean ghcIsFinished() {
		return true;
	}
}
