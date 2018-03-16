package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

public interface IMotorController {
	
	// Information
	
	// Get the name of the controller for debugging purposes
	public String getName();
	// Get the configuration
	public MotorConfiguration getConfiguration();
	// Used to read the position
	public Length readPosition();
	// Read the motors state of operation
	public MotorState getState();
	// Get the details of the previous state configuration
	public MotorState getPreviousState();
	// Read a limit switch
	public boolean readLimitSwitch(Direction switchDirection);
	// Return true if motor inversion can be handled automatically
	public boolean supportsMotorInversion();
	// Return true if sensor inversion can be handled automatically
	public boolean supportsSensorInversion();
	
	// Debugging
	public void dumpState();
	
	// Actions
	
	// Configure the motor according to configuration
	public void configure();
	// Set the direction and speed
	public boolean move(Direction direction, Rate speedParam);
	// Used to set an absolute position for closed loop position holding
	public boolean moveToPosition(Length position);
	// Used to move a specific amount
	public boolean moveADistance(Length distance);
	// Disable the motor
	public boolean disable();
	// Hold the current position (PID if possible, brake if possible)
	public boolean holdCurrentPosition();
	// Set the encoder position value (if it's resettable)
	public boolean resetEncoderSensorPosition(Length position);
}
