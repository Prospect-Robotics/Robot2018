package org.usfirst.frc2813.Robot2018.subsystems;

import java.util.Iterator;
import java.util.logging.Logger;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Elevator.ElevatorHoldPosition;
import org.usfirst.frc2813.Robot2018.motor.MotorControllerState;
import org.usfirst.frc2813.Robot2018.motor.Talon;
import org.usfirst.frc2813.Robot2018.motor.TalonProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.TalonSensorPhase;
import org.usfirst.frc2813.Robot2018.motor.axis.AxisConfiguration;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.SystemOfMeasurement;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.uom.RateUOM;
import org.usfirst.frc2813.units.uom.TimeUOM;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;

/**
 * Elevator subsystem. Moves up and down.
 *
 * Elevator can halt, move to an exact position or in a direction.
 * Speed can be set separately.
 * position is in inches.
 * speed is in inches per second.
 */
public class Elevator extends SubsystemPositionDirectionSpeed {
	private Talon motorController;
	public static final ElevatorAxisConfiguration elevatorAxis = new ElevatorAxisConfiguration();

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
	public static final RateUOM   ElevatorSRXMotorPulseRate   = new RateUOM(ElevatorSRXMotorPulses, TimeUOM.Deciseconds, "Elevator pulses/100ms");
	public static final RateUOM   ElevatorSRXRPM              = new RateUOM(ElevatorSRXMotorRevolution, TimeUOM.Minutes, "Elevator RPMs");
	
	public Elevator() {
		super();
		motorController = new Talon(RobotMap.srxElevator, Logger.getLogger("ElevatorMC"));		
		// Configure the limits for DOWN
		motorController.configureHardLimitSwitch(Direction.DOWN, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
		motorController.disableSoftLimitSwitch(Direction.DOWN);
		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, true);

// HW BUG WORKAROUND
motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.UP, true);
//		// Configure the limits for UP
//		motorController.configureHardLimitSwitch(Direction.UP, LimitSwitchSource.Deactivated); // Ignore any short in the wiring.  The default is enabled.
//		motorController.setHardLimitSwitchClearsPositionAutomatically(Direction.DOWN, false);
// HW BUG WORKAROUND
		motorController.configureSoftLimitSwitch(Direction.UP, (int)(MAX_POSITION * PULSES_PER_UNIT_POSITION));
	
		// Configure the PID profiles
  	    motorController.configurePID(TalonProfileSlot.HoldingPosition, 0.8, 0.0, 0.0);
	    motorController.configurePID(TalonProfileSlot.Moving, 0.75, 0.01, 40.0);
	}
	
	public static void mathReport() {
		System.out.println();
		System.out.println("[Software Settings]");
		System.out.println("Range.............................{" + MINIMUM_POSITION_INCHES + ".." + MAXIMUM_POSITION_INCHES + "}");
		System.out.println("Default Speed....................." + DEFAULT_SPEED_INCHES_PER_SECOND);
		System.out.println("Default Speed (RPM)..............." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(ElevatorSRXRPM));
		System.out.println("Default Speed (SRX)..............." + DEFAULT_SPEED_INCHES_PER_SECOND.convertTo(ElevatorSRXMotorPulseRate));
		System.out.println();
		System.out.println("[Robot Measurements]");
		System.out.println("SHAFT.............................d=" + SHAFT_DIAMETER + ", r=" + SHAFT_DIAMETER.divide(2) + ", c=" + SHAFT_DIAMETER.multiply(Math.PI));
		System.out.println("CORD..............................d=" + CORD_DIAMETER + ", r=" + CORD_DIAMETER.divide(2) + ", c=" + CORD_DIAMETER.multiply(Math.PI));
		System.out.println("DRIVE.............................d=" + DRIVE_AXIS_DIAMETER + ", r=" + DRIVE_AXIS_DIAMETER.divide(2) + ", c=" + DRIVE_AXIS_DIAMETER.multiply(Math.PI));
		System.out.println();
		System.out.println("[Calculations]");
		System.out.println("Pulses/rev........................" + PULSES_PER_ENCODER_REVOLUTION);
		System.out.println("Inches/rev........................" + INCHES_PER_ENCODER_REVOLUTION);
		System.out.println("Pulses/inch......................." + PULSE_PER_INCH);
		System.out.println("Pulse Length (Inches)............." + INCHES_PER_PULSE);
		System.out.println("Pulse Length (Canonical).........." + PULSE_CANONICAL_LENGTH);
		System.out.println();
		System.out.println("[Units Of Measure]");
		System.out.println("Elevator SRX Revolution..........." + ElevatorSRXMotorRevolution.getValue() + " = " + ElevatorSRXMotorRevolution.getCanonicalValue());
		System.out.println("Elevator SRX Rate................." + ElevatorSRXMotorPulseRate.getValue() + " = " + ElevatorSRXMotorPulseRate.getCanonicalValue());
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
	}
	public boolean readLimitSwitch(Direction switchDirection) {
		return motorController.readLimitSwitch(switchDirection);
	}
	/*
	 * Returns units of sensor axis
	 */
	protected int readControllerPosition() {
		return motorController.readPosition();
	}
	/*
	 * Converts to units of sensor axis before sending to motor controller
	 */
	protected void setControllerPosition(int positionParam) {
		/* Convert position in UI units to the units expected by this arm. */
		motorController.setPosition(positionParam); //(int)Math.round(positionParam.convertTo(elevatorAxis.getNativeMotorLengthUOM()).getValue()));
	}

	protected MotorControllerState readMotorControllerState() {
		return motorController.getState();
	}

	protected void setControllerDirectionAndSpeed(Direction direction, double speedParam) {
		motorController.move(direction, speedParam); // Math.round(speedParam.convertTo(elevatorAxis.getNativeMotorRateUOM()).getValue()));
	}

	protected void disableController() {
		motorController.disable();
	}

	protected void holdControllerPosition() {
		motorController.holdCurrentPosition();
	}

	// initializes elevator in static position
	@Override
	public void initDefaultCommand() {
		// Set to hold position by default
		setDefaultCommand(new ElevatorHoldPosition());
	}

	@Override
	public void periodic() {}
	
	/// Dump debug output
	public void dumpState() {
		super.dumpState();
		motorController.dumpState();
	}
	
	// Just testing
	public void encoderRelativePositionTestingMode() {
		disable();
		motorController.setEncoderPosition(0);
		disable();
		dumpState();
	}
}
