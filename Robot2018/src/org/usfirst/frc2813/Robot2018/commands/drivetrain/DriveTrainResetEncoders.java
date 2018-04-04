package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
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
        super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
    }

    // Called once when the command executes
    @Override
    protected void ghscInitialize() {
    	action("initialize", "resetting drive train encoders.");
    	subsystem.getEncoderRight().reset();
    	subsystem.getEncoderLeft().reset();
    }

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true; // stop while we are resetting, so we don't screw up
	}

	@Override
	protected boolean ghscIsFinished() {
		return true;
	}
}
