package org.usfirst.frc2813.Robot2018.commands.post;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder.BooleanConsumer;

/**
 *
 */
public class TestEncoder extends Command {

	private final SpeedController motor;
	private final Encoder encoder;
	private final BooleanConsumer callback;
	private int startingPosition;

	public TestEncoder(SpeedController motor, Encoder encoder, BooleanConsumer callback) {
		this.motor=motor;
		this.encoder=encoder;
		this.callback=callback;
		setTimeout(0.1);
		// this happens during POST.  Please don't interrupt it!
		setInterruptible(false);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		startingPosition = encoder.getRaw();
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
		motor.set(0.1); // 0.1 will at least cause the encoder to move.
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if(encoder.getRaw() != startingPosition) {
			if(callback != null)
				callback.accept(true);
			return true;
		}
		if(isTimedOut()) {
			if(callback != null)
				callback.accept(false);
			System.out.println("\n"
					+ "WARNING WARNING WARNING\n"
					+ "Encoder."+encoder.getName() + " in " + encoder.getSubsystem() + " is nonfunctional.\n"
					+ "WARNING WARNING WARNING\n");
			return true;
		}
		return false;
	}
}
