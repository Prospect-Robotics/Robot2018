package org.usfirst.frc2813.Robot2018.motor.victor;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
//XXX THIS CLASS MAY NOT BE NECESSARY
public class Victor extends AbstractMotorController implements IMotor {
	private final WPI_VictorSPX spx;
	@SuppressWarnings("unused")
	private ControlMode lastControlMode;
	@SuppressWarnings("unused")
	private double lastControlModeValue;

	public Victor(IMotorConfiguration configuration, WPI_VictorSPX spx) {
		super(configuration);
		this.spx = spx;		
	}

	@Override
	public Length getCurrentPosition() {
		throw new UnsupportedOperationException("Cannot read position on Victor");
	}

	@Override
	public boolean getCurrentLimitSwitchStatus(Direction switchDirection) {
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
		final long SupportedFeatures = IMotorConfiguration.Forward|IMotorConfiguration.Reverse|IMotorConfiguration.ControlRate|IMotorConfiguration.ReadRate|IMotorConfiguration.NeutralMode|IMotorConfiguration.DefaultRate|IMotorConfiguration.Disconnected|IMotorConfiguration.LimitRate;
		
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
	protected boolean executeTransition(IMotorState proposedState) {
		ControlMode newControlMode = ControlMode.Disabled;
		double		newControlModeValue = 0;
		switch(proposedState.getOperation()) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newControlModeValue = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			throw new UnsupportedOperationException("Victor Does Not Hold Position");
		case MOVING_TO_ABSOLUTE_POSITION:
			throw new UnsupportedOperationException("Victor Does Not Move to Absolute Position");
		case MOVING_TO_RELATIVE_POSITION:
			throw new UnsupportedOperationException("Victor Does Not Move to Relative Position");
		case MOVING_IN_DIRECTION_AT_RATE:
			//FIXME is this the correct math for WPI_VictorSPX?
			newControlModeValue = toMotorUnits(proposedState.getTargetRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
			break;
		}
		spx.set(newControlModeValue);
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		return true;
	}

	@Override
	public Rate getCurrentRate() {
		return null;
	}

}
