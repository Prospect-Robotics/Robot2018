package org.usfirst.frc2813.Robot2018.subsystems.motor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.commands.motor.MotorHoldPosition;
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

public class ArmConfiguration extends MotorConfiguration{

	// Pulses per revolution
	private static final double PULSES_PER_ENCODER_REVOLUTION = 4096.0;
	private static final double SENSOR_TO_DRIVE_DIVISOR = (36.0/30.0)*100.0*4.0; // 120:1 now 480:1

	// Motor constants
	private static final double    MAX_ENCODER_RPMS_UNLOADED             = 18700;
	private static final double    MAX_ENCODER_RPMS_UNLOADED_GEARED_DOWN = MAX_ENCODER_RPMS_UNLOADED / SENSOR_TO_DRIVE_DIVISOR;
	/*
	 * Here we are taking the number of sensor pulses per rev and multiplying that by the ratio of sensor pulses per drive rotation.  
	 * This assumes it's scaling up. If not the SENSOR_TO_DRIVE should be a (1/ratio). 
	 */
	private static final double PULSES_PER_DRIVE_REVOLUTION   = (PULSES_PER_ENCODER_REVOLUTION * SENSOR_TO_DRIVE_DIVISOR);

	/*
	 * Here we are making up an arbitrary radius - so we have a way to map pulses and degrees to a unit of length.
	 * We will use the arc-length of a 360 degree arc (complete circle) to map to a full revolution's worth of pulses.
	 * This means that one of our units of "length" will actually be the distance traveled by a point that is RADIUS_DRIVE_AXIS_TO_ARM_END from the drive shaft,
	 * or literally giving the arc length of the tip of the arms!
	 * 
	 * TODO: I don't think it's really 12.
	 */
	private static final Length LENGTH_OF_DRIVE_AXIS_TO_ARM_END  = LengthUOM.Inches.create(100);
	/*
	 * To find the 'arc length' we will use as a reference in mapping angular distance to linear distance,
	 * we will take the 'arc length' of a complete circle of radius RADIUS_DRIVE_AXIS_TO_ARM_END.
	 * 
	 * Since there are 2*pi*r length, and r is RADIUS_DRIVE_AXIS_TO_ARM_END, so the arc length of a full revolution is RADIUS_DRIVE_AXIS_TO_ARM_END.multiply(2).multiply(Math.PI).
	 */
	private static final Length ARC_LENGTH_PER_DRIVE_REVOLUTION_IN_INCHES = LengthUOM.Inches.create(LENGTH_OF_DRIVE_AXIS_TO_ARM_END.multiply(2).multiply(Math.PI).getValue());
	/*
	 * We know it takes PULSES_PER_DRIVE_REVOLUTION pulses for a complete revolution, so we can divide by 360 to get pulses per degree.
	 */
	private static final Length PULSES_PER_ONE_DEGREE = LengthUOM.Inches.create(PULSES_PER_DRIVE_REVOLUTION).divide(360);
	/*
	 * We also know the arc length of one revolution, so we can divide by 360 to get the arc length per degree 
	 */
	private static final Length DRIVE_INCHES_PER_ONE_DEGREE = ARC_LENGTH_PER_DRIVE_REVOLUTION_IN_INCHES.divide(360);
	/*
	 * Now we can define a length unit representing one degree.
	 */
	public static final LengthUOM ArmDegrees = new LengthUOM("arm-degree", "arm-degrees", "arm-deg", LengthUOM.CanonicalLengthUOM, DRIVE_INCHES_PER_ONE_DEGREE.getCanonicalValue());
	/*
	 * Now let's figure out a length of drive inches per pulse 
	 */
	private static final Length DRIVE_INCHES_PER_PULSE = ARC_LENGTH_PER_DRIVE_REVOLUTION_IN_INCHES.divide(PULSES_PER_DRIVE_REVOLUTION);
	/*
	 * Here's a unit of measure that represents SRX motor pulse lengths 
	 */
	public static final LengthUOM  ArmSRXMotorPulses = new LengthUOM("srxpulse", "srxpulses", "p", LengthUOM.CanonicalLengthUOM, DRIVE_INCHES_PER_PULSE.getCanonicalValue());
	/*
	 * Now we need a UOM for motor control.  Pulse rates are specified to Talon in pulses/100ms so we need a unit for that. 
	 */
	public static final RateUOM   ArmSRXMotorPulseRate   = new RateUOM(ArmSRXMotorPulses, TimeUOM.Deciseconds, RateUOM.CanonicalRateUOMForMovement, "Arm-pulses/100ms");
	/*
	 * We also need a UOM for degrees/second because that's how we humans want to deal with the arm  
	 */
	public static final RateUOM   ArmDegreesPerSecond    = new RateUOM(ArmDegrees, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Arm-degrees/second");
	/*
	 * Now we need to figure out theoretical maximum rates.
	 * 
	 * Start with a unit of length for one revolution
	 */
	public static final LengthUOM  ArmSRXEncoderRevolution = new LengthUOM("revolution", "revolutions", "rev", LengthUOM.CanonicalLengthUOM, ARC_LENGTH_PER_DRIVE_REVOLUTION_IN_INCHES.getCanonicalValue());
	/*
	 * Now create a unit representing one revolution per minute.
	 */
	public static final RateUOM   ArmSRXEncoderRPM       = new RateUOM(ArmSRXEncoderRevolution, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "Arm-revs/minute");
	/*
	 * Now figure out the encoder pulses for that 
	 */
	public static final RateUOM   ArmSRXEncoderRPS       = new RateUOM(ArmSRXEncoderRevolution, TimeUOM.Seconds, RateUOM.CanonicalRateUOMForMovement, "Arm-revs/second");

	// Constants used for configuration
	private static final Rate   DEFAULT_SPEED_DEGREES_PER_SECOND = ArmDegreesPerSecond.create(6.5); // was 10
	private static final Length MINIMUM_POSITION_DEGREES = ArmDegrees.create(15); 
	private static final Length MAXIMUM_POSITION_DEGREES = ArmDegrees.create(110);
	private static final Rate   MINIMUM_RATE = ArmDegreesPerSecond.create(0);
	private static final Rate   MAXIMUM_RATE = ArmDegreesPerSecond.create(90);
	
	/*
	 * A value of how max RPMs in encoder units
	 */
	public static final Rate    	ArmMaxDriveRPM          	  = ArmSRXEncoderRPM.create(MAX_ENCODER_RPMS_UNLOADED_GEARED_DOWN);
	public static final Rate    	ArmMaxDriveRPS          	  = ArmSRXEncoderRPM.create(MAX_ENCODER_RPMS_UNLOADED_GEARED_DOWN/60);
	/*
	 * Now how much is that in distance per minute 
	 */
	private static final Length    maxDistancePerMinute           = ArmMaxDriveRPM.getLength(TimeUOM.Minutes.create(1));
	/*
	 * Take 1% of that and turn it into a unit representing a distance you can go at 1% power in one minute.
	 */
	public static final LengthUOM  ArmDistanceInOneSecondDistanceAtOnePercent  = new LengthUOM("minute-distance", "minute-distance", "minute-distance", LengthUOM.CanonicalLengthUOM, maxDistancePerMinute.getCanonicalValue()/100.0);
	/*
	 * Now turn that into a rate of 1%power's distance/minute which gives you 
	 * what we needed... a way to represent rate as % of output power.
	 */
	public static final RateUOM    ArmRateOnePercentOutputPerOneSecond      = new RateUOM(ArmDistanceInOneSecondDistanceAtOnePercent, TimeUOM.Minutes, RateUOM.CanonicalRateUOMForMovement, "% Arm");

	public static List<PIDConfiguration> createPidConfigurations() {
		List<PIDConfiguration> pidConfigurations = new ArrayList<PIDConfiguration>();
		pidConfigurations.add(new PIDConfiguration(PIDProfileSlot.HoldingPosition, 0.015, 0.0, 0.01, 0.0));
		pidConfigurations.add(new PIDConfiguration(PIDProfileSlot.MovingToPosition, 0.02, 0.0, 0.01, 0.0));
		pidConfigurations.add(new PIDConfiguration(PIDProfileSlot.MovingAtVelocity, 0.03, 0.0, 0.02, 1.0));
		pidConfigurations.add(new PIDConfiguration(PIDProfileSlot.NoOpPosition, 0.0, 0.0, 0.0, 0.0));
		return Collections.unmodifiableList(pidConfigurations);
	}

	public ArmConfiguration() {
		super(
			"Arm",
			(0
					|IMotorConfiguration.ControlDirection
					|IMotorConfiguration.ControlPosition
					|IMotorConfiguration.ControlRate
					|IMotorConfiguration.Forward
					|IMotorConfiguration.ForwardSoftLimitSwitch
					|IMotorConfiguration.ReverseSoftLimitSwitch // Added
					|IMotorConfiguration.LimitPosition
					|IMotorConfiguration.LimitRate
					|IMotorConfiguration.SensorToDriveScale
					|IMotorConfiguration.ReadDirection
					|IMotorConfiguration.ReadPosition
					|IMotorConfiguration.ReadRate
					|IMotorConfiguration.Reverse
					|IMotorConfiguration.LocalReverseHardLimitSwitch
					|IMotorConfiguration.DefaultRate
					|IMotorConfiguration.NeutralMode
					|IMotorConfiguration.StaticCurrentLimit
//					|MotorConfiguration.Disconnected // NB: WARNING: THIS TOTALLY DISABLES IT
					),
			ArmDegrees,                   	    	// nativeDisplayLengthUOM
			ArmSRXMotorPulses,                  	// nativeMotorLengthUOM
			Boolean.FALSE,                      	// motorPhaseIsReversed
			Boolean.TRUE,                       	// sensorPhaseIsReversed
			ArmSRXMotorPulses,   	            	// nativeSensorLengthUOM
			ArmDegreesPerSecond,                	// nativeDisplayRateUOM
			ArmSRXMotorPulseRate,               	// nativeMotorRateUOM
			ArmSRXMotorPulseRate,               	// nativeSensorRateUOM
			MINIMUM_RATE,  							// minimumForwardRate
			MAXIMUM_RATE, 							// maximumForwardRate
			MINIMUM_RATE,  							// minimumReverseRate
			MAXIMUM_RATE, 							// maximumReverseRate
			Double.valueOf(1/SENSOR_TO_DRIVE_DIVISOR), // sensorToDriveMultiplier
			MAXIMUM_POSITION_DEGREES,        		// forwardLimit
			MINIMUM_POSITION_DEGREES,         		// reverseLimit
			null,                               	// forwardHardLimitSwitchNormal
			null,                               	// forwardHardLimitSwitchResetsEncoder
			LimitSwitchNormal.NormallyOpen,     	// reverseHardLimitSwitchNormal
			Boolean.TRUE,                       	// reverseHardLimitSwitchResetsEncoder
			MAXIMUM_POSITION_DEGREES,    			// forwardSoftLimit
			MINIMUM_POSITION_DEGREES,          		// reverseSoftLimit
			DEFAULT_SPEED_DEGREES_PER_SECOND, 		// defaultRate
			com.ctre.phoenix.motorcontrol.NeutralMode.Brake, // neutralMode
			ArmRateOnePercentOutputPerOneSecond,    // percentageRate
			null, // remoteForwardHardLimitSwitchSource
			null, // remoteForwardHardLimitSwitchDeviceId
			null, // remoteReverseHardLimitSwitchSource
			null, // remoteReverseHardLimitSwitchDeviceId
			new ICommandFactory<Motor>() { // defaultCommand 
				public GearheadsCommand createCommand(Motor m) { 
					return new MotorHoldPosition(m); 
				}
			},
			createPidConfigurations(), // pidConfigurations
			0.75,
			-0.75,
			30,
			0, // was 1000,
			30
			);
	}

	public static void mathReport() {
		System.out.println();
		System.out.println("[Software Settings]");
		System.out.println("Range.............................{" + MINIMUM_POSITION_DEGREES + ".." + MAXIMUM_POSITION_DEGREES + "}");
		System.out.println("Default Speed....................." + DEFAULT_SPEED_DEGREES_PER_SECOND);
		System.out.println("Default Speed (Native)............" + DEFAULT_SPEED_DEGREES_PER_SECOND.convertTo(ArmSRXMotorPulseRate));
		System.out.println();
		System.out.println("[Robot Measurements]");
		System.out.println("ARM...............................d=" + LENGTH_OF_DRIVE_AXIS_TO_ARM_END.multiply(2) + ", r=" + LENGTH_OF_DRIVE_AXIS_TO_ARM_END + ", c=" + LENGTH_OF_DRIVE_AXIS_TO_ARM_END.multiply(2).multiply(Math.PI));
		System.out.println();
		System.out.println("[Units Of Measure]");
		System.out.println("EncoderRevolution................." + ArmSRXEncoderRevolution.getValue() + " = " + ArmSRXEncoderRevolution.getValue().convertTo(LengthUOM.Inches));
		System.out.println("Max Pulse/Decisecond.............." + ArmMaxDriveRPM.convertTo(ArmSRXMotorPulseRate).getValue() + " = " + ArmSRXMotorPulseRate.getCanonicalValue());
		System.out.println("Max RPMs.........................." + ArmMaxDriveRPM + " = " + ArmMaxDriveRPM.convertTo(ArmSRXEncoderRPS) + " = " + ArmMaxDriveRPM.convertTo(ArmSRXEncoderRPS).getValue() * LENGTH_OF_DRIVE_AXIS_TO_ARM_END.multiply(2).multiply(Math.PI).getValue());
		System.out.println();
		System.out.println("[Calculations]");
		System.out.println("Pulses/encoder rev................" + PULSES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/drive rev.................." + PULSES_PER_DRIVE_REVOLUTION);
		System.out.println("Inches/encoder rev................" + DRIVE_INCHES_PER_PULSE.multiply(PULSES_PER_ENCODER_REVOLUTION));
		System.out.println("Pulses/drive degree..............." + PULSES_PER_ONE_DEGREE);
		System.out.println("Inches/drive degree..............." + DRIVE_INCHES_PER_ONE_DEGREE);
		System.out.println("Pulse Length (Inches)............." + DRIVE_INCHES_PER_PULSE);
		System.out.println("Pulse Length (Canonical).........." + DRIVE_INCHES_PER_PULSE.convertToCanonicalUOM());
		System.out.println("Distance in Second................" + ArmMaxDriveRPM.getLength(TimeUOM.Seconds.create(1)).convertTo(LengthUOM.Inches));
		System.out.println("Distance in Minute................" + ArmMaxDriveRPM.getLength(TimeUOM.Minutes.create(1)).convertTo(LengthUOM.Inches));
 		System.out.println("Arm 100% Rate....................." + ArmRateOnePercentOutputPerOneSecond.create(100) + " = " + ArmRateOnePercentOutputPerOneSecond.create(100).convertTo(RateUOM.FeetPerSecond));
 		System.out.println("Arm 100% Rate....................." + ArmRateOnePercentOutputPerOneSecond.create(100) + " = " + ArmRateOnePercentOutputPerOneSecond.create(100).convertTo(ArmDegreesPerSecond));
 		System.out.println("Arm % Rate Table..................");
 		/*		for(int q = 0; q <= 100; q++) {
		Rate pct = ArmRateOnePercentOutputPerOneSecond.create(q);
		Rate pr = ArmSRXMotorPulseRate.create(pct.convertTo(ArmSRXMotorPulseRate).getValueAsInt());
		Rate rpm = ArmSRXEncoderRPM.create(pct.convertTo(ArmSRXEncoderRPM).getValueAsInt());
		Rate ips = RateUOM.InchesPerSecond.create(pct.convertTo(RateUOM.InchesPerSecond).getValueAsInt());
		System.out.println("                                  " + pct + " = " + pr + " = " + rpm + " = " + ips);	
	}
*/
 		for(int q = 0; q <= 360; q++) {
		Length degrees = ArmDegrees.create(q);
		Length pulses = degrees.convertTo(ArmSRXMotorPulses);
		System.out.println("                                  " + degrees + " = " + pulses);	
	}

	}
}