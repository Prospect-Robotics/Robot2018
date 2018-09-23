package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.post.POST;

public class AutoThread implements Runnable {

	public AutoThread() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			Robot.autonomousCommand = new AutonomousCommandGroup();
			Robot.autoCmdGenerator = new AutonomousCommandGroupGenerator();
			new POST(Robot.autonomousCommand).start();
			Thread.sleep(5);
		} catch(Exception e) {
			
		}
	}

}
