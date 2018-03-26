package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

/**
 * Reset the Gyro counter.
 */
public final class DriveTrainResetGyro extends SubsystemCommand<DriveTrain> {

	/*
	 * Reset the Gyro
	 * Whatever is already running may be using the gyro... so require the subsystem.
	 */
    public DriveTrainResetGyro(DriveTrain driveTrain) {
    	super(driveTrain, CommandDuration.DISABLED, Lockout.Disabled);
    	setName(toString());
    }

    /**
     * Reset the gyro
     */
    @Override
    protected void subsystemInitializeImpl() {
    	super.initialize();
    	trace("initialize", "resetting drive train gyro.");
    	subsystem.getGyro().reset();
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
