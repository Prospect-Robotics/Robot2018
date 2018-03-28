package org.usfirst.frc2813.Robot2018.motor.test;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerUnitConversionAdapter;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.simulated.Simulated;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ArmConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.ElevatorConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Time;

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

//	@org.junit.jupiter.api.Test
	public void test() {
		runTest();
	}
	
	void waitForCompletion(IMotor motor) {
		MotorCommandMonitor.waitForCompletion(motor, true);
	}

	/**
	 * I just call periodic on something every 100ms
	 */
	public static class PeriodicTimerThread extends Thread {
		private static final int PERIODIC_INTERVAL = 250;
		private long lastRun = System.currentTimeMillis();
		private final IMotor target;
		PeriodicTimerThread(IMotor target) {
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

	void runTest() {
		try {
			IMotor arm;
			IMotor elevator;
			if(Motor.class.getSuperclass().equals(DummySubsystemForTesting.class)) {
				// Standalone Mode
				Simulated armSim = new Simulated(new ArmConfiguration());
				Simulated elevatorSim = new Simulated(new ElevatorConfiguration());
				arm = new MotorControllerUnitConversionAdapter(armSim.getConfiguration(), armSim);
				elevator = new MotorControllerUnitConversionAdapter(elevatorSim.getConfiguration(), elevatorSim);
				// Start Monitoring
				new MotorMonitorThread(elevator).start();
				new MotorMonitorThread(arm).start();
				((DummySubsystemForTesting)(Object)elevator).startPeriodic();
				((DummySubsystemForTesting)(Object)arm).startPeriodic();
				// Start Periodic Updates
//				new PeriodicTimerThread(elevator).start();
//				new PeriodicTimerThread(arm).start();
			} else {
				// WPILib Mode
				elevator = new Motor(new ElevatorConfiguration());
				arm = new Motor(new ArmConfiguration());
				// Start Monitoring
				((Motor)(Object)elevator).startMonitoring();
				((Motor)(Object)elevator).startMonitoring();
				// NB: No need to start periodic updates, we're tied into Robot in this case
			}
			// Logger.setLoggingLevel(LogLevel.ERROR);
			Length ninety = arm.getConfiguration().createDisplayLength(90);
			// ARM
			arm.moveToAbsolutePosition(ninety);
			waitForCompletion(arm);
			arm.calibrateSensorInDirection(Direction.REVERSE);
			waitForCompletion(arm);
			
			// Elevator
			elevator.moveToAbsolutePosition(LengthUOM.Inches.create(10));
			waitForCompletion(elevator);
			elevator.moveToAbsolutePosition(LengthUOM.Inches.create(30));
			waitForCompletion(elevator);
			elevator.moveToAbsolutePosition(LengthUOM.Inches.create(5));
			waitForCompletion(elevator);
			elevator.moveToRelativePosition(Direction.FORWARD, LengthUOM.Inches.create(5));
			waitForCompletion(elevator);
			elevator.moveToRelativePosition(Direction.REVERSE, LengthUOM.Inches.create(5));
			waitForCompletion(elevator);
			elevator.moveToAbsolutePosition(LengthUOM.Inches.create(-1));
			waitForCompletion(elevator);
			elevator.moveToAbsolutePosition(LengthUOM.Inches.create(1000));
			waitForCompletion(elevator);
			elevator.calibrateSensorInDirection(Direction.REVERSE);
			waitForCompletion(elevator);
			elevator.moveInDirectionAtRate(Direction.FORWARD, RateUOM.FeetPerSecond.create(6));
			waitForCompletion(elevator);
	//		Logger.setLoggingLevel(LogLevel.DEBUG);
			elevator.moveInDirectionAtDefaultRate(Direction.REVERSE);
			waitForCompletion(elevator);
	//		Logger.setLoggingLevel(LogLevel.INFO);
			Logger.info("-----------------------------------------------------------");
			Logger.info("ALL DONE.");
			Logger.info("-----------------------------------------------------------");
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
}
