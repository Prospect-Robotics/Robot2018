package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
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
public interface IMotor {
	
	// Information
	
	/** 
	 * Get the name of the controller for debugging purposes 
	 **/
	public String getName();
	/**
	 * Get the configuration
	 */
	public IMotorConfiguration getConfiguration();
	/**
	 * Used to read the current position of the motor.  NOTE: If you want the target position, get it from getTargetState().getTargetPosition().
	 */
	public Length getCurrentPosition();
	/**
	 * Used to read the current rate of the motor.  
	 * NOTE: If you want the target rate, get it from getTargetState().getTargetRate().
	 */
	public Rate getCurrentRate();
	/**
	 * Used to read the target state for the motor; what we are asking it to do.  
	 * NOTE: If you want the current status, use getCurrentXXX() methods instead.
	 * @see GetCurrentPosition()
	 * @see GetCurrentRate()
	 */
	public IMotorState getTargetState();
	/**
	 * Used to read the previous target state for the motor; what we were last asking it to do.
	 * NOTE: If you want the target state, use getTargetState() methods instead.
	 * NOTE: If you want the current status, use getCurrentXXX() methods instead.
	 * @see getTargetState()
	 * @see getLimitSwitch
	 */
	public IMotorState getPreviousTargetState();
	/**
	 * Read the current state of the hard limit switch.
	 */
	public boolean getCurrentHardLimitSwitchStatus(Direction switchDirection);
	/**
	 * Read the current state of the soft limit switch.
	 */
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection);

	// Debugging
	
	/**
	 * Dump the output of getDiagnostics() to the logger
	 */
	public void dumpDiagnostics();
	/**
	 * Get a diagnostic description of the controller's target and current state.
	 */
	public String getDiagnostics();
	
	// Actions
	
	/**
	 * Get the motor configuration information.  This will include all the high level meta-information
	 * about how the motor is being used in this application, including lower level information the 
	 * controller uses to do it's job. 
	 */
	public void configure();
	/**
	 * Set the target for the motor to move in a particular direction and rate.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean moveInDirectionAtRate(Direction direction, Rate rate);
	/**
	 * Set the target for the motor to move in a particular direction and default rate.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean moveInDirectionAtDefaultRate(Direction direction);
	/**
	 * Set the target for the motor to move to a particular absolute position and hold it using closed
	 * loop.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean moveToAbsolutePosition(Length position);
	/**
	 * Set the target for the motor to move a specific relative distance.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance);
	/**
	 * Set the target for the motor to disabled/neutral.  Motor will be in OFF mode (either braking or neutral)
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean disable();
	/**
	 * Set the target for the motor to hold whatever the current position is.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getTargetState()
	 **/
	public boolean holdCurrentPosition();
	/**
	 * Shortcut to getTargetState().getCurrentPositionError()
	 * @See IMotorState.getCurrentPositionError()
	 */
	Length getCurrentPositionError();
	/**
	 * Shortcut to getTargetState().getCurrentRateError()
	 * @See IMotorState.getCurrentRateError()
	 */
	Rate getCurrentRateError();
	/**
	 * Shortcut to getTargetState().getCurrentRateErrorWithin()
	 * @See IMotorState.getCurrentRateErrorWithin()
	 */
	boolean getCurrentRateErrorWithin(Rate marginOfError);
	/**
	 * Shortcut to getTargetState().getCurrentPositionErrorWithin()
	 * @See IMotorState.getCurrentPositionErrorWithin()
	 */
	boolean getCurrentPositionErrorWithin(Length marginOfError);
	/**
	 * Move the arm towards a hard limit switch (until we hit it)
	 */
	boolean calibrateSensorInDirection(Direction direction);
	/**
	 * Hook for periodic updates.  We use this to update our behavior 
	 * as we are reaching our target condition. 
	 */
	void periodic();
	/**
	 * For testing auto, we'll ignore this device if it's disconnected 
	 */
	boolean isDisconnected();
}
