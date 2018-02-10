package org.usfirst.frc2813.Robot2018.commands.post;

import java.util.Optional;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.BottomElevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class POST extends CommandGroup {

	private static boolean POSTinitiated = false;
	private final Optional<Command> afterPOST;

	public POST() {
		this(null);
	}

	public POST(Command runAfterPOST) {
		afterPOST = Optional.ofNullable(runAfterPOST);

		addSequential(new TestEncoder(Robot.driveTrain.speedController1, Robot.driveTrain.quadratureEncoder1,
				(boolean functional) -> {
					Robot.driveTrain.encoder1Functional = functional;
				}, Robot.driveTrain));
		addSequential(new TestEncoder(Robot.driveTrain.speedController2, Robot.driveTrain.quadratureEncoder2,
				(boolean functional) -> {
					Robot.driveTrain.encoder2Functional = functional;
				}, null));

	}

	@Override
	public void end() {
		super.end();
		afterPOST.ifPresent(Command::start);
	}

	public static void beginPOST() {

		new TestEncoder(Robot.driveTrain.speedController2, Robot.driveTrain.quadratureEncoder2,
				(boolean functional) -> {
					Robot.driveTrain.encoder2Functional = functional;
				}, null) // must provide null subsystem to this one; trying to start a command that
							// requires a subsystem already in use a non-interruptible command, rather than
							// waiting for it to finish, will simply silently fail. Thanks WPI.
						.start();

		// when they add an encoder on the elevator, add another TestEncoder here.

		// uncomment when they add the limit switch!
		// new BottomElevator().start();
	}

	@Override
	public void start() {
		// ensure POST only runs once per reboot!
		if (POSTinitiated)
			afterPOST.ifPresent(Command::start);
		else {
			POSTinitiated = true;
			super.start();
		}

	}
}
