package org.usfirst.frc2813.Robot2018.motor.victor;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotorController;
import org.usfirst.frc2813.Robot2018.motor.MotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.MotorOperation;
import org.usfirst.frc2813.Robot2018.motor.MotorState;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;

public class Victor extends AbstractMotorController implements IMotorController {
	private final VictorSPX spx;

	public Victor(MotorConfiguration configuration, VictorSPX spx) {
		super(configuration);
		this.spx = spx;		
	}

	@Override
	public Length readPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean readLimitSwitch(Direction switchDirection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsMotorInversion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean supportsSensorInversion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void configure() {
		final int SupportedFeatures = MotorConfiguration.Forward|MotorConfiguration.Reverse|MotorConfiguration.ControlRate|MotorConfiguration.ReadRate|MotorConfiguration.NeutralMode|MotorConfiguration.DefaultRate;
		
		int Errors = configuration.getCapabilities() & SupportedFeatures; 
		
		if (!configuration.hasAny(SupportedFeatures)) {
			throw new IllegalArgumentException("You've selected one or more unsupported features: " + MotorConfiguration.listCapabilities(, "", "; ", suffix));
		}
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean executeTransition(MotorState proposedState) {
		// TODO Auto-generated method stub
		return false;
	}
}
