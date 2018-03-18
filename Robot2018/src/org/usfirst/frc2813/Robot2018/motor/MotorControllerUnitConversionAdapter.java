package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

/*
 * This guy handles scaling of position and rate values based on gearing rations,
 * and compensation for reversed sensor or motor output.
 */
public final class MotorControllerUnitConversionAdapter implements IMotorController {

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
		return fromSensor(controller.getTargetState());
	}

	@Override
	public IMotorState getPreviousTargetState() {
		return fromSensor(controller.getPreviousTargetState());
	}

	@Override
	public boolean moveToAbsolutePosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return controller.moveToAbsolutePosition(toSensorUnitsScalePhase(position));
	}

	@Override
	public boolean resetEncoderSensorPosition(Length position) {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return controller.resetEncoderSensorPosition(toSensorUnitsScalePhase(position));
	}
	
	@Override
	public boolean calibrateSensorInDirection(Direction directio) {
		return controller.calibrateSensorInDirection(directio);
	}

	@Override
	public boolean moveToRelativePosition(Direction direction, Length relativeDistance) {
		// [RELATIVE] Must adjust direction separately, then adjust relative value for units and scale
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return controller.moveToRelativePosition(adjustPhase(direction), toSensorUnitsScale(relativeDistance));
	}

	@Override
	public Length getCurrentPosition() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return fromSensorUnitsScalePhase(controller.getCurrentPosition());
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
		return controller.moveInDirectionAtRate(adjustPhase(direction), toSensorUnitsScale(rate));
	}

	@Override
	public boolean moveInDirectionAtDefaultRate(Direction direction) {
		// [RELATIVE] Must adjust direction
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return controller.moveInDirectionAtDefaultRate(adjustPhase(direction));
	}

	@Override
	public boolean getCurrentLimitSwitchStatus(Direction switchDirection) {
		// [RELATIVE] Must adjust direction
		return controller.getCurrentLimitSwitchStatus(adjustPhase(switchDirection));
	}

	@Override
	public Rate getCurrentRate() {
		// [ABSOLUTE] Must adjust units, scale and phase
		// Since this requires a sensor, units for motor and sensor can be assumed to be the same
		return fromSensorUnitsScalePhase(controller.getCurrentRate());
	}

	@Override
	public Length getCurrentPositionError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		return fromSensorUnitsScalePhase(controller.getCurrentPositionError());
	}

	@Override
	public Rate getCurrentRateError() {
		// [ABSOLUTE] Must adjust units, scale and phase
		return fromSensorUnitsScalePhase(controller.getCurrentRateError());
	}

	@Override
	public boolean getCurrentRateErrorWithin(Rate marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		return getCurrentRateErrorWithin(toSensorUnitsScale(marginOfError).getAbsoluteValue());
	}

	@Override
	public boolean getCurrentPositionErrorWithin(Length marginOfError) {
		if(marginOfError.getValue() < 0) {
			throw new IllegalArgumentException("Margin of error is +/- an absolute value and should not be negative."); 
		}
		// [RELATIVE] Must adjust units, scale
		return getCurrentPositionErrorWithin(toSensorUnitsScale(marginOfError).getAbsoluteValue());
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
	
	// Convert a length to sensor units
	protected Length toSensorUnits(Length l) {
		return l.convertTo(configuration.getNativeSensorLengthUOM());
	}
	// Convert a length to motor units	
	protected Length toMotorUnits(Length l) {
		return l.convertTo(configuration.getNativeMotorLengthUOM());
	}	
	// Convert a length to display units	
	protected Length toSubsystemUnits(Length l) {
		return l.convertTo(configuration.getNativeDisplayLengthUOM());
	}
	// Convert a length to sensor units
	protected Rate toSensorUnits(Rate l) {
		return l.convertTo(configuration.getNativeSensorRateUOM());
	}
	// Convert a length to motor units	
	protected Rate toMotorUnits(Rate l) {
		return l.convertTo(configuration.getNativeMotorRateUOM());
	}
	// Convert a length to display units	
	protected Rate toSubsystemUnits(Rate l) {
		return l.convertTo(configuration.getNativeDisplayRateUOM());
	}

	private final double phaseAdjustment() {
		double a = 1.0;
		if(configuration.getMotorPhaseIsReversed() && !controller.supportsMotorInversion()) {
			a *= -1;
		}
		if(configuration.getSensorPhaseIsReversed() && !controller.supportsSensorInversion()) {
			a *= -1;
		}
		return a;
	}

	private final Direction adjustPhase(Direction d) {
		Direction a = d;
		if(configuration.getMotorPhaseIsReversed() && !controller.supportsMotorInversion()) {
			a = a.getInverse();
		}
		if(configuration.getSensorPhaseIsReversed() && !controller.supportsSensorInversion()) {
			a = a.getInverse();
		}
		return a;
	}

	private final IMotorState fromSensor(IMotorState motorState) {
		return motorState.convertTo(
				configuration.getNativeDisplayLengthUOM(), 
				configuration.getNativeDisplayRateUOM(),
				phaseAdjustment() != 1.0 ? true : false
				);
	}

	private final Length toControllerUnits(Length v) {
		return toSensorUnits(v);
	}
	
	private final Length fromControllerUnits(Length v) {
		return toSubsystemUnits(v);
	}
	
	private final Rate toControllerUnits(Rate v) {
		return toSensorUnits(v);
	}
	
	private final Rate fromControllerUnits(Rate v) {
		return toSubsystemUnits(v);
	}
	
	private final Length toSensorUnitsScale(Length v) {
		return toControllerUnits(v).divide(configuration.getSensorToDriveScalingFactor());
	}
	
	private final Length fromSensorUnitsScale(Length v) {
		return fromControllerUnits(v).multiply(configuration.getSensorToDriveScalingFactor());
	}
	
	private final Rate toSensorUnitsScale(Rate v) {
		return toControllerUnits(v).divide(configuration.getSensorToDriveScalingFactor());
	}
	
	private final Rate fromSensorUnitsScale(Rate v) {
		return fromControllerUnits(v).multiply(configuration.getSensorToDriveScalingFactor());
	}
	
	private final Length toSensorUnitsScalePhase(Length v) {
		return toSensorUnitsScale(v).multiply(phaseAdjustment());
	}
	
	private final Length fromSensorUnitsScalePhase(Length v) {
		return fromSensorUnitsScale(v).multiply(phaseAdjustment());
	}
	
	@SuppressWarnings("unused")
	private final Rate toSensorUnitsScalePhase(Rate v) {
		return toSensorUnitsScale(v).multiply(phaseAdjustment());
	}
	
	private final Rate fromSensorUnitsScalePhase(Rate v) {
		return fromSensorUnitsScale(v).multiply(phaseAdjustment());
	}

	@Override
	public void periodic() {
		controller.periodic();
	}
}
