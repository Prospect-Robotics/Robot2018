package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.logging.LogType;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.UOM;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;
import org.usfirst.frc2813.units.values.Value;
import org.usfirst.frc2813.util.Formatter;

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

	protected IMotorController getMotorController() {
		return controller;
	}

	@Override
	public void configure() {
		getMotorController().configure();
	}

	@Override
	public IMotorState getTargetState() {
	//	return toDisplay(getMotorController().getTargetState());
		return getMotorController().getTargetState();
	}

	@Override
	public IMotorState getPreviousTargetState() {
//		return toDisplay(getMotorController().getPreviousTargetState());
		return getMotorController().getPreviousTargetState();
	}

	public void error(String function, Object...objects) {
		Logger.print(logLevel, String.format("%s.%s %s", getClass().getSimpleName(), function, objects));
		(new Throwable()).printStackTrace();
	}

	public void log(String function, Object...objects) {
		Logger.print(logLevel, String.format("%s.%s %s", getClass().getSimpleName(), function, objects));
	}
	
	public void logf(String function, String format, Object...objects) {
		
		Logger.print(logLevel, Formatter.concat(getClass().getSimpleName(), '.', function, ' ', String.format(format, objects)));
	}

	@Override
	public boolean moveToAbsolutePosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length newPosition = adjustPhase(toSensorUnitsNoPhaseCorrection(position));
		log("moveToAbsolutePosition", String.format("converted %s to %s", position, newPosition));
		return getMotorController().moveToAbsolutePosition(newPosition);
	}

	@Override
	public boolean resetEncoderSensorPosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length newPosition = adjustPhase(toSensorUnitsNoPhaseCorrection(position));
		logf("resetEncoderSensorPosition", "converted %s to %s", position, newPosition);
		return getMotorController().resetEncoderSensorPosition(newPosition);
	}
	
	@Override
	public boolean calibrateSensorInDirection(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		logf("calibrateSensorInDirection", "converted %s to %s", direction, newDirection);
		return getMotorController().calibrateSensorInDirection(newDirection);
	}

	@Override
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance) {
		// [RELATIVE] Must adjust direction separately, then adjust relative value for units and scale
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		Length newRelativeDistance = toSensorUnitsNoPhaseCorrection(relativeDistance);
		logf("moveToRelativePosition", "converted {%s, %s} to {%s, %s}", direction, relativeDistance, newDirection, newRelativeDistance);
		return getMotorController().moveToRelativePosition(newDirection, newRelativeDistance);
	}

	@Override
	public Length getCurrentPosition() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Length position = getMotorController().getCurrentPosition();
		Length newPosition = adjustPhase(toDisplayUnitsNoPhaseCorrection(position));
		logf("getCurrentPosition", "converted %s to %s", position, newPosition);
		return newPosition;
	}

	@Override
	public boolean disable() {
		return getMotorController().disable();
	}

	@Override
	public boolean holdCurrentPosition() {
		return getMotorController().holdCurrentPosition();
	}

	@Override
	public boolean moveInDirectionAtRate(Direction direction, Rate rate) {		
		// [RELATIVE] Must adjust direction separately, then adjust relative value for units and scale
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		Rate newRate = toSensorUnitsNoPhaseCorrection(rate);
		log("moveInDirectionAtRate", String.format("converted {%s, %s} to {%s, %s}", direction, rate, newDirection, newRate));
		return getMotorController().moveInDirectionAtRate(newDirection, newRate);
	}

	@Override
	public boolean moveInDirectionAtDefaultRate(Direction direction) {
		// [RELATIVE] Must adjust direction
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Direction newDirection = adjustPhase(direction);
		log("moveInDirectionAtDefaultRate", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().moveInDirectionAtDefaultRate(newDirection);
	}

	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		// [RELATIVE] Must adjust direction
		Direction newDirection = adjustPhase(direction);
		log("getCurrentHardLimitSwitchStatus", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().getCurrentHardLimitSwitchStatus(newDirection);
	}

	@Override
	public Rate getCurrentRate() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		Rate rate = getMotorController().getCurrentRate();
		Rate newRate = adjustPhase(toDisplayUnitsNoPhaseCorrection(rate));
		log("getCurrentRate", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}

	@Override
	public Length getCurrentPositionError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		Length error = getMotorController().getCurrentPositionError();
		Length newError = adjustPhase(toDisplayUnitsNoPhaseCorrection(error));
		log("getCurrentPositionError", String.format("converted %s to %s", error, newError));
		return newError;
	}

	@Override
	public Rate getCurrentRateError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		Rate rate = getMotorController().getCurrentRateError();
		Rate newRate = adjustPhase(toDisplayUnitsNoPhaseCorrection(rate));
		log("getCurrentRateError", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		Rate newMarginOfError = toSensorUnitsNoPhaseCorrection(marginOfError).getAbsoluteValue();
		log("getCurrentRateErrorWithin", String.format("converted %s to %s", marginOfError, newMarginOfError));
		return getMotorController().getCurrentRateErrorWithin(newMarginOfError);
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		Length newMarginOfError = toSensorUnitsNoPhaseCorrection(marginOfError).getAbsoluteValue();
		log("getCurrentPositionErrorWithin", String.format("converted %s to %s", marginOfError, newMarginOfError));
		return getMotorController().getCurrentPositionErrorWithin(newMarginOfError);
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
		return getMotorController().getName();
	}

	@Override
	public void dumpDiagnostics() {
		getMotorController().dumpDiagnostics();
	}

	@Override
	public String getDiagnostics() {
		return getMotorController().getDiagnostics();
	}

	@Override
	public IMotorConfiguration getConfiguration() {
		return getMotorController().getConfiguration();
	}
	
	public String toString() {
		return getMotorController().toString();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Helpers
	 * ---------------------------------------------------------------------------------------------- */
	
	private final double getPhaseMultiplier() {
		double v = 1.0;
		if(configuration.getMotorPhaseIsReversed() && !getMotorController().supportsMotorInversion()) {
			v *= -1.0;
		}
		if(configuration.getSensorPhaseIsReversed() && !getMotorController().supportsSensorInversion()) {
			v *= -1.0;
		}
		return v;
	}

	private final Rate   adjustPhase(Rate v) { return v.multiply(getPhaseMultiplier()); }

	private final Length adjustPhase(Length v) { return v.multiply(getPhaseMultiplier()); }

	private final Direction adjustPhase(Direction v) {
		if(configuration.getMotorPhaseIsReversed() && !getMotorController().supportsMotorInversion()) {
			v = v.getInverse();
		}
		if(configuration.getSensorPhaseIsReversed() && !getMotorController().supportsSensorInversion()) {
			v = v.getInverse();
		}
		return v;
	}
	/**
	 * Validation helper.  Validate that the value is NOT a sensor
	 * @param title A title to prefix the message
	 * @param value A value to check and make sure it IS NOT in sensor units 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void validateValueIsNotASensorValue(String title, Value value) {
		UOM sensorUOM = (value instanceof org.usfirst.frc2813.units.values.Rate) ? configuration.getNativeSensorRateUOM() : configuration.getNativeSensorLengthUOM();
		if(value.getUOM().equals(sensorUOM)) {
			Logger.printLabelled(LogType.ERROR, title, String.format("ERROR: We do not want %s!"));
		}
	}

	/**
	 * Validation helper.  Validate that we did or did not get the units we expected. 
	 * @param title A title to prefix the message
	 * @param value A value to check and make sure it IS in sensor units 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private final void validateValueIsASensorValue(String title, Value value) {
		UOM sensorUOM = (value instanceof org.usfirst.frc2813.units.values.Rate) ? configuration.getNativeSensorRateUOM() : configuration.getNativeSensorLengthUOM();
		if(!value.getUOM().equals(sensorUOM)) {
			Logger.printLabelled(LogType.ERROR, title, String.format("ERROR: Expected %s but got %s", sensorUOM, value.getUOM()));
		}
	}
	/**
	 * Converts v to display units
	 * @param v The units to convert
	 * @return The converted units
	 */
	private final Length toDisplayUnitsNoPhaseCorrection(Length v) {
		validateValueIsASensorValue("scaleSensorToDrive", v);
		return v
				.convertTo(configuration.getNativeDisplayLengthUOM()); // convert to display units
	}

	/**
	 * Converts v to display units
	 * @param v The units to convert
	 * @return The converted units
	 */
	private final Rate toDisplayUnitsNoPhaseCorrection(Rate v) {
		validateValueIsASensorValue("scaleSensorToDrive", v);
		return 
				v
				.convertTo(configuration.getNativeDisplayRateUOM()); // scale for gearing ratio
	}

	/**
	 * Converts v to sensor units
	 * @param v The units to convert
	 * @return The converted units
	 */
	private final Length toSensorUnitsNoPhaseCorrection(Length v) {
		validateValueIsNotASensorValue("scaleDriveToSensor", v);
		return v
				.convertTo(configuration.getNativeSensorLengthUOM()) // convert to sensor units
				;
	}

	/**
	 * Converts v to sensor units
	 * @param v The units to convert
	 * @return The converted units
	 */
	private final Rate toSensorUnitsNoPhaseCorrection(Rate v) {
		validateValueIsNotASensorValue("scaleDriveToSensor", v);
		return v
				.convertTo(configuration.getNativeSensorRateUOM()) // convert to sensor units
				;
	}

	@Override
	public void periodic() {
		getMotorController().periodic();
	}

	@Override
	public boolean isDisconnected() {
		return getMotorController().isDisconnected();
	}

	@Override
	public boolean getCurrentSoftLimitSwitchStatus(Direction switchDirection) {
		return getMotorController().getCurrentSoftLimitSwitchStatus(switchDirection);
	}
	
	protected IMotorState toDisplay(IMotorState motorState) {
		return motorState.convertTo(getConfiguration().getNativeDisplayLengthUOM(), getConfiguration().getNativeDisplayRateUOM(), getPhaseMultiplier() != 1.0);
	}
	@Override
	public Length getPhysicalLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		Length length = getMotorController().getPhysicalLimit(newDirection);
		Length newLength = toDisplayUnitsNoPhaseCorrection(length);
		log("getPhysicalLimit", String.format("converted %s to %s and %s to %s", direction, newDirection, length, newLength));
		return newLength;
	}
	@Override
	public boolean getHasHardLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		log("getHasHardLimit", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().getHasHardLimit(newDirection);
	}
	@Override
	public boolean getHasSoftLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		log("getHasSoftLimit", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().getHasSoftLimit(newDirection);
	}
	@Override
	public boolean getHasHardOrSoftLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		log("getHasHardOrSoftLimit", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().getHasHardOrSoftLimit(newDirection);
	}
	
	@Override
	public Length getSoftLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		Length length = getMotorController().getSoftLimit(newDirection);
		Length newLength = toDisplayUnitsNoPhaseCorrection(length);
		log("getSoftLimit", String.format("converted %s to %s and %s to %s", direction, newDirection, length, newLength));
		return newLength;
	}
	@Override
	public Rate getMinimumForwardRate() {
		Rate rate = getMotorController().getMinimumForwardRate();
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMinimumForwardRate", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}
	@Override
	public Rate getMaximumForwardRate() {
		Rate rate = getMotorController().getMaximumForwardRate();
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMaximumForwardRate", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}
	@Override
	public Rate getMaximumReverseRate() {
		Rate rate = getMotorController().getMaximumReverseRate();
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMaximumReverseRate", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}
	@Override
	public Rate getMinimumReverseRate() {
		Rate rate = getMotorController().getMinimumReverseRate();
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMinimumReverseRate", String.format("converted %s to %s", rate, newRate));
		return newRate;
	}
	@Override
	public Rate getMaximumRate(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		Rate rate = getMotorController().getMaximumRate(newDirection);
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMaximumRate", String.format("converted %s to %s and %s to %s", direction, newDirection, rate, newRate));
		return newRate;
	}
	@Override
	public Rate getMinimumRate(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		Rate rate = getMotorController().getMinimumRate(newDirection);
		Rate newRate = toDisplayUnitsNoPhaseCorrection(rate);
		log("getMinimumRate", String.format("converted %s to %s and %s to %s", direction, newDirection, rate, newRate));
		return newRate;
	}
	@Override
	public Length getHardLimit(Direction direction) {
		Direction newDirection = adjustPhase(direction);
		Length length = getMotorController().getHardLimit(newDirection);
		Length newLength = toDisplayUnitsNoPhaseCorrection(length);
		log("getHardLimit", String.format("converted %s to %s and %s to %s", direction, newDirection, length, newLength));
		return newLength;
	}
	
	@Override
	public boolean isHardLimitExceeded(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isHardLimitExceeded", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isHardLimitExceeded(newDirection); 
	}
	@Override
	public boolean isHardLimitReached(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isHardLimitReached", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isHardLimitReached(newDirection); 
	}
	@Override
	public boolean isHardLimitNeedingCalibration(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isHardLimitNeedingCalibration", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isHardLimitNeedingCalibration(newDirection); 
	}
	@Override
	public boolean isSoftLimitExceeded(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isSoftLimitExceeded", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isSoftLimitExceeded(newDirection); 
	}
	@Override
	public boolean isSoftLimitReached(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isSoftLimitReached", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isSoftLimitReached(newDirection); 
	}
	@Override
	public boolean isPhysicalLimitExceeded(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isPhysicalLimitExceeded", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isPhysicalLimitExceeded(newDirection); 
	}
	@Override
	public boolean isPhysicalLimitReached(Direction direction) { 
		Direction newDirection = adjustPhase(direction);
		log("isPhysicalLimitReached", String.format("converted %s to %s", direction, newDirection));
		return getMotorController().isPhysicalLimitReached(newDirection); 
	}
}
