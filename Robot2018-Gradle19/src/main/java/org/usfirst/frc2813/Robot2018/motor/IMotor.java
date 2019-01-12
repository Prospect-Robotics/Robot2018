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
	 * @return A human readable description of the motor 
	 **/
	public String getName();
	/**
	 * Get the configuration
	 * @return the motor's configuration
	 */
	public IMotorConfiguration getConfiguration();
	/**
	 * Used to read the current position of the motor.  NOTE: If you want the target position, get it from getTargetState().getTargetPosition().
	 * @return The current position of the motor
	 */
	public Length getCurrentPosition();
	/**
	 * Used to read the ACTUAL rate of rotation.  
	 * NOTE: If you want the target rate of rotation, get it from getTargetState().getTargetRate().
	 * @see IMotorState#getTargetRate()
	 * @return The current rate of rotation of the motor hardware, signed.
	 */
	public Rate getCurrentRate();
	/**
	 * Used to read the target state for the motor; what we are asking it to do.  
	 * NOTE: If you want the current status, use getCurrentXXX() methods instead.
	 * @see IMotor#getCurrentPosition()
	 * @see IMotor#getCurrentRate()
	 * @see IMotor#getCurrentPositionError()
	 * @see IMotor#getCurrentPositionErrorWithin(Length)
	 * @see IMotor#getCurrentRate()
	 * @see IMotor#getCurrentRateError()
	 * @see IMotor#getCurrentRateErrorWithin(Rate)
	 * @return The target state for the motor
	 */
	public IMotorState getTargetState();
	/**
	 * Used to read the previous target state for the motor; what we were last asking it to do.
	 * NOTE: If you want the target state, use getTargetState().
	 * NOTE: If you want current hardware state, use getCurrent* methods.
	 * @see IMotor#getTargetState()
	 * @see IMotor#getCurrentPosition()
	 * @see IMotor#getCurrentRate()
	 * @see IMotor#getCurrentPositionError()
	 * @see IMotor#getCurrentPositionErrorWithin(Length)
	 * @see IMotor#getCurrentRate()
	 * @see IMotor#getCurrentRateError()
	 * @see IMotor#getCurrentRateErrorWithin(Rate)
	 * @return The previous target state of the motor (last thing it was doing)
	 */
	public IMotorState getPreviousTargetState();
	/**
	 * Read the current state of the hard limit switch (either local or remote)
	 * @param switchDirection which limit switch are you asking about?
	 * @return true if a hard limit switch exists AND is active, as defined by the configuration (active high/low)
	 */
	public boolean getCurrentHardLimitSwitchStatus(Direction switchDirection);
	/**
	 * Read the current state of the soft limit switch.
	 * @param switchDirection which limit switch are you asking about?
	 * @return true if a soft limit switch exists AND is active, as defined by the configuration.
	 */
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection);

	/**
	 * Dump the output of getDiagnostics() to the logger
	 */
	public void dumpDiagnostics();
	/**
	 * Get a diagnostic description of the object
	 * @return a diagnostic description of the object
	 */
	public String getDiagnostics();
	
	/**
	 * Load the motor configuration into the motor.
	 * @see IMotor#getConfiguration()
	 * @see IMotorConfiguration  
	 */
	public void configure();
	/**
	 * Set the target for the motor to move in a particular direction and rate.
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @param direction The direction to move
	 * @param rate The velocity to move
	 * @see IMotor#getTargetState()
	 * @return true if the command is supported and accepted
	 **/
	public boolean moveInDirectionAtRate(Direction direction, Rate rate);
	/**
	 * Set the target for the motor to move in a particular direction and default rate.
	 * @return true if the command is supported and accepted
	 * @param direction The direction to move
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see IMotor#getTargetState()
	 * @return true if the command is supported and accepted
	 **/
	public boolean moveInDirectionAtDefaultRate(Direction direction);
	/**
	 * Set the target for the motor to move to a particular absolute position and hold it using closed
	 * loop.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @param absolutePosition the position to move to.
	 * @see IMotor#getTargetState()
	 **/
	public boolean moveToAbsolutePosition(Length absolutePosition);
	/**
	 * Set the target for the motor to move a specific relative distance.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @param direction The direction to move
	 * @param relativeDistance The relative distance to move
	 * @see IMotor#getTargetState()
	 **/
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance);
	/**
	 * Set the target for the motor to disabled/neutral.  Motor will be in OFF mode (either braking or neutral)
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see IMotor#getTargetState()
	 **/
	public boolean disable();
	/**
	 * Set the target for the motor to hold whatever the current position is.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see IMotor#getTargetState()
	 **/
	public boolean holdCurrentPosition();
	/**
	 * Shortcut to getTargetState().getCurrentPositionError()
	 * @see IMotorState#getCurrentPositionError()
	 * @return The current position error if we are trying to hold a position, 0 otherwise.
	 */
	Length getCurrentPositionError();
	/**
	 * Shortcut to getTargetState().getCurrentRateError()
	 * @see IMotorState#getCurrentRateError()
	 * @return The current rate error if we are trying to hold a rate, 0 otherwise.
	 */
	Rate getCurrentRateError();
	/**
	 * Shortcut to getTargetState().getCurrentRateErrorWithin()
	 * @see IMotorState#getCurrentRateErrorWithin(Rate)
	 * @param marginOfError The magnitude of error we will accept when determining if the error is "close enough"
	 * @return True if the magnitude of the error is within the +/- the marginOfError
	 */
	boolean getCurrentRateErrorWithin(Rate marginOfError);
	/**
	 * Shortcut to getTargetState().getCurrentPositionErrorWithin()
	 * @see IMotorState#getCurrentPositionErrorWithin(Length)
	 * @param marginOfError The magnitude of error we will accept when determining if the error is "close enough"
	 * @return True if the magnitude of the error is within the +/- the marginOfError
	 */
	boolean getCurrentPositionErrorWithin(Length marginOfError);
	/**
	 * Start moving the arm towards a hard limit switch at constant rate.
	 * NOTE: When we hit the limit, we will stop.  To see if we're done check the current state of the hard limit switch.
	 * @see IMotor#getCurrentHardLimitSwitchStatus(Direction)
	 * @param direction The direction to move the ARM, looking for the hard limit. 
	 * @return True if we complete the calibration
	 */
	boolean calibrateSensorInDirection(Direction direction);
	/**
	 * Hook for periodic updates.  We use this to update our behavior 
	 * as we are reaching our target condition. 
	 */
	void periodic();
	/**
	 * For testing auto, we'll ignore this device if it's disconnected
	 * @return true if the motor is being skipped because of a failure or it's not ready yet. 
	 */
	boolean isDisconnected();
	/**
	 * Get the physical limit of the hardware
	 * @param direction the direction to check
	 * @return the physical limit, as specified in the configuration.
	 */
	public Length getPhysicalLimit(Direction direction);
	/**
	 * Do we have a hard limit in the indicated direction
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasHardLimit(Direction direction);
	/**
	 * Do we have a soft limit in the indicated direction 
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasSoftLimit(Direction direction);
	/**
	 * Do we have a hard or soft limit providing safety in the indicated direction 
	 * @param direction the direction to check
	 * @return true/false 
	 */
	public boolean getHasHardOrSoftLimit(Direction direction);
	
	/**
	 * Get the soft limit switch position for the given direction, or null if we don't have one.
	 * @param direction the direction to check
	 * @see getHasSoftLimit
	 * @return The soft limit if one exists, or null if we don't have one 
	 */
	public Length getSoftLimit(Direction direction);

	/**
	 * Get the minimum forward rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumForwardRate();
	/**
	 * Get the maximum forward rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumForwardRate();
	/**
	 * Get the maximum reverse rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumReverseRate();
	/**
	 * Get the minimum reverse rate
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumReverseRate();
	/**
	 * Get the maximum rate
	 * @param direction the direction to check
	 * @return the requested rate, if applicable.
	 */
	public Rate getMaximumRate(Direction direction);
	/**
	 * Get the minimum rate
	 * @param direction the direction to check
	 * @return the requested rate, if applicable.
	 */
	public Rate getMinimumRate(Direction direction);
	
	/**
	 * Get the hard limit value
	 * @param direction the direction to check
	 * @return The hard limit in a direction, if a hard limit switch exists. 
	 * @see IMotor#getHasHardLimit
	 */
	public Length getHardLimit(Direction direction);

	/**
	 * Shortcut - do we have a hard limit in the indicated direction that we have exceeded?
	 * NOTE: This does NOT tell you the limit switch, it tells you whether we think we exceeded the range!
	 * @see IMotor#getCurrentHardLimitSwitchStatus(Direction)
	 * @param direction the direction to check
	 * @return true if the current position is beyond the hard limit position in the configuration
	 */
	public boolean isHardLimitExceeded(Direction direction);

	/**
	 * Shortcut - do we have a hard limit in the indicated direction that we have reached?
	 * NOTE: This does NOT tell you the limit switch, it tells you whether we think we exceeded the range!
	 * @param direction the direction to check
	 * @return true if the current position is at or beyond the hard limit position in the configuration
	 */
	public boolean isHardLimitReached(Direction direction);

	/**
	 * Shortcut - do we think we should have passed a hard limit, but the switch isn't on?
	 * @param direction the direction to check
	 * @return true if the current position is at or beyond the hard limit position in the configuration, but the switch isn't active.  false if there's no limit or it's good.
	 */
	public boolean isHardLimitNeedingCalibration(Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have exceeded?
	 * @param direction the direction to check
	 * @return true if the current position is beyond the soft limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isSoftLimitExceeded(Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have reached?
	 * @param direction the direction to check
	 * @return true if the current position is at or beyond the soft limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isSoftLimitReached(Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have exceeded?
	 * @param direction the direction to check
	 * @return true if the current position is at or beyond the physical limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isPhysicalLimitExceeded(Direction direction);

	/**
	 * Shortcut - do we have a soft limit in the indicated direction that we have reached?
	 * @param direction the direction to check
	 * @return true if the current position is at the physical limit position in the configuration.  false if there's no limit or it's good.
	 */
	public boolean isPhysicalLimitReached(Direction direction);
}
