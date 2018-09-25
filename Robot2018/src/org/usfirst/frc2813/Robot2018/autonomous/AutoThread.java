package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.post.POST;

public class AutoThread implements Runnable {
	private static boolean is_active = false;
	public AutoThread() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while (is_active) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		autonomousPeriodic();
	}
	
	public void autonomousPeriodic() {
		Robot.autonomousCommand = new AutonomousCommandGroup();
		Robot.autoCmdGenerator = new AutonomousCommandGroupGenerator();
		new POST(Robot.autonomousCommand).start();
		is_active = true;
	}

	public static void stop() {
		is_active = false;
	}
}
