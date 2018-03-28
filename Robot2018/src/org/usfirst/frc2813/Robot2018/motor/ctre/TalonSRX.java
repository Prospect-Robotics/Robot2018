package org.usfirst.frc2813.Robot2018.motor.ctre;

import java.util.Iterator;

import org.usfirst.frc2813.Robot2018.motor.AbstractMotorController;
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

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;

/**
 * A wrapper class to handle an Talon SRX motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class TalonSRX extends AbstractCTREMotorController {
	private final com.ctre.phoenix.motorcontrol.can.TalonSRX mc;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public TalonSRX(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.TalonSRX mc) {
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
		// Set forward hard limits.  NB: You won't have both local and remote, so it's ok that they both are writing to clear flag here.
		if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.LocalForwardHardLimitSwitch)) {
			mc.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, configuration.getForwardHardLimitSwitchNormal(), getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
		} else if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteForwardHardLimitSwitch)) {
			mc.configForwardLimitSwitchSource(configuration.getRemoteForwardHardLimitSwitchSource(), configuration.getForwardHardLimitSwitchNormal(), configuration.getRemoteForwardHardLimitSwitchDeviceId(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);			
		} else {
			mc.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		}
		// Set reverse hard limits.  NB: You won't have both local and remote, so it's ok that they both are writing to clear flag here.
		if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.LocalReverseHardLimitSwitch)) {
			mc.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, configuration.getReverseHardLimitSwitchNormal(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);			
		} else if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteReverseHardLimitSwitch)) {
			mc.configReverseLimitSwitchSource(configuration.getRemoteReverseHardLimitSwitchSource(), configuration.getReverseHardLimitSwitchNormal(), configuration.getRemoteReverseHardLimitSwitchDeviceId(), getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
		} else {
			mc.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);
		}
	}
	
	protected void configureFeedbackSensor(PID pid) {
		// Start with primary PID set to relative
		mc.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, pid.getPIDIndex(), getTimeout());
	}
}
