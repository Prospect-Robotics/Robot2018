package org.usfirst.frc2813.Robot2018.subsystems.motor;

import java.util.Iterator;

import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Time;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

import edu.wpi.first.wpilibj.command.Command;

public class ArmConfiguration extends MotorConfiguration{

	// Talon constants
	private static final double PULSES_PER_ENCODER_REVOLUTION = 4096.0;
	
	// Gearing constants
	private static final double SENSOR_TO_DRIVE = (36.0/30.0)*100.0;
	private static final double MOTOR_TO_DRIVE = (36.0/30.0)*100.0;
	
	// Motor constants
	private static final double    MAX_RPMS_UNLOADED             = 18700;
	private static final double    MAX_RPMS_UNLOADED_GEARED_DOWN = MAX_RPMS_UNLOADED / MOTOR_TO_DRIVE;

	// Hardware Inputs
	private static final Length SHAFT_DIAMETER                = LengthUOM.Inches.create(0);
	private static final Length CORD_DIAMETER                 = LengthUOM.Millimeters.create(0);
	private static final Length ARM_LENGTH                 	  = LengthUOM.Inches.create(12);
	private static final Length DRIVE_AXIS_DIAMETER           = SHAFT_DIAMETER.add(CORD_DIAMETER).add(ARM_LENGTH);	
	private static final double PULSES_PER_DRIVE_REVOLUTION   = (PULSES_PER_ENCODER_REVOLUTION * SENSOR_TO_DRIVE);
	private static final Length INCHES_PER_ENCODER_REVOLUTION = LengthUOM.Inches.create(DRIVE_AXIS_DIAMETER.multiply(Math.PI).getValue()/SENSOR_TO_DRIVE);

	// Calculations
	private static final Length PULSE_PER_DEGREE              = LengthUOM.Inches.create(PULSES_PER_DRIVE_REVOLUTION).divide(LengthUOM.Inches.create(360));
	private static final Length INCHES_PER_DEGREE             = LengthUOM.Inches.create(DRIVE_AXIS_DIAMETER.multiply(Math.PI).getCanonicalValue()/360);
	private static final Length INCHES_PER_PULSE              = INCHES_PER_DEGREE.divide(PULSE_PER_DEGREE);
	private static final Length PULSE_CANONICAL_LENGTH        = INCHES_PER_PULSE.convertToCanonicalUOM();

	// Units Of Length for Arm
	private static final LengthUOM ArmDegrees = new LengthUOM("degree", "degrees", "deg", LengthUOM.CanonicalLengthUOM, INCHES_PER_DEGREE.getCanonicalValue());
	public static final LengthUOM ArmSRXMotorPulses      = new LengthUOM("srxpulse", "srxpulses", "p", LengthUOM.CanonicalLengthUOM, PULSE_CANONICAL_LENGTH.getCanonicalValue());
	public static final LengthUOM ArmSRXEncoderRevolution= new LengthUOM("revolution", "revolutions", "rev", LengthUOM.CanonicalLengthUOM, INCHES_PER_ENCODER_REVOLUTION.getCanonicalValue());
	
