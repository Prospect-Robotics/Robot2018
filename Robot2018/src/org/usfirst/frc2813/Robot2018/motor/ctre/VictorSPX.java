package org.usfirst.frc2813.Robot2018.motor.ctre;

import java.util.Iterator;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.motor.IMotorConfiguration;
import org.usfirst.frc2813.Robot2018.motor.PID;
import org.usfirst.frc2813.Robot2018.motor.PIDConfiguration;
import org.usfirst.frc2813.Robot2018.motor.PIDProfileSlot;
import org.usfirst.frc2813.Robot2018.motor.state.IMotorState;
import org.usfirst.frc2813.Robot2018.motor.state.MotorStateFactory;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.values.Length;
import org.usfirst.frc2813.units.values.Rate;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

/**
 * A wrapper class to handle an Victor SPX motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public class VictorSPX extends AbstractCTREMotorController implements IMotor {
	private final com.ctre.phoenix.motorcontrol.can.VictorSPX mc;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public VictorSPX(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.VictorSPX mc) {
		super(configuration, mc);
		this.mc = mc;
		initialize();
	}
	
	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		if (direction.isNegative()) {
			return mc.getSensorCollection().isRevLimitSwitchClosed();
		} else {
			return mc.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}
	
	protected void configureHardLimitSwitches() {
		// Set forward hard limits
		if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteForwardHardLimitSwitch)) {
			mc.configForwardLimitSwitchSource(configuration.getRemoteForwardHardLimitSwitchSource(), configuration.getForwardHardLimitSwitchNormal(), configuration.getRemoteForwardHardLimitSwitchDeviceId(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);			
		} else {
			mc.configForwardLimitSwitchSource(RemoteLimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		}
		// Set reverse hard limits
		if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteReverseHardLimitSwitch)) {
			mc.configReverseLimitSwitchSource(configuration.getRemoteReverseHardLimitSwitchSource(), configuration.getReverseHardLimitSwitchNormal(), configuration.getRemoteReverseHardLimitSwitchDeviceId(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);			
		} else {
			mc.configReverseLimitSwitchSource(RemoteLimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);
		}
	}

	@Override
	protected void configureFeedbackSensor(PID pid) {
		// NB: Nothing we can do here...
	}
}
