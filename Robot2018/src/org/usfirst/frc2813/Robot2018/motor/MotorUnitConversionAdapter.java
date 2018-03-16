package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

/*
 * This guy handles scaling of position and rate values based on gearing rations,
 * and compensation for reversed sensor or motor output.
 */
public final class MotorUnitConversionAdapter implements IMotorController {

	private final MotorConfiguration configuration;
	private final IMotorController   controller;
	
	public MotorUnitConversionAdapter(MotorConfiguration configuration, IMotorController controller) {
		this.configuration = configuration;
		this.controller = controller;
	}

	@Override
	public void configure() {
		controller.configure();
	}

	@Override
	public MotorState getState() {
		return controller.getState();
	}

	@Override
	public MotorState getPreviousState() {
		return controller.getPreviousState();
	}

	@Override
	public boolean moveToPosition(Length position) {
		Length sensorPosition = toSensorUnits(position);
		Length scaledPosition = sensorPosition.multiply(configuration.getSensorToDriveScalingFactor());
		if(configuration.getMotorPhaseIsReversed() && !supportsMotorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		if(configuration.getSensorPhaseIsReversed() && !supportsSensorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		return controller.moveToPosition(scaledPosition);
	}

	@Override
	public boolean resetEncoderSensorPosition(Length position) {
		Length sensorPosition = toSensorUnits(position);
		Length scaledPosition = sensorPosition.multiply(configuration.getSensorToDriveScalingFactor());
		if(configuration.getMotorPhaseIsReversed() && !supportsMotorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		if(configuration.getSensorPhaseIsReversed() && !supportsSensorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		return controller.resetEncoderSensorPosition(scaledPosition);
	}

	@Override
	public boolean moveADistance(Length distance) {
		Length sensorPosition = toSensorUnits(distance);
		Length scaledPosition = sensorPosition.multiply(configuration.getSensorToDriveScalingFactor());
		if(configuration.getMotorPhaseIsReversed() && !supportsMotorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		if(configuration.getSensorPhaseIsReversed() && !supportsSensorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		return controller.moveADistance(distance);
	}

	@Override
	public Length readPosition() {
		Length sensorPosition = controller.readPosition();
		Length scaledPosition = sensorPosition.multiply(configuration.getSensorToDriveScalingFactor());
		if(configuration.getMotorPhaseIsReversed() && !supportsMotorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		if(configuration.getSensorPhaseIsReversed() && !supportsSensorInversion()) {
			scaledPosition = scaledPosition.multiply(-1);
		}
		return toSubsystemUnits(scaledPosition);
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
	public boolean move(Direction direction, Rate speedParam) {
		Rate sensorRate = toSensorUnits(speedParam);
		Rate scaledRate = toSensorUnits(sensorRate.multiply(configuration.getSensorToDriveScalingFactor()));
		// NB: In this case we reverse direction, not speed
		if(configuration.getMotorPhaseIsReversed() && !supportsMotorInversion()) {
			direction = direction.getInverse();
		}
		if(configuration.getSensorPhaseIsReversed() && !supportsSensorInversion()) {
			direction = direction.getInverse();
		}		
		return controller.move(direction, scaledRate);
	}

	@Override
	public boolean readLimitSwitch(Direction switchDirection) {
		return controller.readLimitSwitch(switchDirection);
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
	public MotorConfiguration getConfiguration() {
		return controller.getConfiguration();
	}

	/* ----------------------------------------------------------------------------------------------
	 * Units Helpers
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
	
	public String toString() {
		return controller.toString();
	}
}
