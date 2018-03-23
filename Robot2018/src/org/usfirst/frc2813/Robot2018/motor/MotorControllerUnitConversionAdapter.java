package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.logging.LogLevel;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;

/*
 * This guy handles scaling of position and rate values based on gearing rations,
 * and compensation for reversed sensor or motor output.
 */
public class MotorControllerUnitConversionAdapter implements IMotorController {

	private final static LogType logLevel = LogType.DEBUG;
	private final IMotorConfiguration configuration;
	private final IMotorController   controller;
	
	public MotorControllerUnitConversionAdapter(
			IMotorConfiguration configuration, 
			IMotorController controller
	) 
	{
		this.configuration = configuration;
		this.controller = controller;
	}

	@Override
	public void configure() {
		controller.configure();
	}

	@Override
	public IMotorState getTargetState() {
		return toDisplay(controller.getTargetState());
	}

	@Override
	public IMotorState getPreviousTargetState() {
		return toDisplay(controller.getPreviousTargetState());
	}
	
	public void error(String function, Object...objects) {
		Logger.printLabelled(logLevel, getClass().getSimpleName() + "." + function + " ", objects);
		(new Throwable()).printStackTrace();
	}
	
	public void log(String function, Object...objects) {
		Logger.printLabelled(logLevel, getClass().getSimpleName() + "." + function + " ", objects);
	}