	// Units Of Rate for Arm
	public static final RateUOM   ArmSRXMotorPulseRate   = new RateUOM(ArmSRXMotorPulses, TimeUOM.Deciseconds, RateUOM.CanonicalRateUOMForMovement, "Arm-pulses/100ms");
	public static final RateUOM   ArmSRXEncoderRPM       = new RateUOM(ArmSRXEncoderRevolution, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "Arm-RPMs");
	public static final RateUOM   ArmSRXEncoderRPS       = new RateUOM(ArmSRXEncoderRevolution, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Arm-RPSs");
	public static final RateUOM   ArmDegreesPerSecond    = new RateUOM(ArmDegrees, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Arm-DPSs");

	// Software Settings
	private static final Length MINIMUM_POSITION_DEGREES = ArmDegrees.create(0);
	private static final Length MAXIMUM_POSITION_DEGREES = ArmDegrees.create(180); // TBD
	private static final Rate   DEFAULT_SPEED_DEGREES_PER_SECOND = ArmDegreesPerSecond.create(180); // TBD
	
	public static final Rate      ArmSRXDriveMaxRPM      = ArmSRXEncoderRPM.create(MAX_RPMS_UNLOADED_GEARED_DOWN);
	public static final Rate      ArmSRXMotorMaxRPS      = ArmSRXEncoderRPS.create(MAX_RPMS_UNLOADED_GEARED_DOWN / 60);

	private static final Length    maxDistancePerMinute           = ArmSRXDriveMaxRPM.getLength(ArmSRXDriveMaxRPM.getTimeUOM().getValue());
	private static final Length    maxDistancePerSecond           = ArmSRXMotorMaxRPS.getLength(ArmSRXMotorMaxRPS.getTimeUOM().getValue());
	private static final LengthUOM OneSecondDistanceAtOnePercent  = new LengthUOM("minute-distance", "minute-distance", "minute-distance", LengthUOM.CanonicalLengthUOM, maxDistancePerMinute.getCanonicalValue()/100.0);
	public static final RateUOM    ArmSRXMotorPercentageRate = new RateUOM(OneSecondDistanceAtOnePercent, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "% Arm");

	public static void mathReport() {
		System.out.println();
		System.out.println("[Software Settings]");
		System.out.println("Range.............................{" + MINIMUM_POSITION_DEGREES + ".." + MAXIMUM_POSITION_DEGREES + "}");
		System.out.println("Default Speed (Motor)............." + DEFAULT_SPEED_DEGREES_PER_SECOND);
		System.out.println("Default Speed (Encoder)..........." + DEFAULT_SPEED_DEGREES_PER_SECOND.multiply(MOTOR_TO_DRIVE));
		System.out.println("Default Speed (SRX)..............." + DEFAULT_SPEED_DEGREES_PER_SECOND.convertTo(ArmSRXMotorPulseRate));
		System.out.println();
		System.out.println("[Robot Measurements]");
		System.out.println("SHAFT.............................d=" + SHAFT_DIAMETER + ", r=" + SHAFT_DIAMETER.divide(2) + ", c=" + SHAFT_DIAMETER.multiply(Math.PI));
		System.out.println("CORD..............................d=" + CORD_DIAMETER + ", r=" + CORD_DIAMETER.divide(2) + ", c=" + CORD_DIAMETER.multiply(Math.PI));
		System.out.println("DRIVE.............................d=" + DRIVE_AXIS_DIAMETER + ", r=" + DRIVE_AXIS_DIAMETER.divide(2) + ", c=" + DRIVE_AXIS_DIAMETER.multiply(Math.PI));
		System.out.println();
		System.out.println("[Units Of Measure]");
		System.out.println("EncoderRevolution................." + ArmSRXEncoderRevolution.getValue() + " = " + ArmSRXEncoderRevolution.getValue().convertTo(LengthUOM.Inches));
		System.out.println("Max Pulse/Decisecond.............." + ArmSRXMotorPulseRate.getValue() + " = " + ArmSRXMotorPulseRate.getCanonicalValue());
		System.out.println("Max RPMs.........................." + ArmSRXDriveMaxRPM + " = " + ArmSRXDriveMaxRPM.convertTo(ArmSRXEncoderRPS) + " = " + ArmSRXDriveMaxRPM.convertTo(ArmSRXEncoderRPS).getValue() * DRIVE_AXIS_DIAMETER.multiply(Math.PI).getValue());
		System.out.println();
		System.out.println("[Calculations]");
		System.out.println("Pulses/encoder rev................" + PULSES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/drive rev.................." + PULSES_PER_DRIVE_REVOLUTION);
		System.out.println("Inches/encoder rev................" + INCHES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/drive degree..............." + PULSE_PER_DEGREE);
		System.out.println("Inches/drive degree..............." + INCHES_PER_DEGREE);
		System.out.println("Pulse Length (Inches)............." + INCHES_PER_PULSE);
		System.out.println("Pulse Length (Canonical).........." + PULSE_CANONICAL_LENGTH);
		System.out.println("Distance in Second................" + ArmSRXDriveMaxRPM.getLength(TimeUOM.Seconds.create(1)).convertTo(LengthUOM.Inches)); //Wrong
		System.out.println("Distance in Minute................" + ArmSRXDriveMaxRPM.getLength(TimeUOM.Minutes.create(1)).convertTo(LengthUOM.Inches)); //Wrong
 		System.out.println("Arm 100% Rate................" + ArmSRXMotorPercentageRate.create(100) + " = " + ArmSRXMotorPercentageRate.create(100).convertTo(RateUOM.FeetPerSecond));
 		System.out.println("Arm % Rate Table.............");
		for(int q = 0; q <= 100; q++) {
			Rate pct = ArmSRXMotorPercentageRate.create(q);
			Rate pr = ArmSRXMotorPulseRate.create(pct.convertTo(ArmSRXMotorPulseRate).getValueAsInt());
			Rate rpm = ArmSRXEncoderRPM.create(pct.convertTo(ArmSRXEncoderRPM).getValueAsInt());
			Rate ips = RateUOM.InchesPerSecond.create(pct.convertTo(RateUOM.InchesPerSecond).getValueAsInt());
			System.out.println("                                  " + pct + " = " + pr + " = " + rpm + " = " + ips);	
		}

		System.out.println();
		System.out.println("[Conversion Table]");
		System.out.println("Arm SRX Revolution..........." + ArmSRXEncoderRevolution.getValue());

		Iterator<UOM> i = UOM.allUnits.get(SystemOfMeasurement.Length).iterator();
		while(i.hasNext()) {
			System.out.println("                                  " + ArmSRXEncoderRevolution.getValue().convertTo((LengthUOM)i.next()));
		}
		System.out.println("Arm SRX Rate................." + ArmSRXMotorPulseRate);
		i = UOM.allUnits.get(SystemOfMeasurement.Rate).iterator();
		while(i.hasNext()) {
			System.out.println("                                  " + ArmSRXMotorPulseRate.getValue().convertTo((RateUOM)i.next()));
		}
		System.out.println();
		System.out.println("[Reference Tables]");
		for(int j = 0; j < 24; j++) {
			System.out.println(String.format("%3d in/sec........................~%s %s", 
					j, 
					Math.round(RateUOM.InchesPerSecond.create(j).convertTo(ArmSRXEncoderRPM).getValue()), ArmSRXEncoderRPM.getUnitNameAbbreviation())
			);
		}
	
	}

	public ArmConfiguration() {
		super(
			"Arm Rotational Axis",
			(0
					|MotorConfiguration.ControlDirection
					|MotorConfiguration.ControlPosition
					|MotorConfiguration.ControlRate
					|MotorConfiguration.Forward
//					|MotorConfiguration.ForwardHardLimitSwitch
					|MotorConfiguration.ForwardSoftLimitSwitch
					|MotorConfiguration.LimitPosition
					|MotorConfiguration.LimitRate
					|MotorConfiguration.SensorToDriveScale
					|MotorConfiguration.ReadDirection
					|MotorConfiguration.ReadPosition
					|MotorConfiguration.ReadRate
					|MotorConfiguration.Reverse
					|MotorConfiguration.ReverseHardLimitSwitch
//					|MotorConfiguration.ReverseSoftLimitSwitch
					|MotorConfiguration.DefaultRate
					|MotorConfiguration.NeutralMode
					|MotorConfiguration.Disconnected // NB: WARNING: THIS TOTALLY DISABLES IT
					),
			ArmDegrees,                  	    // nativeDisplayLengthUOM
			ArmSRXMotorPulses,                  // nativeMotorLengthUOM
			Boolean.FALSE,                      // motorPhaseIsReversed
			Boolean.FALSE,                      // sensorPhaseIsReversed
			ArmSRXMotorPulses,   	            // nativeSensorLengthUOM
			ArmDegreesPerSecond,            // nativeDisplayRateUOM
			ArmSRXMotorPulseRate,               // nativeMotorRateUOM
			ArmSRXMotorPulseRate,               // nativeSensorRateUOM
			ArmDegreesPerSecond.create(0),  // minimumForwardRate
			ArmDegreesPerSecond.create(30), // maximumForwardRate (placeholder)
			ArmDegreesPerSecond.create(0),  // minimumReverseRate
			ArmDegreesPerSecond.create(30), // maximumReverseRate (placeholder)
			Double.valueOf(SENSOR_TO_DRIVE),    // sensorToDriveScale (per JT - output 1:1 on Elevator)
			ArmDegrees.create(180),        // forwardLimit (placeholder)
			ArmDegrees.create(0),         // reverseLimit
			null,                               // forwardHardLimitSwitchNormal
			null,                               // forwardHardLimitStopsMotor
			null,                               // forwardHardLimitSwitchResetsEncoder
			LimitSwitchNormal.NormallyOpen,     // reverseHardLimitSwitchNormal
			Boolean.TRUE,                       // reverseHardLimitStopsMotor
			Boolean.TRUE,                       // reverseHardLimitSwitchResetsEncoder
			ArmDegrees.create(180),        // forwardSoftLimit
			null,                               // reverseSoftLimit
			DEFAULT_SPEED_DEGREES_PER_SECOND, // defaultRate
			com.ctre.phoenix.motorcontrol.NeutralMode.Brake, // neutralMode
			ArmSRXMotorPercentageRate,           // percentageRate
			new ICommandFactory<Motor>() { // defaultCommand 
				public Command createCommand(Motor m) { 
					return new MotorHoldPosition(m); 
				}
			});
	}

}