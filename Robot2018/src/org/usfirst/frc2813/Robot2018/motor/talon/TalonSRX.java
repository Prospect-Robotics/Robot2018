package org.usfirst.frc2813.Robot2018.motor.talon;

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

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

/**
 * A wrapper class to handle an Talon SRX motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class TalonSRX extends AbstractMotorController {
	private final com.ctre.phoenix.motorcontrol.can.TalonSRX mc;
	
	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */
	
	// Last arg sent for controlMode
	private PIDProfileSlot   lastSlot                 = PIDProfileSlot.HoldingPosition;
	// last control mode parameter
	protected ControlMode    lastControlMode          = ControlMode.Position; // Remember last assigned control mode, help us implement state transitions
	private double           lastControlModeValue     = 0;

	/* ----------------------------------------------------------------------------------------------
	 * Constants
	 * ---------------------------------------------------------------------------------------------- */
	
	// NB: This is not yet in the firmware apparently, so enable this when it's ready
	public static final boolean AUXILLIARY_PID_SUPPORTED = false;
	// During initialization we use 10ms timeout to setup commands
	public static final int SETUP_CONFIGURATION_COMMAND_TIMEOUT_MS = 10;
	// During operation, we use 0ms timeout to avoid stalling on transient bus
	public static final int RUNNING_CONFIGURATION_COMMAND_TIMEOUT_MS = 0;
	// We will always use the same PID index
	public static final PID currentPID = PID.Primary;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public TalonSRX(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.TalonSRX mc) {
		super(configuration);
		this.mc = mc;
		initialize();
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * Public IMotorController functions
	 * ---------------------------------------------------------------------------------------------- */


	@Override
	public boolean supportsMotorInversion() {
		return true;
	}

	@Override
	public boolean supportsSensorInversion() {
		return true;
	}
	
	@Override
	public boolean getCurrentHardLimitSwitchStatus(Direction direction) {
		if (direction.isNegative()) {
			return mc.getSensorCollection().isRevLimitSwitchClosed();
		} else {
			return mc.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}
	
	@Override
	public final Length getCurrentPosition() {
		int raw = mc.getSelectedSensorPosition(currentPID.getPIDIndex());
		Length length = configuration.getNativeSensorLengthUOM().create(raw); 
//		Logger.info("readPosition " + raw + " --> " + length);
		return length;
	}
	
	/* ----------------------------------------------------------------------------------------------
	 * API To Base Class
	 * ---------------------------------------------------------------------------------------------- */
	
	@Override
	protected boolean resetEncoderSensorPositionImpl(Length sensorPosition) {
		boolean result = true;
		if(!resetEncoderSensorPosition(PID.Primary, sensorPosition)) {
			result = false;
		}
		return result;
	}

	protected boolean executeTransition(IMotorState proposedState) {
		// New PID/Slot are almost always maintain
		PIDProfileSlot newSlotIndex        = lastSlot;
		ControlMode      newControlMode      = ControlMode.Disabled;
		double           newControlModeValue = 0;

		// Figure out the new control mode and argument
		switch(proposedState.getOperation()) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newControlModeValue = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			newControlMode = ControlMode.Position;
			newControlModeValue = getCurrentPosition().getValue();
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			newControlMode = ControlMode.Position;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_TO_RELATIVE_POSITION:
			newControlMode = ControlMode.Position;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			newControlMode = ControlMode.Velocity;
			newControlModeValue = toMotorUnits(proposedState.getTargetRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
			break;
		case CALIBRATING_SENSOR_IN_DIRECTION:
			newControlMode = ControlMode.Velocity;
			if(!getCurrentHardLimitSwitchStatus(proposedState.getTargetDirection())) {
				newControlModeValue = toMotorUnits(configuration.getDefaultRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
			}
		default:
			break;
		}
		// Determine the appropriate PID profile to use
		newSlotIndex   = getAppropriatePIDProfileSlot(proposedState);
		// Select the profile to use for the control loop (this is almost certainly going to be closed loop)
		mc.selectProfileSlot(newSlotIndex.getProfileSlotIndex(), currentPID.getPIDIndex());
		// Set the control mode
		mc.set(newControlMode, newControlModeValue);
		
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		this.lastSlot = newSlotIndex;
		return true;
	}

	/* ----------------------------------------------------------------------------------------------
	 * Internal Helper Functions - Motor Specific
	 * ---------------------------------------------------------------------------------------------- */
	
	private int getTimeout() {
		switch(currentState.getOperation()) {
		case HOLDING_CURRENT_POSITION:
		case MOVING_IN_DIRECTION_AT_RATE:
		case MOVING_TO_ABSOLUTE_POSITION:
			return RUNNING_CONFIGURATION_COMMAND_TIMEOUT_MS;
		case DISABLED:
		default:
			return SETUP_CONFIGURATION_COMMAND_TIMEOUT_MS;
		}
	}
	
	/*
	 * Toggle whether the hard limit resets the position sensor automatically.  Default is off.
	 */
	private void setHardLimitSwitchClearsPositionAutomatically(Direction direction, boolean clearPositionAutomatically) {
			ParamEnum parameter = direction.isNegative() ? ParamEnum.eClearPositionOnLimitR : ParamEnum.eClearPositionOnLimitF;
			mc.configSetParameter(
					parameter, 
					clearPositionAutomatically ? 1 : 0, 
					0 /* unused */, 
					0 /* unused */, 
					getTimeout());
	}
	
	private boolean resetEncoderSensorPosition(PID pid, Length sensorPosition) {
		if(pid.equals(PID.Auxilliary) && !AUXILLIARY_PID_SUPPORTED) {
			Logger.warning("WARNING: correctEncoderSensorPositions will not be run on auxilliary PID loop.  Firmware support for aux PID is not released yet.");
			return true;
		}
		// Select relative & reset
		int rawValue = toSensorUnits(sensorPosition).getValueAsInt();
		Logger.debug(this + " setting selected sensor " + pid.getPIDIndex() + " to " + rawValue + " (Requested " + sensorPosition + ").");
		mc.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, pid.getPIDIndex(), getTimeout());
		mc.setSelectedSensorPosition(rawValue, pid.getPIDIndex(), getTimeout());
		int readBack = mc.getSelectedSensorPosition(pid.getPIDIndex());
		if(Math.abs(readBack - rawValue) > SENSOR_RESET_TOLERANCE_PULSES) {
			Logger.error(this + " failed setting selected sensor " + pid.getPIDIndex() + " to " + rawValue + " (Requested " + sensorPosition + ") - got " + readBack + " instead.");
			mc.setSelectedSensorPosition(rawValue, pid.getPIDIndex(), getTimeout());
			return false;
		}
		return true;
	}
	/*
	 * Configure PID values
	 */
	@SuppressWarnings("unused")
	private void configurePID(PIDProfileSlot profileSlot, double p, double i, double d) {
		configurePID(profileSlot, p, i, d, 0);
	}
	
	/*
	 * Configure PID values
	 */
	public void configurePID(PIDProfileSlot profileSlot, double p, double i, double d, double f) {
		mc.config_kF(profileSlot.getProfileSlotIndex(), f, getTimeout());
		mc.config_kP(profileSlot.getProfileSlotIndex(), p, getTimeout());
		mc.config_kI(profileSlot.getProfileSlotIndex(), i, getTimeout());
		mc.config_kD(profileSlot.getProfileSlotIndex(), d, getTimeout());
		// I'm not sure if someone has to call selectProfileSlot to reload the values or if it's automatically looking at the right ones (guessing the latter)
	}
	
	public String getDiagnotics() {
		return super.getDiagnostics()
				+ (configuration.hasAll(IMotorConfiguration.Disconnected) ? "" :
				  " [ControlMode=" + lastControlMode
				+ ", ControlModeValue=" + lastControlModeValue
				+ ", SlotIndex=" + lastSlot
				+ ", PIDIndex=" + currentPID
				+ "]");
	}
	
	@Override
	public void configure() {
		// Start disabled
		changeState(MotorStateFactory.createDisabled(this));
		
		// set the peak and nominal outputs, 12V means full
		mc.configNominalOutputForward(0, getTimeout());
		mc.configNominalOutputReverse(0, getTimeout());
		mc.configPeakOutputForward(1, getTimeout());
		mc.configPeakOutputReverse(-1, getTimeout());
		
//		correctRelativeEncodersFromAbsolute();
		
		// Reset PID controllers
		configurePID(PIDProfileSlot.ProfileSlot0, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot1, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot2, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot3, 0, 0, 0, 0);

		// Start with primary PID set to relative
		mc.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, currentPID.getPIDIndex(), getTimeout());
		/*
		 * set the allowable closed-loop error, Closed-Loop output will be neutral
		 * within this range. See Table in Section 17.2.1 for native units per rotation.
		 */
		mc.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot0.getProfileSlotIndex(), 0, getTimeout());
		mc.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot1.getProfileSlotIndex(), 0, getTimeout());
		mc.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot2.getProfileSlotIndex(), 0, getTimeout());
		mc.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot3.getProfileSlotIndex(), 0, getTimeout());
		// Disable clearing position on quad index, we don't support/use it and this restores SRX default.
		mc.configSetParameter(ParamEnum.eClearPositionOnQuadIdx, 0 /* disabled */, 0 /* unused */, 0 /* unused */, getTimeout());
		
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
		// Set forward soft limit
		if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.ForwardSoftLimitSwitch)) {
			mc.configForwardSoftLimitEnable(true, getTimeout());
			mc.configForwardSoftLimitThreshold(getForwardSoftLimit().getValueAsInt(), getTimeout());
		} else {
			mc.configForwardSoftLimitThreshold(0, getTimeout()); // Clear it so it's not confusing us in RoboRio Web UI
			mc.configForwardSoftLimitEnable(false, getTimeout());
		}
		// Set reverse soft limit
		if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.ReverseSoftLimitSwitch)) {
			mc.configReverseSoftLimitEnable(true, getTimeout());
			mc.configReverseSoftLimitThreshold(getReverseSoftLimit().getValueAsInt(), getTimeout());
		} else {
			mc.configReverseSoftLimitThreshold(0, getTimeout()); // Clear it so it's not confusing us in RoboRio Web UI
			mc.configReverseSoftLimitEnable(false, getTimeout());
		}
		mc.setSensorPhase(configuration.getSensorPhaseIsReversed());
		mc.setInverted(configuration.getMotorPhaseIsReversed());
		// Set neutral mode, if specified - otherwise leave it with pre-configured value
		if(configuration.hasAny(IMotorConfiguration.NeutralMode)) {
			mc.setNeutralMode(configuration.getNeutralMode());
		} else { 
			mc.setNeutralMode(NeutralMode.EEPROMSetting); 
		}

		// Configure the PID profiles
		Iterator<PIDConfiguration> pidProfiles = configuration.getPIDConfigurations().iterator();
		while(pidProfiles.hasNext()) {
			PIDConfiguration pidConfiguration = pidProfiles.next();
	 	    configurePID(
	 	    		pidConfiguration.getPIDProfileSlot(),
	 	    		pidConfiguration.getP(),
	 	    		pidConfiguration.getI(),
	 	    		pidConfiguration.getD(),
	 	    		pidConfiguration.getF());
	 	    Logger.info(this + " loaded : " + pidConfiguration);
		}
	}

	public String toString() {
		return configuration.getName() + "." + this.getClass().getSimpleName();  
	}

	@Override
	public Rate getCurrentRate() {
		return configuration.getNativeSensorRateUOM().create(mc.getSelectedSensorVelocity(currentPID.getPIDIndex()));
	}

	@Override
	protected PIDProfileSlot getPIDProfileSlot() {
		return lastSlot; 
	}

	@Override
	protected boolean setPIDProfileSlot(PIDProfileSlot slot) {
		mc.selectProfileSlot(slot.getProfileSlotIndex(), currentPID.getPIDIndex());
		this.lastSlot = slot;
		return true;
	}
}
