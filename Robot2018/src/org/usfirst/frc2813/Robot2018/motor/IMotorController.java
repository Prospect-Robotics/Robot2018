package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

/*
* 
*  Nomenclature:
*  
*  - get/set "target" values -- read the current objective.  If we are going to a position and/or at a 
*    a particular speed, the motor is going to try to use it's closed loop logic to maintain the 
*    speed and/or reach the target position.  It may in fact go up and down in either speed and may
*    overshoot or oscillate on position.  The "target" values are what you told it to try to do.
*    
*  - get/set "current' values -- state read directly from the motor controller on what it is actually
*    doing at this moment.  In some cases we can't actually get up to the moment status on the closed
*    loop behavior, but wherever we can - we will use "current".
*    
*    Talon can give you 'current' position and rate information.  In closed loop modes, depending on 
*    the mode in use, we can often get the closed loop error to determine whether we are going
*    'forward' or 'reverse' at that moment in time. 
*
*/
public interface IMotorController {
	
	// Information
	
	// Get the name of the controller for debugging purposes
	public String getName();
	// Get the configuration
	public MotorConfiguration getConfiguration();
	// Used to read the position
	public Length getCurrentPosition();
	// Read the motors state of operation
	public MotorState getTargetState();
	// Get the details of the previous state configuration
	public MotorState getPreviousTargetState();
	// Read a limit switch
	public boolean readLimitSwitch(Direction switchDirection);
	// Return true if motor inversion can be handled automatically
	public boolean supportsMotorInversion();
	// Return true if sensor inversion can be handled automatically
	public boolean supportsSensorInversion();
	
	// Debugging
	public void dumpDiagnostics();
	public String getDiagnostics();
	
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