	@Override
	public boolean moveToAbsolutePosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length newPosition = adjustPhase(toSensorUnitsNoPhaseCorrection(position));
		log("moveToAbsolutePosition", "converted " + position + " to " + newPosition);
		return controller.moveToAbsolutePosition(newPosition);
	}

	@Override
	public boolean resetEncoderSensorPosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length newPosition = adjustPhase(toSensorUnitsNoPhaseCorrection(position));
		log("resetEncoderSensorPosition", "converted " + position + " to " + newPosition);
		return controller.resetEncoderSensorPosition(newPosition);
	}
	
	@Override
	public boolean calibrateSensorInDirection(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		log("calibrateSensorInDirection", "converted " + direction + " to " + newDirection);
		return controller.calibrateSensorInDirection(newDirection);
	}

	@Override
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance) {
		// [RELATIVE] Must adjust direction separately, then adjust relative value for units and scale
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		Length newRelativeDistance = toSensorUnitsNoPhaseCorrection(relativeDistance);
		log("moveToRelativePosition", "converted {" + direction + "," + relativeDistance + "} to {" + newDirection + "," + newRelativeDistance + "}");
		return controller.moveToRelativePosition(newDirection, newRelativeDistance);
	}

	@Override
	public Length getCurrentPosition() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length position = controller.getCurrentPosition();
		Length newPosition = adjustPhase(fromSensorUnitsNoPhaseCorrection(position));
		log("getCurrentPosition", "converted " + position + " to " + newPosition);
		return newPosition;
	}

	@Override
	public boolean disable() {
		return controller.disable();
	}

	@Override
	public boolean holdCurrentPosition() {
		return controller.holdCurrentPosition();
	}

	@Override
	public boolean moveInDirectionAtRate(Direction direction, Rate rate) {		
		// [RELATIVE] Must adjust direction separately, then adjust relative value for units and scale
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		Rate newRate = toSensorUnitsNoPhaseCorrection(rate);
		log("moveInDirectionAtRate", "converted {" + direction + "," + rate + "} to {" + newDirection + "," + newRate + "}");
		return controller.moveInDirectionAtRate(newDirection, newRate);
	}

	@Override
	public boolean moveInDirectionAtDefaultRate(Direction direction) {
		// [RELATIVE] Must adjust direction
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		log("moveInDirectionAtDefaultRate", "converted " + direction + " to " + newDirection);
		return controller.moveInDirectionAtDefaultRate(newDirection);
	}

	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		// [RELATIVE] Must adjust direction
		Direction newDirection = adjustPhase(direction);
		log("getCurrentHardLimitSwitchStatus", "converted " + direction + " to " + newDirection);
		return controller.getCurrentHardLimitSwitchStatus(newDirection);
	}

	@Override
	public Rate getCurrentRate() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Rate rate = controller.getCurrentRate();
		Rate newRate = adjustPhase(fromSensorUnitsNoPhaseCorrection(rate));
		log("getCurrentRate", "converted " + rate + " to " + newRate);
		return newRate;
	}

	@Override
	public Length getCurrentPositionError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		Length error = controller.getCurrentPositionError();
		Length newError = adjustPhase(fromSensorUnitsNoPhaseCorrection(error));
		log("getCurrentPositionError", "converted " + error + " to " + newError);
		return newError;
	}

	@Override
	public Rate getCurrentRateError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		Rate rate = controller.getCurrentRateError();
		Rate newRate = adjustPhase(fromSensorUnitsNoPhaseCorrection(rate));
		log("getCurrentRateError", "converted " + rate + " to " + newRate);
		return newRate;
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		Rate newMarginOfError = toSensorUnitsNoPhaseCorrection(marginOfError).getAbsoluteValue();
		log("getCurrentRateErrorWithin", "converted " + marginOfError + " to " + newMarginOfError);
		return controller.getCurrentRateErrorWithin(newMarginOfError);
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		Length newMarginOfError = toSensorUnitsNoPhaseCorrection(marginOfError).getAbsoluteValue();
		log("getCurrentPositionErrorWithin", "converted " + marginOfError + " to " + newMarginOfError);
		return controller.getCurrentPositionErrorWithin(newMarginOfError);
	}

	@Override
	public boolean supportsMotorInversion() {
		// We do now
		return true;
	}

	@Override
	public boolean supportsSensorInversion() {
		// We do now
		return true;
	}
	
	public String getName() {
		return controller.getName();
	}

	@Override
	public void dumpDiagnostics() {
		controller.dumpDiagnostics();
	}

	@Override
	public String getDiagnostics() {
		return controller.getDiagnostics();
	}

	@Override
	public IMotorConfiguration getConfiguration() {
		return controller.getConfiguration();
	}
	
	public String toString() {
		return controller.toString();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Helpers
	 * ---------------------------------------------------------------------------------------------- */
	
	private final double getPhaseMultiplier() {
		double v = 1.0;
		if(configuration.getMotorPhaseIsReversed() && !controller.supportsMotorInversion()) {
			v *= -1.0;
		}
		if(configuration.getSensorPhaseIsReversed() && !controller.supportsSensorInversion()) {
			v *= -1.0;
		}
		return v;
	}

	private final Rate   adjustPhase(Rate v) { return v.multiply(getPhaseMultiplier()); }

	private final Length adjustPhase(Length v) { return v.multiply(getPhaseMultiplier()); }

	private final Direction adjustPhase(Direction v) {
		if(configuration.getMotorPhaseIsReversed() && !controller.supportsMotorInversion()) {
			v = v.getInverse();
		}
		if(configuration.getSensorPhaseIsReversed() && !controller.supportsSensorInversion()) {
			v = v.getInverse();
		}
		return v;
	}
	/**
	 * Validation helper.  Validate that the value is NOT a sensor 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private final void validateValueIsNotASensorValue(String title, Value value) {
		UOM sensorUOM = (value instanceof org.usfirst.frc2813.units.values.Rate) ? configuration.getNativeSensorRateUOM() : configuration.getNativeSensorLengthUOM();
		if(value.getUOM().equals(sensorUOM)) {
			Logger.printLabelled(LogType.ERROR, title, "ERROR: We do not want " + value.getUOM() + "!");
		}
	}

	/**
	 * Validation helper.  Validate that we did or did not get the units we expected. 
	 */
	private final void validateValueIsASensorValue(String title, Value value) {
		UOM sensorUOM = (value instanceof org.usfirst.frc2813.units.values.Rate) ? configuration.getNativeSensorRateUOM() : configuration.getNativeSensorLengthUOM();
		if(!value.getUOM().equals(sensorUOM)) {
			Logger.printLabelled(LogType.ERROR, title, "ERROR: Expected " + sensorUOM + " but got " + value.getUOM() + ".");
		}
	}
	/**
	 * Converts v to sensor units, multiplies by the sensor to drive scaling factor (gearing), and converts to subsystem units.
	 */
	private final Length toDisplayUnits(Length v) {
		validateValueIsASensorValue("scaleSensorToDrive", v);
		return v
				.convertTo(configuration.getNativeDisplayLengthUOM()); // convert to display units
	}

	/**
	 * Converts v to sensor units, multiplies by the sensor to drive scaling factor (gearing), and converts to subsystem units.
	 */
	private final Rate toDisplayUnits(Rate v) {
		validateValueIsASensorValue("scaleSensorToDrive", v);
		return 
				v
				.convertTo(configuration.getNativeDisplayRateUOM()); // scale for gearing ratio
	}

	/**
	 * Converts v to sensor units, multiplies by the sensor to drive scaling factor (gearing), and converts to subsystem units.
	 */
	private final Length toSensor(Length v) {
		validateValueIsNotASensorValue("scaleDriveToSensor", v);
		return v
				.convertTo(configuration.getNativeSensorLengthUOM()) // convert to sensor units
				;
	}

	private final Rate toSensor(Rate v) {
		validateValueIsNotASensorValue("scaleDriveToSensor", v);
		return v
				.convertTo(configuration.getNativeSensorRateUOM()) // convert to sensor units
				;
	}
	/** Alias for scaleSensorToDrive */
	private final Length toSensorUnitsNoPhaseCorrection(Length v) { return toSensor(v); }
	/** Alias for scaleSEnsorToDrive */
	private final Rate toSensorUnitsNoPhaseCorrection(Rate v) { return toSensor(v); }
	/** Alias for scaleDriveToSensor */
	private final Length fromSensorUnitsNoPhaseCorrection(Length v) { return toDisplayUnits(v); }
	/** Alias for secaleSensorToDrive */
	private final Rate fromSensorUnitsNoPhaseCorrection(Rate v) { return toDisplayUnits(v); }

	@Override
	public void periodic() {
		controller.periodic();
	}

	@Override
	public boolean isDisconnected() {
		return controller.isDisconnected();
	}

	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection) {
		return controller.getCurrentSoftLimitSwitchStatus(switchDirection);
	}
	
	protected IMotorState toDisplay(IMotorState motorState) {
		return motorState.convertTo(getConfiguration().getNativeDisplayLengthUOM(), getConfiguration().getNativeDisplayRateUOM(), getPhaseMultiplier() != 1.0);
	}
}
