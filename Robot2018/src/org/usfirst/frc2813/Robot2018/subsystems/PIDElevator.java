package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 *
 */
public class PIDElevator extends PIDSubsystem {
	private static final boolean DEBUG = false;
	
	public final SpeedController speedController = RobotMap.elevatorSpeedControllerPort;
    public final Encoder encoder = RobotMap.elevatorQuadratureEncoder1;
    public final DigitalInput limitSwitch = RobotMap.elevatorLimitSwitch;
    private boolean rateMode; // as opposed to position mode.

    public void moveTo(double pos) {
    	enable();
    }
    // Initialize your subsystem here
    public PIDElevator() {
    	super(0, 0, 0);
        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        // enable() - Enables the PID controller.
    }

    public void initDefaultCommand() {
    //    initDefaultCommand(new MaintainElevatorPosition);		// TODO:  redo all of PID elevator for Talons - this code was for Victors
    }

    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;
        return rateMode ? encoder.getDistance() : encoder.getDistance();		// TODO:  Review what returnPidInput should return
    }

    protected void usePIDOutput(double output) {
    	if(DEBUG)
    		System.out.println("[ELEVATOR] Output updated to: "+output);
        speedController.set(output);
    }
}
