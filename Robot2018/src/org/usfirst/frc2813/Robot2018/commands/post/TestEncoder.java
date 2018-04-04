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
		motor.set(0.15); // 0.1 will at least cause the encoder to move.
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
	}
	
	@Override
	protected void end() {
		motor.set(0);
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		if(Math.abs(encoder.getRaw() - startingPosition) >= 3) {
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
