package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.motor.axis.AxisConfiguration;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public class ElevatorAxisConfiguration extends AxisConfiguration {
	
	public ElevatorAxisConfiguration() {
		super(
			"Elevator Vertical Axis",
			(0
					|AxisConfiguration.ControlDirection
					|AxisConfiguration.ControlPosition
					|AxisConfiguration.ControlRate
					|AxisConfiguration.Forward
//					|AxisConfiguration.ForwardHardLimitSwitch
					|AxisConfiguration.ForwardSoftLimitSwitch
					|AxisConfiguration.LimitPosition
					|AxisConfiguration.LimitRate
					|AxisConfiguration.MotorToDriveScale
					|AxisConfiguration.MotorToSensorScale
					|AxisConfiguration.ReadDirection
					|AxisConfiguration.ReadPosition
					|AxisConfiguration.ReadRate
					|AxisConfiguration.Reverse
					|AxisConfiguration.ReverseHardLimitSwitch
//					|AxisConfiguration.ReverseSoftLimitSwitch
					),
			LengthUOM.Inches,                   // nativeDisplayLengthUOM
			Elevator.ElevatorSRXMotorPulses,    // nativeMotorLengthUOM
			Boolean.FALSE,                      // motorPhaseIsReversed
			Boolean.FALSE,                      // sensorPhaseIsReversed
			Elevator.ElevatorSRXMotorPulses,    // nativeSensorLengthUOM
			RateUOM.InchesPerSecond,            // nativeDisplayRateUOM
			Elevator.ElevatorSRXMotorPulseRate, // nativeMotorRateUOM
			Elevator.ElevatorSRXMotorPulseRate, // nativeSensorRateUOM
			RateUOM.InchesPerSecond.create(0),  // minimumForwardRate
			RateUOM.InchesPerSecond.create(12), // maximumForwardRate                  - TBD
			RateUOM.InchesPerSecond.create(0),  // minimumReverseRate
			RateUOM.InchesPerSecond.create(12), // maximumReverseRate                  - TBD
			Double.valueOf(1),                  // motorToSensorScale                  - TBD
			Double.valueOf(1),                  // motorToDriveScale                   - TBD
			LengthUOM.Inches.create(24),        // forwardLimit                        - TBD
			LengthUOM.Inches.create(0),         // reverseLimit
			LimitSwitchNormal.Disabled,         // forwardHardLimitSwitchBehavior
			null,                               // forwardHardLimitStopsMotor
			null,                               // forwardHardLimitSwitchResetsEncoder
			LimitSwitchNormal.NormallyOpen,     // reverseHardLimitSwitchBehavior
			Boolean.TRUE,                       // reverseHardLimitStopsMotor
			Boolean.TRUE,                       // reverseHardLimitSwitchResetsEncoder
			LengthUOM.Inches.create(24),        // forwardSoftLimit
			null                                // reverseSoftLimit
			);
	}
}
