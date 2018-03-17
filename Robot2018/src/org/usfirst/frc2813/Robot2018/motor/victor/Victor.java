package org.usfirst.frc2813.Robot2018.motor.victor;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.MotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
//XXX THIS CLASS MAY NOT BE NECESSARY
public class Victor extends AbstractMotorController implements IMotorController {
	private final WPI_VictorSPX spx;
	private ControlMode lastControlMode;
	private double lastControlModeValue;

	public Victor(MotorConfiguration configuration, WPI_VictorSPX spx) {
		super(configuration);
		this.spx = spx;		
	}

	@Override
	public Length readPosition() {
		throw new UnsupportedOperationException("Cannot read position on Victor");
	}

	@Override
	public boolean readLimitSwitch(Direction switchDirection) {
		throw new UnsupportedOperationException("Cannot read limit switch on Victor");
	}

	@Override
	public boolean supportsMotorInversion() {
		return true;
	}

	@Override
	public boolean supportsSensorInversion() {
		return false;
	}

	
	
	@Override
	public void configure() {
		final long SupportedFeatures = MotorConfiguration.Forward|MotorConfiguration.Reverse|MotorConfiguration.ControlRate|MotorConfiguration.ReadRate|MotorConfiguration.NeutralMode|MotorConfiguration.DefaultRate|MotorConfiguration.Disconnected|MotorConfiguration.LimitRate;
		
		long Errors = configuration.getCapabilities() & SupportedFeatures; 
		
		if (!configuration.hasAny(SupportedFeatures)) {
			throw new IllegalArgumentException("You've selected one or more unsupported features: " + MotorConfiguration.listCapabilities(Errors, "", "; ", "."));
		}
		
	}

	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		throw new UnsupportedOperationException("Cannot resent Encoder on Victor");
	}
	//XXX Is this necessary for a WPI_VictorSPX?
	protected boolean executeTransition(MotorState proposedState) {
		ControlMode newControlMode = ControlMode.Disabled;
		double		newControlModeValue = 0;
		switch(proposedState.getOperation()) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newControlModeValue = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			throw new UnsupportedOperationException("Victor Does Not Hold Position");
		case MOVING_TO_POSITION:
			throw new UnsupportedOperationException("Victor Does Not Move to Position");
		case MOVING:
			//FIXME is this the correct math for WPI_VictorSPX?
			newControlModeValue = toMotorUnits(proposedState.getRate()).getValue() * proposedState.getDirection().getMultiplierAsDouble();
			break;
		}
		spx.set(newControlModeValue);
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		return true;
	}

}
