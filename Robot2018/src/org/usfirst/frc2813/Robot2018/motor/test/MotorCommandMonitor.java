package org.usfirst.frc2813.Robot2018.motor.test;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Time;

public class MotorCommandMonitor extends Thread implements IMotorState.IStatusCheckCallback {
	private final IMotorState targetMotorState;
	private final IMotor motor;
	private final int operationID;
	private int lastEncoderValue = 0;
	private long startTimeMillis = System.currentTimeMillis();
	private long lastCheckedTimeMillis = 0;
	private long completedTimeMillis;
	private StringBuffer message = new StringBuffer();

	private boolean done = false;
	private String status = "unknown";
	
	public MotorCommandMonitor(IMotor motor, int operationID) {
		this.motor = motor;
		this.operationID = operationID;
		this.targetMotorState = motor.getTargetState();
	}		
	public MotorCommandMonitor(IMotor motor) {
		this(motor, -1);
	}		
	public IMotor getMotor() {
		return motor;
	}
	public int getLastEncoderValue() {
		return lastEncoderValue;
	}
	public void setLastEncoderValue(int lastEncoderValue) {
		this.lastEncoderValue = lastEncoderValue;
	}
	public long getLastCheckedTimeMillis() {
		return lastCheckedTimeMillis;
	}
	public void setLastCheckedTimeMillis(long lastCheckedTimeMillis) {
		this.lastCheckedTimeMillis = lastCheckedTimeMillis;
	}
	public void completed(String message) {
		this.message.append(message).append("\n");
//		Logger.info(message);
		status = "completed";
		done = true;
	}
	public void failed(String message) {
		this.message.append(message).append("\n");
//		Logger.info(message);
		status = "failed";
		done = true;
	}
	public void disabled(String message) {
		this.message.append(message).append("\n");
//		Logger.info(message);
		status = "disabled";
		done = true;
	}
	public void interrupted(String message) {
		this.message.append(message).append("\n");
//		Logger.info(message);
		status = "interrupted";
		done = true;
	}
	public boolean check() {
		if(targetMotorState.checkStatus(this)) {
			done = true;
			completedTimeMillis = System.currentTimeMillis();
		} else if(status.equals("unknown")){
			status = "running";
		}
		return done;
	}
	public boolean isDone() {
		return done;
	}
	public String getStatus() {
		return status;
	}
	public long getEllapsedTimeMillis() {
		if(done) {
			return completedTimeMillis - startTimeMillis;
		} else {
			return System.currentTimeMillis() - startTimeMillis;
		}
	}
	public Time getEllapsedMilliseconds() {
		return TimeUOM.Milliseconds.create(getEllapsedTimeMillis());
	}
	public Time getEllapsedSeconds() {
		return getEllapsedMilliseconds().convertTo(TimeUOM.Seconds);
	}
	public IMotorState getTargetState() {
		return targetMotorState;
	}
	public String getMessages() {
		return message.toString();
	}
	private void print(String message) {
		if(message.length() == 0) {
			Logger.info(" ");
		} else {
			Logger.info(motor + " " + message);
		}
	}
	public void printHeader() {
		synchronized(getClass()) {
			print("");
			print("-----------------------------------------------------------");
			if(operationID != -1)
				print("Objective: #" + operationID + " : " + getTargetState());
			else
				print("Objective: " + getTargetState());
			print("-----------------------------------------------------------");
			print("");
		}
	}
	public void printSummary() {
		synchronized(getClass()) {
			print("");
			print("-----------------------------------------------------------");
			if(operationID != -1)
				print("Objective: #" + operationID + " : " + getTargetState());
			else
				print("Objective: " + getTargetState());
			print("  Is Done: " + isDone());
			print("   Status: " + getStatus());
			print("  Message: " + getMessages().trim());
			print(" Last Pos: " + getLastEncoderValue());
			print("  Elapsed: " + getEllapsedSeconds());
			print("-----------------------------------------------------------");
			print("");
		}
	}
	/*
	 * Wait for one command to complete
	 */
	public void waitForCompletion(boolean quiet) {
		if(!quiet) {
			printHeader();
		}
		while(!check()) {
			Thread.yield();
		}
		if(!quiet) {
			printSummary();
		}
	}
	/*
	 * Wait for one command to complete
	 */
	public void waitForCompletion() {
		waitForCompletion(true);
	}

	/*
	 * Wait for one command to complete
	 */
	public static void waitForCompletion(IMotor motor, boolean quiet) {
		(new MotorCommandMonitor(motor)).waitForCompletion(quiet);
	}
	
	/*
	 * Wait for one command to complete
	 */
	public static void waitForCompletion(IMotor motor) {
		waitForCompletion(motor, true);
	}
};
