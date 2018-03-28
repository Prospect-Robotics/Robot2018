package org.usfirst.frc2813.Robot2018.motor.test;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.operation.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.logging.Logger;

/**
 * I monitor motors when they start and stop an operation.
 */
public class MotorMonitorThread extends Thread {
	private long lastRun = System.currentTimeMillis();
	private final IMotor motor;

	private MotorCommandMonitor checker = null;
	private IMotorState lastAnnounced = null;
	static final int PERIODIC_INTERVAL = 25;
	private int numStates = 0;

	public MotorMonitorThread(IMotor motor) {
		this.motor = motor;
	}
	
	public void run() {
		while(true) {
			try {
				IMotorState targetState = motor.getTargetState();
				if(lastAnnounced != targetState) {
					checker = new MotorCommandMonitor(motor, numStates);
					if(numStates != 0 || !targetState.isDisabled()) {
						checker.printHeader();
					}
				}
				if(!checker.isDone() && checker.check()) {
					// done now...
					if(numStates != 0 || !targetState.isDisabled()) {
						checker.printSummary();
					}
				}
				if(lastAnnounced != targetState) {
					lastAnnounced = targetState;
					numStates++;
				}
				try {
					Thread.sleep(PERIODIC_INTERVAL);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				Thread.yield();
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
}
