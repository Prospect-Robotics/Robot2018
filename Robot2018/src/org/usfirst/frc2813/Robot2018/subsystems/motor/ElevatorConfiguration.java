package org.usfirst.frc2813.Robot2018.subsystems.motor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPositionAsync;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.PIDConfiguration;
import org.usfirst.frc2813.Robot2018.motor.PIDProfileSlot;
import org.usfirst.frc2813.Robot2018.subsystems.ICommandFactory;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public class ElevatorConfiguration extends MotorConfiguration {

	// Talon constants
	private static final double PULSES_PER_ENCODER_REVOLUTION = 4096;
	
	private static final Length MINIMUM_POSITION_INCHES = LengthUOM.Inches.create(0);
	private static final Length MAXIMUM_POSITION_INCHES = LengthUOM.Inches.create(82);
	private static final Length SAFETY_MARGIN_INCHES = LengthUOM.Inches.create(4);
	// Gearing constants
	private static final double SENSOR_TO_DRIVE = 1.0;
	private static final double MOTOR_TO_DRIVE = 1.0/30.5;

	// Motor constants
	private static final double    MAX_RPMS_UNLOADED             = 18700;
	private static final double    MAX_RPMS_UNLOADED_GEARED_DOWN = MAX_RPMS_UNLOADED	* MOTOR_TO_DRIVE;

	// Software Settings
	private static final Rate   DEFAULT_SPEED_INCHES_PER_SECOND = RateUOM.InchesPerSecond.create(12); // TBD

	// Hardware Inputs
	private static final Length SHAFT_DIAMETER                = LengthUOM.Inches.create(1.25);
	private static final Length CORD_DIAMETER                 = LengthUOM.Millimeters.create(3); 
	private static final Length DRIVE_AXIS_DIAMETER           = SHAFT_DIAMETER.add(CORD_DIAMETER);	
	private static final Length INCHES_PER_ENCODER_REVOLUTION = DRIVE_AXIS_DIAMETER.multiply(Math.PI);

	// Calculations
	private static final double PULSE_PER_INCH                = PULSES_PER_ENCODER_REVOLUTION/INCHES_PER_ENCODER_REVOLUTION.getValue();
	private static final Length INCHES_PER_PULSE              = LengthUOM.Inches.create(1.0/PULSE_PER_INCH);
	private static final Length PULSE_CANONICAL_LENGTH        = INCHES_PER_PULSE.convertToCanonicalUOM();

	// Units Of Length for Elevator
	public static final LengthUOM ElevatorSRXMotorPulses      = new LengthUOM("srxpulse", "srxpulses", "p", LengthUOM.CanonicalLengthUOM, PULSE_CANONICAL_LENGTH.getCanonicalValue());
	public static final LengthUOM ElevatorSRXEncoderRevolution= new LengthUOM("revolution", "revolutions", "rev", LengthUOM.CanonicalLengthUOM, INCHES_PER_ENCODER_REVOLUTION.getCanonicalValue());

	// Units Of Rate for Elevator
	public static final RateUOM   ElevatorSRXMotorPulseRate   = new RateUOM(ElevatorSRXMotorPulses, TimeUOM.Deciseconds, RateUOM.CanonicalRateUOMForMovement, "Elevator-pulses/100ms");
	public static final RateUOM   ElevatorSRXEncoderRPM       = new RateUOM(ElevatorSRXEncoderRevolution, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "Elevator-RPMs");
	public static final RateUOM   ElevatorSRXEncoderRPS       = new RateUOM(ElevatorSRXEncoderRevolution, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Elevator-RPSs");

	public static final Rate      ElevatorSRXMotorMaxRPM      = ElevatorSRXEncoderRPM.create(MAX_RPMS_UNLOADED_GEARED_DOWN);
	public static final Rate      ElevatorSRXMotorMaxRPS      = ElevatorSRXEncoderRPS.create(MAX_RPMS_UNLOADED_GEARED_DOWN / 60);

	private static final Length    maxDistancePerMinute           = ElevatorSRXMotorMaxRPM.getLength(ElevatorSRXMotorMaxRPM.getTimeUOM().getValue());
//	private static final Length    maxDistancePerSecond           = ElevatorSRXMotorMaxRPS.getLength(ElevatorSRXMotorMaxRPS.getTimeUOM().getValue());
	private static final LengthUOM OneSecondDistanceAtOnePercent  = new LengthUOM("minute-distance", "minute-distance", "minute-distance", LengthUOM.CanonicalLengthUOM, maxDistancePerMinute.getCanonicalValue()/100.0);
	public static final RateUOM    ElevatorSRXMotorPercentageRate = new RateUOM(OneSecondDistanceAtOnePercent, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "% Elevator");

	public static void mathReport() {
		System.out.println();
		System.out.println("[Software Settings]");
		System.out.println("Range.............................{" + MINIMUM_POSITION_INCHES + ".." + MAXIMUM_POSITION_INCHES + "}");
		System.out.println("Default Speed....................." + DEFAULT_SPEED_INCHES_PER_SECOND);
		System.out.println("Default Speed (Encoder RPM)......." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(ElevatorSRXEncoderRPM));
		System.out.println("Default Speed (SRX)..............." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(ElevatorSRXMotorPulseRate));
		System.out.println();
		System.out.println("[Robot Measurements]");
		System.out.println("SHAFT.............................d=" + SHAFT_DIAMETER + ", r=" + SHAFT_DIAMETER.divide(2) + ", c=" + SHAFT_DIAMETER.multiply(Math.PI));
		System.out.println("CORD..............................d=" + CORD_DIAMETER + ", r=" + CORD_DIAMETER.divide(2) + ", c=" + CORD_DIAMETER.multiply(Math.PI));
		System.out.println("DRIVE.............................d=" + DRIVE_AXIS_DIAMETER + ", r=" + DRIVE_AXIS_DIAMETER.divide(2) + ", c=" + DRIVE_AXIS_DIAMETER.multiply(Math.PI));
		System.out.println();
		System.out.println("[Units Of Measure]");
		System.out.println("EncoderRevolution................." + ElevatorSRXEncoderRevolution.getValue() + " = " + ElevatorSRXEncoderRevolution.getValue().convertTo(LengthUOM.Inches));
		System.out.println("Max Pulse/Decisecond.............." + ElevatorSRXMotorPulseRate.getValue() + " = " + ElevatorSRXMotorPulseRate.getCanonicalValue());
		System.out.println("Max RPMs.........................." + ElevatorSRXMotorMaxRPM + " = " + ElevatorSRXMotorMaxRPM.convertTo(ElevatorSRXEncoderRPS) + " = " + ElevatorSRXMotorMaxRPM.convertTo(RateUOM.InchesPerSecond));
		System.out.println();
		System.out.println("[Calculations]");
		System.out.println("Pulses/rev........................" + PULSES_PER_ENCODER_REVOLUTION);
		System.out.println("Inches/rev........................" + INCHES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/inch......................." + PULSE_PER_INCH);
		System.out.println("Pulse Length (Inches)............." + INCHES_PER_PULSE);
		System.out.println("Pulse Length (Canonical).........." + PULSE_CANONICAL_LENGTH);
		System.out.println("Distance in Second................" + ElevatorSRXMotorMaxRPM.getLength(TimeUOM.Seconds.create(1)).convertTo(LengthUOM.Feet));
		System.out.println("Distance in Minute................" + ElevatorSRXMotorMaxRPM.getLength(TimeUOM.Minutes.create(1)).convertTo(LengthUOM.Feet));
 		System.out.println("Elevator 100% Rate................" + ElevatorSRXMotorPercentageRate.create(100) + " = " + ElevatorSRXMotorPercentageRate.create(100).convertTo(RateUOM.FeetPerSecond));
 		System.out.println("Elevator % Rate Table.............");
		for(int q = 0; q <= 100; q++) {
			Rate pct = ElevatorSRXMotorPercentageRate.create(q);
			Rate pr = ElevatorSRXMotorPulseRate.create(pct.convertTo(ElevatorSRXMotorPulseRate).getValueAsInt());
			Rate rpm = ElevatorSRXEncoderRPM.create(pct.convertTo(ElevatorSRXEncoderRPM).getValueAsInt());
			Rate ips = RateUOM.InchesPerSecond.create(pct.convertTo(RateUOM.InchesPerSecond).getValueAsInt());
			System.out.println("                                  " + pct + " = " + pr + " = " + rpm + " = " + ips);	
		}
/*		
		System.out.println();
		System.out.println("[Conversion Table]");
		System.out.println("Elevator SRX Revolution..........." + ElevatorSRXMotorRevolution.getValue());

		Iterator<UOM> i = UOM.allUnits.get(SystemOfMeasurement.Length).iterator();
		while(i.hasNext()) {
			System.out.println("                                  " + ElevatorSRXMotorRevolution.getValue().convertTo((LengthUOM)i.next()));
		}
		System.out.println("Elevator SRX Rate................." + ElevatorSRXMotorPulseRate);
		i = UOM.allUnits.get(SystemOfMeasurement.Rate).iterator();
		while(i.hasNext()) {
			System.out.println("                                  " + ElevatorSRXMotorPulseRate.getValue().convertTo((RateUOM)i.next()));
		}
		System.out.println();
		System.out.println("[Reference Tables]");
		for(int j = 0; j < 24; j++) {
			System.out.println(String.format("%3d in/sec........................~%s %s", 
					j, 
					Math.round(RateUOM.InchesPerSecond.create(j).convertTo(ElevatorSRXRPM).getValue()), ElevatorSRXRPM.getUnitNameAbbreviation())
			);
		}
*/		
	}
	public static List<PIDConfiguration> createPidConfigurations() {
		List<PIDConfiguration> pidConfigurations = new ArrayList<PIDConfiguration>();
		pidConfigurations.add(new PIDConfiguration("Holding", PIDProfileSlot.HoldingPosition.getProfileSlotIndex(), 0.8, 0.0, 0.0, 0.0));
		pidConfigurations.add(new PIDConfiguration("Moving", PIDProfileSlot.Moving.getProfileSlotIndex(), 0.5, 0.0, 0.0, 0.0)); 
//		pidConfigurations.add(new PIDConfiguration("Moving", PIDProfileSlot.Moving.getProfileSlotIndex(), 0.45, 0.011, 45.0, 0.0)); 
		pidConfigurations.add(new PIDConfiguration("ProfileSlot3", PIDProfileSlot.ProfileSlot2.getProfileSlotIndex(), 0.0, 0.0, 0.0, 0.0));
		pidConfigurations.add(new PIDConfiguration("ProfileSlot4", PIDProfileSlot.ProfileSlot3.getProfileSlotIndex(), 0.0, 0.0, 0.0, 0.0));
		return Collections.unmodifiableList(pidConfigurations);
	}

	public ElevatorConfiguration() {
		super(
			"Elevator",
			(0
					|IMotorConfiguration.ControlDirection
					|IMotorConfiguration.ControlPosition
					|IMotorConfiguration.ControlRate
					|IMotorConfiguration.Forward
					|IMotorConfiguration.LocalReverseHardLimitSwitch
					|IMotorConfiguration.ForwardSoftLimitSwitch
					|IMotorConfiguration.LimitPosition
					|IMotorConfiguration.LimitRate
					|IMotorConfiguration.SensorToDriveScale
					|IMotorConfiguration.ReadDirection
					|IMotorConfiguration.ReadPosition
					|IMotorConfiguration.ReadRate
					|IMotorConfiguration.Reverse
					|IMotorConfiguration.DefaultRate
					|IMotorConfiguration.NeutralMode
//					|IMotorConfiguration.Disconnected // NB: WARNING !!
					),
			LengthUOM.Inches,                   // nativeDisplayLengthUOM
			ElevatorSRXMotorPulses,             // nativeMotorLengthUOM
			Boolean.FALSE,                      // motorPhaseIsReversed
			Boolean.FALSE,                      // sensorPhaseIsReversed
			ElevatorSRXMotorPulses,   	        // nativeSensorLengthUOM
			RateUOM.InchesPerSecond,            // nativeDisplayRateUOM
			ElevatorSRXMotorPulseRate,          // nativeMotorRateUOM
			ElevatorSRXMotorPulseRate,          // nativeSensorRateUOM
			RateUOM.InchesPerSecond.create(0),  // minimumForwardRate
			RateUOM.InchesPerSecond.create(12), // maximumForwardRate (placeholder)
			RateUOM.InchesPerSecond.create(0),  // minimumReverseRate
			RateUOM.InchesPerSecond.create(12), // maximumReverseRate (placeholder)
			Double.valueOf(SENSOR_TO_DRIVE),    // sensorToDriveScale (per JT - output 1:1 on Elevator)
			MAXIMUM_POSITION_INCHES,                          // forwardLimit (placeholder)
			MINIMUM_POSITION_INCHES,                          // reverseLimit
			null,                               // forwardHardLimitSwitchNormal
			null,                               // forwardHardLimitSwitchResetsEncoder
			LimitSwitchNormal.NormallyOpen,     // reverseHardLimitSwitchNormal
			Boolean.TRUE,                       // reverseHardLimitSwitchResetsEncoder
			MAXIMUM_POSITION_INCHES.subtract(SAFETY_MARGIN_INCHES),        // forwardSoftLimit - set to 4" below the end of the physical range 
			null,                               // reverseSoftLimit // NB: This is the correct value when hardware limit is fixed
			RateUOM.InchesPerSecond.create(12), // defaultRate
			com.ctre.phoenix.motorcontrol.NeutralMode.Brake, // neutralMode
			ElevatorSRXMotorPercentageRate,      // percentageRate
			null, // remoteForwardHardLimitSwitchSource
			null, // remoteForwardHardLimitSwitchDeviceId
			null, // remoteReverseHardLimitSwitchSource
			null, // remoteReverseHardLimitSwitchDeviceId
			new ICommandFactory<Motor>() { // defaultCommand 
				public GearheadsCommand createCommand(Motor m) { 
					return new MotorHoldPositionAsync(m); 
				}
			},
			createPidConfigurations() // pidConfigurations
			);
	}
}

