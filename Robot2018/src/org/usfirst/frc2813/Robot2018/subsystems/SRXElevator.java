package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Elevator.MaintainElevatorPosition;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class SRXElevator extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	private static final double PULSES_PER_INCH = RobotMap.SRX_MAG_PULSES_PER_REVOLUTION / RobotMap.ELEVATOR_INCHES_PER_REVOLUTION;
	
	// TODO find maximum allowable elevator height; 24 is only a placeholder.
	private static final double ELEVATOR_HEIGHT = 24; // inches

	public static final int TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS = 10;
	public static final int TALON_SRX_RUNTIME_TIMEOUT_DEFAULT_MS = 0;

	
	
	private final TalonSRX motor = RobotMap.srxElevator;
	// No need for any other variables.
	
	public SRXElevator() {
		// Do some motor configization
		// XXX Should we reconfigure the Talon every power on?  It should store the config, right?
		// probably should be fine.

		
		// 10 is the CAN receive timeout in milliseconds.
		// Why do we have to specify it all of a sudden?  Dunno.
		/*
		 * Talon documentation suggests 10ms as the default timeout value ON INITIALIZATION
		 * During teleop, the timeout value should be 0
		 * TODO:  create constants for those values
		 * public static final int TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS = 10;
		 *  
		 */
		motor.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS);
		// For some reason CTRE decided that two slots would be enough for everything.
		
		//motor.configSelectedFeedbackSensor(com.ctre.phoenix.motorcontrol.FeedbackDevice.QuadEncoder, 0, TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS);
		motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 1, 10);
		/*motor.configForwardSoftLimitEnable(true, 10);
		motor.configForwardSoftLimitThreshold((int) (ELEVATOR_HEIGHT * PULSES_PER_INCH), TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS);*/
		motor.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 1, 0, TALON_SRX_INITIALIZE_TIMEOUT_DEFAULT_MS);
	}
	
	public void setSpeed(double feetPerSecond) {
		// set() accepts a velocity in position change per 100ms.  Divide by 10 to convert position change per 1000ms to that.
		motor.set(ControlMode.Velocity, (feetPerSecond * 12.0 * PULSES_PER_INCH) / 10.0);
	}
	
	public void setPosition(double inchesFromBottom) {
		// round to nearest int because unless we're using an analog sensor, this expects values in inches.
		// Not sure what the talon will do if we give it a float value when it expects an int value.
		// It might floor it (it *should* floor it), but it *might* puke.
		motor.set(ControlMode.Position, (int) (inchesFromBottom * PULSES_PER_INCH));
	}
	
	public void activelyMaintainCurrentPosition() {
		System.out.println("Elevator"+motor.getSelectedSensorPosition(0));
		motor.set(ControlMode.Position, motor.getSelectedSensorPosition(0));
	}

	public void initDefaultCommand() {
        setDefaultCommand(new MaintainElevatorPosition());
    }
	public void periodic() {
		if(getCurrentCommand() == null) {
    		new MaintainElevatorPosition().start();
    	}
	}
}

