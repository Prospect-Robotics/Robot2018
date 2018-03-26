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
    protected void initializeImpl() {
    	if(getTarget().enabled()) {
    		System.out.println(this + " disabling the compressor.");
    		getTarget().stop();
    	} else {
    		System.out.println(this + " enabling the compressor.");
    		getTarget().start();
    		if(getTarget().getPressureSwitchValue()) {
    			System.out.println(this + " has enabled the compressor, but it will not start until tank pressure drops below 90PSI.");
    		}
    	}
    }

	@Override
	protected boolean isFinishedImpl() {
		return true;
	}
}
