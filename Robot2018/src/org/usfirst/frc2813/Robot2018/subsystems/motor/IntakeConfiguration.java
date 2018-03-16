package org.usfirst.frc2813.Robot2018.subsystems.motor;

import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeConfiguration extends MotorConfiguration {

	// Victor constants
	private static final double PULSES_PER_ENCODER_REVOLUTION = 4096;
	
	// Gearing constants
//	private static final double SENSOR_TO_DRIVE = 1.0;
	private static final double MOTOR_TO_DRIVE = 0.5;
	
	// Motor constants
	private static final double    MAX_RPMS_UNLOADED             = 18700;
	private static final double    MAX_RPMS_UNLOADED_GEARED_DOWN = MAX_RPMS_UNLOADED	* MOTOR_TO_DRIVE;

	// Software Settings
//	private static final Length MINIMUM_POSITION_INCHES = LengthUOM.Inches.create(0.0);
//	private static final Length MAXIMUM_POSITION_INCHES = LengthUOM.Inches.create(24.0); // TBD
	private static final Rate   DEFAULT_SPEED_INCHES_PER_SECOND = RateUOM.InchesPerSecond.create(12); // TBD

	// Hardware Inputs
	private static final Length SHAFT_DIAMETER                = LengthUOM.Inches.create(0.5);
//	private static final Length CORD_DIAMETER                 = LengthUOM.Millimeters.create(4);
	private static final Length DRIVE_AXIS_DIAMETER           = LengthUOM.Inches.create(4.0);	
	private static final Length INCHES_PER_ENCODER_REVOLUTION = DRIVE_AXIS_DIAMETER.multiply(Math.PI);

	// Calculations
	private static final double PULSE_PER_INCH                = PULSES_PER_ENCODER_REVOLUTION/INCHES_PER_ENCODER_REVOLUTION.getValue();
	private static final Length INCHES_PER_PULSE              = LengthUOM.Inches.create(1.0/PULSE_PER_INCH);
	private static final Length PULSE_CANONICAL_LENGTH        = INCHES_PER_PULSE.convertToCanonicalUOM();

	// Units Of Length for Intake
	public static final LengthUOM IntakeSPXMotorPulses      = new LengthUOM("SPXpulse", "SPXpulses", "p", LengthUOM.CanonicalLengthUOM, PULSE_CANONICAL_LENGTH.getCanonicalValue());
	public static final LengthUOM IntakeSPXEncoderRevolution= new LengthUOM("revolution", "revolutions", "rev", LengthUOM.CanonicalLengthUOM, INCHES_PER_ENCODER_REVOLUTION.getCanonicalValue());

	// Units Of Rate for Intake
	public static final RateUOM   IntakeSPXMotorPulseRate   = new RateUOM(IntakeSPXMotorPulses, TimeUOM.Deciseconds, RateUOM.CanonicalRateUOMForMovement, "Intake-pulses/100ms");
	public static final RateUOM   IntakeSPXEncoderRPM       = new RateUOM(IntakeSPXEncoderRevolution, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "Intake-RPMs");
	public static final RateUOM   IntakeSPXEncoderRPS       = new RateUOM(IntakeSPXEncoderRevolution, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Intake-RPSs");

	public static final Rate      IntakeSPXMotorMaxRPM      = IntakeSPXEncoderRPM.create(MAX_RPMS_UNLOADED_GEARED_DOWN);
	public static final Rate      IntakeSPXMotorMaxRPS      = IntakeSPXEncoderRPS.create(MAX_RPMS_UNLOADED_GEARED_DOWN / 60);

	private static final Length    maxDistancePerMinute           = IntakeSPXMotorMaxRPM.getLength(IntakeSPXMotorMaxRPM.getTimeUOM().getValue());
//	private static final Length    maxDistancePerSecond           = IntakeSPXMotorMaxRPS.getLength(IntakeSPXMotorMaxRPS.getTimeUOM().getValue());
	private static final LengthUOM OneSecondDistanceAtOnePercent  = new LengthUOM("minute-distance", "minute-distance", "minute-distance", LengthUOM.CanonicalLengthUOM, maxDistancePerMinute.getCanonicalValue()/100.0);
	public static final RateUOM    IntakeSPXMotorPercentageRate = new RateUOM(OneSecondDistanceAtOnePercent, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "% Intake");

	public static void mathReport() {
		System.out.println();
		System.out.println("[Software Settings]");
//		System.out.println("Range.............................{" + MINIMUM_POSITION_INCHES + ".." + MAXIMUM_POSITION_INCHES + "}");
		System.out.println("Default Speed....................." + DEFAULT_SPEED_INCHES_PER_SECOND);
		System.out.println("Default Speed (Encoder RPM)......." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(IntakeSPXEncoderRPM));
		System.out.println("Default Speed (SPX)..............." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(IntakeSPXMotorPulseRate));
		System.out.println();
		System.out.println("[Robot Measurements]");
		System.out.println("SHAFT.............................d=" + SHAFT_DIAMETER + ", r=" + SHAFT_DIAMETER.divide(2) + ", c=" + SHAFT_DIAMETER.multiply(Math.PI));
		System.out.println("DRIVE.............................d=" + DRIVE_AXIS_DIAMETER + ", r=" + DRIVE_AXIS_DIAMETER.divide(2) + ", c=" + DRIVE_AXIS_DIAMETER.multiply(Math.PI));
		System.out.println();
		System.out.println("[Units Of Measure]");
		System.out.println("EncoderRevolution................." + IntakeSPXEncoderRevolution.getValue() + " = " + IntakeSPXEncoderRevolution.getValue().convertTo(LengthUOM.Inches));
		System.out.println("Max Pulse/Decisecond.............." + IntakeSPXMotorPulseRate.getValue() + " = " + IntakeSPXMotorPulseRate.getCanonicalValue());
		System.out.println("Max RPMs.........................." + IntakeSPXMotorMaxRPM + " = " + IntakeSPXMotorMaxRPM.convertTo(IntakeSPXEncoderRPS) + " = " + IntakeSPXMotorMaxRPM.convertTo(RateUOM.InchesPerSecond));
		System.out.println();
		System.out.println("[Calculations]");
		System.out.println("Pulses/rev........................" + PULSES_PER_ENCODER_REVOLUTION);
		System.out.println("Inches/rev........................" + INCHES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/inch......................." + PULSE_PER_INCH);
		System.out.println("Pulse Length (Inches)............." + INCHES_PER_PULSE);
		System.out.println("Pulse Length (Canonical).........." + PULSE_CANONICAL_LENGTH);
		System.out.println("Distance in Second................" + IntakeSPXMotorMaxRPM.getLength(TimeUOM.Seconds.create(1)).convertTo(LengthUOM.Feet));
		System.out.println("Distance in Minute................" + IntakeSPXMotorMaxRPM.getLength(TimeUOM.Minutes.create(1)).convertTo(LengthUOM.Feet));
 		System.out.println("Intake 100% Rate................" + IntakeSPXMotorPercentageRate.create(100) + " = " + IntakeSPXMotorPercentageRate.create(100).convertTo(RateUOM.FeetPerSecond));
 		System.out.println("Intake % Rate Table.............");
		for(int q = 0; q <= 100; q++) {
			Rate pct = IntakeSPXMotorPercentageRate.create(q);
			Rate pr = IntakeSPXMotorPulseRate.create(pct.convertTo(IntakeSPXMotorPulseRate).getValueAsInt());
			Rate rpm = IntakeSPXEncoderRPM.create(pct.convertTo(IntakeSPXEncoderRPM).getValueAsInt());
			Rate ips = RateUOM.InchesPerSecond.create(pct.convertTo(RateUOM.InchesPerSecond).getValueAsInt());
			System.out.println("                                  " + pct + " = " + pr + " = " + rpm + " = " + ips);	
		}
	}

	public IntakeConfiguration() {
		super(
			"Intake",
			(0
					|MotorConfiguration.ControlDirection
//					|MotorConfiguration.ControlPosition
					|MotorConfiguration.ControlRate
					|MotorConfiguration.Forward
//					|MotorConfiguration.ForwardHardLimitSwitch
//					|MotorConfiguration.ForwardSoftLimitSwitch
//					|MotorConfiguration.LimitPosition
					|MotorConfiguration.LimitRate
//					|MotorConfiguration.SensorToDriveScale
					|MotorConfiguration.ReadDirection
//					|MotorConfiguration.ReadPosition
//					|MotorConfiguration.ReadRate
					|MotorConfiguration.Reverse
//					|MotorConfiguration.ReverseHardLimitSwitch
//					|MotorConfiguration.ReverseSoftLimitSwitch
					|MotorConfiguration.DefaultRate
					|MotorConfiguration.NeutralMode
					|MotorConfiguration.Disconnected // NB: WARNING THIS TOTALLY DISABLES IT!!
					),
			LengthUOM.Inches,                   // nativeDisplayLengthUOM
			IntakeSPXMotorPulses,             // nativeMotorLengthUOM
			Boolean.FALSE,                      // motorPhaseIsReversed
			null,                      // sensorPhaseIsReversed
			null,   	        // nativeSensorLengthUOM
			RateUOM.InchesPerSecond,            // nativeDisplayRateUOM
			IntakeSPXMotorPulseRate,          // nativeMotorRateUOM
			null,          // nativeSensorRateUOM
			RateUOM.InchesPerSecond.create(0),  // minimumForwardRate
			RateUOM.InchesPerSecond.create(12), // maximumForwardRate (placeholder)
			RateUOM.InchesPerSecond.create(0),  // minimumReverseRate
			RateUOM.InchesPerSecond.create(12), // maximumReverseRate (placeholder)
			null,							       // sensorToDriveScale (per JT - output 1:1 on Intake)
			null,        // forwardLimit (placeholder)
			null,         // reverseLimit
			null,         // forwardHardLimitSwitchNormal
			null,                               // forwardHardLimitStopsMotor
			null,                               // forwardHardLimitSwitchResetsEncoder
			null,						       // reverseHardLimitSwitchNormal
			null,                       // reverseHardLimitStopsMotor
			null,                       // reverseHardLimitSwitchResetsEncoder
			null,        // forwardSoftLimit
			null,                               // reverseSoftLimit
			RateUOM.InchesPerSecond.create(12), // defaultRate
			com.ctre.phoenix.motorcontrol.NeutralMode.Brake, // neutralMode
			IntakeSPXMotorPercentageRate,      // percentageRate
			new ICommandFactory<Motor>() { // defaultCommand 
				public Command createCommand(Motor m) { 
					return new MotorHoldPosition(m); 
				}
			});
	}
}
