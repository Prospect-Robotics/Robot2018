package org.usfirst.frc2813.Robot2018.autonomous;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.post.POST;

import edu.wpi.first.wpilibj.command.Scheduler;

public class AutoThread implements Runnable {
	private static boolean is_active = false;
	private static double mUpdateRate = 1.0 / 50.0;
	public AutoThread() {
		Robot.autonomousCommand = new AutonomousCommandGroup();
		Robot.autoCmdGenerator = new AutonomousCommandGroupGenerator();
	}

	@Override
	public void run() {
		is_active = true;
		new POST(Robot.autonomousCommand).start();
		while (is_active) {
				autonomousPeriodic();
				long waitTime = (long) (mUpdateRate * 1000.0);
			try {
				Thread.sleep(waitTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	public static void stop() {
		is_active = false;
	}
}