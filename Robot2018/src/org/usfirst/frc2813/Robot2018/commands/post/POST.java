package org.usfirst.frc2813.Robot2018.commands.post;

import java.util.Optional;

import org.usfirst.frc2813.Robot2018.Robot;

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

	/**
	 * FIXME! POST all of our subsystems, not just drivetrain
	 * @param runAfterPOST
	 */
	public POST(Command runAfterPOST) {
		afterPOST = Optional.ofNullable(runAfterPOST);

		setInterruptible(false); // you really shouldn't be able to interrupt POST.

		addSequential(new TestEncoder(Robot.driveTrain.getSpeedControllerPort(), Robot.driveTrain.getEncoderPort(),
				(boolean functional) -> {
					Robot.driveTrain.encoderPortFunctional = functional;
				}));
		addSequential(new TestEncoder(Robot.driveTrain.getSpeedControllerStarboard(), Robot.driveTrain.getEncoderStarboard(),
				(boolean functional) -> {
					Robot.driveTrain.encoderStarboardFunctional = functional;
				}));
		requires(Robot.driveTrain);

		/*addSequential(new TestEncoder(Robot.arm.speedController, Robot.arm.encoder, (boolean functional) -> {
			Robot.arm.encoderFunctional = functional;
		}));
		requires(Robot.arm);

		addSequential(new TestEncoder(Robot.elevator.speedController, Robot.elevator.encoder, (boolean functional) -> {
			Robot.elevator.encoderFunctional = functional;
		}));
		requires(Robot.elevator);*/

		// add bottomElevator here when they add a limit switch on the elevator.

		//addSequential(new WaitForChildren());
	}

	@Override
	public void end() {
		super.end();
		System.out.println("POST complete!");
		try {
			Thread.sleep(125); // should be enough to let the robot come to a complete stop (I mean we did move
								// the drive train)
		} catch (InterruptedException e) {
			// we aren't prepared to deal with interrupts.
			Thread.currentThread().interrupt();
		}
		afterPOST.ifPresent(Command::start);
	}

	@Override
	public void start() {
		// ensure POST only runs once per reboot!
		if (POSTinitiated)
			// don't run POST at all, it already ran. Just start the command.
			afterPOST.ifPresent(Command::start);
		else {
			POSTinitiated = true;
			System.out.println("Starting POST.");
			// afterPOST will be called after POST completion.
			super.start();
		}

	}
}
