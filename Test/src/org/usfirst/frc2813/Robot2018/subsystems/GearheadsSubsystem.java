package org.usfirst.frc2813.Robot2018.subsystems;

/*
 * This class exists as a stand in for GearheadsSubsystem that isn't derived
 * from WPI Subsystem, so it can be run without roboRIO.  Temporarily switch
 * your subsystem to this base class for testing.  Sadly, no preprocessor
 * so it's a manual process. 
 */
public class GearheadsSubsystem {
	protected boolean encoderFunctional = true;
	protected boolean emulated = true;

	private String name;
	public GearheadsSubsystem() {
		this.name = getClass().getSimpleName();
	}
	
	public String getName() { 
		return name; 
	}
	public void setName(String name) {  
		this.name = name; 
	}
	public boolean isEmulated() { return emulated; }
	
	/**
	 * Doesn't do anything
	 */
	public void disableEmulator() {
	}
	/**
	 * Doesn't do anything
	 */
	public void enableEmulator() {
	}
	/**
	 * Doesn't do anything
	 * @return true
	 */
	public boolean isRobotEnabled() {
		return true;
	}
	/**
	 * Doesn't do anything
	 */
	public void initDefaultCommand() {
	}
	/**
	 * Doesn't do anything
	 * @param defaultCommand ignored. 
	 */
//	public void setDefaultCommand(Command defaultCommand) {
		
//	}
	/**
	 * Doesn't do anything
	 */
	public void periodic() {
	}

	/**
	 * I just call periodic on something every 100ms
	 */
	public static class PeriodicTimerThread extends Thread {
		private static final int PERIODIC_INTERVAL = 250;
		private long lastRun = System.currentTimeMillis();
		private final GearheadsSubsystem target;
		PeriodicTimerThread(GearheadsSubsystem target) {
			this.target = target;
		}
		
		public void run() {
			while(true) {
				try {
					long now = System.currentTimeMillis();
					if((now - lastRun) >= PERIODIC_INTERVAL) {
						target.periodic();
						lastRun = now;
					}
					try {
						Thread.sleep(PERIODIC_INTERVAL/10);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				} catch(Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
	
	private PeriodicTimerThread notifier = null;
	
	public synchronized void startPeriodic() {
		if(notifier != null) {
			throw new IllegalStateException("Periodic timer is already running.");
		}
		this.notifier = new PeriodicTimerThread(this);
		notifier.start();
	}
	
	public synchronized void stopPeriodic() {
		if(notifier == null) {
			throw new IllegalStateException("Periodic timer is already terminated.");
		}
		notifier.interrupt();
		notifier = null;
	}
}
