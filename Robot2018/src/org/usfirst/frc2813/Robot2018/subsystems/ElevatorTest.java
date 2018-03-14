package org.usfirst.frc2813.Robot2018.subsystems;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;

import org.junit.jupiter.api.Test;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;

class ElevatorTest  {
	// Software Settings
	private static final Length MINIMUM_POSITION_INCHES = LengthUOM.Inches.create(0.0);
	private static final Length MAXIMUM_POSITION_INCHES = LengthUOM.Inches.create(24.0); // TBD
	private static final Rate   DEFAULT_SPEED_INCHES_PER_SECOND = RateUOM.InchesPerSecond.create(12); // TBD

	// Talon constants
	private static final double PULSES_PER_ENCODER_REVOLUTION = 4096;

	// Hardware Inputs
	private static final Length SHAFT_DIAMETER                = LengthUOM.Inches.create(1.25);
	private static final Length CORD_DIAMETER                 = LengthUOM.Millimeters.create(4);
	private static final Length DRIVE_AXIS_DIAMETER           = SHAFT_DIAMETER.add(CORD_DIAMETER);	
	private static final Length INCHES_PER_ENCODER_REVOLUTION = DRIVE_AXIS_DIAMETER.multiply(Math.PI);

	// Calculations
	private static final double PULSE_PER_INCH                = PULSES_PER_ENCODER_REVOLUTION/INCHES_PER_ENCODER_REVOLUTION.getValue();
	private static final Length INCHES_PER_PULSE              = LengthUOM.Inches.create(1.0/PULSE_PER_INCH);
	private static final Length PULSE_CANONICAL_LENGTH        = INCHES_PER_PULSE.convertToCanonicalUOM();

	// Units Of Length for Elevator
	public static final LengthUOM ElevatorSRXMotorPulses      = new LengthUOM("srxpulse", "srxpulses", "p", PULSE_CANONICAL_LENGTH.getCanonicalValue()); // TODO: I need a pulse to mm number here
	public static final LengthUOM ElevatorSRXMotorRevolution  = new LengthUOM("revolution", "revolutions", "rev", INCHES_PER_ENCODER_REVOLUTION.getCanonicalValue()); 
	
	// Units Of Rate for Elevator
	public static final RateUOM   ElevatorSRXMotorPulseRate   = new RateUOM(ElevatorSRXMotorPulses, TimeUOM.Deciseconds, "Elevator pulses/decisecond");
	public static final RateUOM   ElevatorSRXRPM              = new RateUOM(ElevatorSRXMotorRevolution, TimeUOM.Minutes, "Elevator RPMs");

	@Test
	void test() {
		Elevator.mathReport();
		Elevator.elevatorAxis.dumpDescription();
	}
}
