package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

/**
 * Reset the encoders
 */
public class DriveTrainResetEncoders extends SubsystemCommand<DriveTrain> {

	/**
	 * Reset the DriveTrain encoders
	 * Whatever is already running may be using the encoders... so require the subsystem.
	 * @param driveTrain The driveTrain subsystem to reset.
	 */
    public DriveTrainResetEncoders(DriveTrain driveTrain) {
        super(driveTrain, CommandDuration.DISABLED, Lockout.Disabled);
    }

    // Called once when the command executes
    @Override
    protected void subsystemInitializeImpl() {
    	trace("initialize", "resetting drive train encoders.");
    	subsystem.getEncoderStarboard().reset();
    	subsystem.getEncoderPort().reset();
    }

	@Override
	public boolean isSubsystemRequired() {
		return true; // stop while we are resetting, so we don't screw up
	}

	@Override
	protected boolean subsystemIsFinishedImpl() {
		return true;
	}
}
