package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorState;
import org.usfirst.frc2813.logging.LogLevel;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;

/*
 * NB: This is a JUnit Test but junit classes aren't available on the target, causing the build to fail
 * So I removed the JUnit classes.  The function can still be called manually from somewhere as needed.
 * 
 * TO TEST MOTOR STANDALONE:
 * 
 * 1.  Make the changes specified in the comments for org.usfirst.frc2813.Robot2018.subsystems.motor.Motor
 * 2.  Uncomment @org.junit.jupiter.api.Test on test() method in this file.
 * 3.  Run or debug with JUnit (right click it and run as should have junit).  If not, try adding a JUnit
 *     with the new object wizard in the package explorer and then deleting the generated class.  That
 *     will make sure JUnit is available in your classpath.
 */
class Test  {
	static final int PERIODIC_INTERVAL = 250;
	
	interface Periodic {
		void periodic();
	}

	/**
	 * I just call periodic on something every 100ms
	 */
	class TimerThread extends Thread {
		private long lastRun = System.currentTimeMillis();
		private final Periodic target;
		TimerThread(Periodic target) {
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

	private static IMotorState lastAnnounced = null;
	private static Length lastSeen = null;
	void waitForCompletion(Motor m) {
		long startTime = System.currentTimeMillis();
		boolean failed = false;
		boolean complete = false;
		while(!failed && !complete) {
			if(lastAnnounced != m.getTargetState()) {
				lastAnnounced = m.getTargetState();
				Logger.info(" ");
				Logger.info(" ");
				Logger.info("-----------------------------------------------------------");
				Logger.info("New Objective: " + lastAnnounced);
				Logger.info("-----------------------------------------------------------");
			}
			Logger.info(m.getDiagnostics());
			boolean hitHardLimit = false;
			boolean hitSoftLimit = false;
			if(m.getTargetState().isCalibratingSensorInDirection() && m.getCurrentHardLimitSwitchStatus(m.getTargetDirection())) {
				complete = true;
				continue;
			}
			if(m.getTargetDirection() != null) {
				hitHardLimit = m.getCurrentHardLimitSwitchStatus(m.getTargetDirection());
				hitSoftLimit = m.getCurrentSoftLimitSwitchStatus(m.getTargetDirection());
			} else {
				hitHardLimit = m.getCurrentHardLimitSwitchStatus(Direction.FORWARD) || m.getCurrentHardLimitSwitchStatus(Direction.REVERSE);
				hitSoftLimit = m.getCurrentSoftLimitSwitchStatus(Direction.FORWARD) || m.getCurrentSoftLimitSwitchStatus(Direction.REVERSE);
			}
			if(m.getTargetDirection() != null && (hitHardLimit || hitSoftLimit)) {
				if(hitHardLimit) {
					Logger.warning("MOVE HIT HARD LIMIT SWITCH.");
				}
				if(hitSoftLimit) {
					Logger.warning("MOVE HIT SOFT LIMIT SWITCH.");
				}
				failed = true;
				continue;
			} else if(lastSeen != null && (hitHardLimit || hitSoftLimit)) {
				/*
				 * We're running PID but we stopped moving and a limit is active... tell tale sign
				 */
				int currentEncoderValue = m.getCurrentPosition().convertTo(m.getConfiguration().getNativeSensorLengthUOM()).getValueAsInt();
				int lastEncoderValue    = lastSeen.convertTo(m.getConfiguration().getNativeSensorLengthUOM()).getValueAsInt();
				if(currentEncoderValue == lastEncoderValue) {
					if(hitHardLimit) {
						Logger.warning("PID HIT HARD LIMIT SWITCH.");
					}
					if(hitSoftLimit) {
						Logger.warning("PID HIT SOFT LIMIT SWITCH.");
					}
					failed = true;
					continue;
				}
			}
			lastSeen = m.getCurrentPosition();
			if(m.getTargetState().isMovingToPosition() && m.getCurrentPositionErrorWithin(LengthUOM.Inches.create(0.001))) {
				complete = true;
				continue;
			}
			try {
				Thread.sleep(PERIODIC_INTERVAL);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			Thread.yield();
		}
		Logger.info("-----------------------------------------------------------");
		Logger.info("Objective " + (failed ? "INCOMPLETE" : "COMPLETE") +" : " + lastAnnounced);
		Logger.info("Position: " + m.getCurrentPosition());
		Logger.info(" Elapsed: " + TimeUOM.Milliseconds.create(System.currentTimeMillis() - startTime).convertTo(TimeUOM.Seconds));
		Logger.info("-----------------------------------------------------------");
		Logger.info(" ");
		Logger.info(" ");
	}

//	@org.junit.jupiter.api.Test
	void test() {
		try {
//		ElevatorConfiguration.mathReport();
//		ArmConfiguration.mathReport();
//		Elevator.axisConfiguration.dumpDescription();
		Motor m = new Motor(new ElevatorConfiguration());
		Thread p = new TimerThread(new Periodic() { public void periodic() { m.periodic(); }});
		p.start();
		Thread.yield();
//		Logger.setLoggingLevel(LogLevel.ERROR);
		m.moveToAbsolutePosition(LengthUOM.Inches.create(10));
		waitForCompletion(m);
		m.moveToAbsolutePosition(LengthUOM.Inches.create(30));
		waitForCompletion(m);
		m.moveToAbsolutePosition(LengthUOM.Inches.create(5));
		waitForCompletion(m);
		m.moveToRelativePosition(Direction.FORWARD, LengthUOM.Inches.create(5));
		waitForCompletion(m);
		m.moveToRelativePosition(Direction.REVERSE, LengthUOM.Inches.create(5));
		waitForCompletion(m);
		m.moveToAbsolutePosition(LengthUOM.Inches.create(-1));
		waitForCompletion(m);
		m.moveToAbsolutePosition(LengthUOM.Inches.create(1000));
		waitForCompletion(m);
		m.calibrateSensorInDirection(Direction.REVERSE);
		waitForCompletion(m);
		m.moveInDirectionAtRate(Direction.FORWARD, RateUOM.FeetPerSecond.create(6));
		waitForCompletion(m);
//		Logger.setLoggingLevel(LogLevel.DEBUG);
		m.moveInDirectionAtDefaultRate(Direction.REVERSE);
		waitForCompletion(m);
//		Logger.setLoggingLevel(LogLevel.INFO);
		Logger.info("-----------------------------------------------------------");
		Logger.info("ALL DONE.");
		Logger.info("-----------------------------------------------------------");
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
}
