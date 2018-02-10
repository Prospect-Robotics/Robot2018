package org.usfirst.frc2813.Robot2018.commands.post;

import java.util.Optional;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.BottomElevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitForChildren;

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
	
		setInterruptible(false); // you really shouldn't be able to interrupt POST.

		addParallel(new TestEncoder(Robot.driveTrain.speedController1, Robot.driveTrain.quadratureEncoder1,
				(boolean functional) -> {
					Robot.driveTrain.encoder1Functional = functional;
				}));
		addParallel(new TestEncoder(Robot.driveTrain.speedController2, Robot.driveTrain.quadratureEncoder2,
				(boolean functional) -> {
					Robot.driveTrain.encoder2Functional = functional;
				}));
		requires(Robot.driveTrain);
		
		// add elevator testEncoder here when they add an encoder on the elevator / we have more time
		
		// add bottomElevator here when they add a limit switch on the elevator.
		
		
		addSequential(new WaitForChildren());
	}

	@Override
	public void end() {
		super.end();
		System.out.println("POST complete!");
		try {
			Thread.sleep(125); // should be enough to let the robot come to a complete stop (I mean we did move the drive train)
		} catch(InterruptedException e) {
			// we aren't prepared to deal with interrupts.
			Thread.currentThread().interrupt();
		}
		afterPOST.ifPresent(Command::start);
	}

	
	@Override
	public void start() {
		// ensure POST only runs once per reboot!
		if (POSTinitiated)
			// don't run POST at all, it already ran.  Just start the command.
			afterPOST.ifPresent(Command::start);
		else {
			POSTinitiated = true;
			// afterPOST will be called after POST completion.
			super.start();
		}

	}
}
