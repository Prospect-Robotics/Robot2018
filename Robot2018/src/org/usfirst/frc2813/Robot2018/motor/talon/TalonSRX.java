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
 * A wrapper class to handle an SRX Talon motor controller.  Assumes all units are already correct.  use MotorUnitConversionAdapter 
 * if you need a translation layer.  
 */
public final class TalonSRX extends AbstractMotorController {
	private final com.ctre.phoenix.motorcontrol.can.TalonSRX srx;
	
	/* ----------------------------------------------------------------------------------------------
	 * State
	 * ---------------------------------------------------------------------------------------------- */
	
	// Last arg sent for controlMode
	private PIDProfileSlot lastSlot                 = PIDProfileSlot.HoldingPosition;
	private PID         lastPID                  = PID.Primary;
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
	// We will use separate profiles for holding and moving
	public static final PIDProfileSlot PROFILE_SLOT_FOR_HOLD_POSITION = PIDProfileSlot.HoldingPosition;
	// We will use separate profiles for holding and moving
	public static final PIDProfileSlot PROFILE_SLOT_FOR_MOVE          = PIDProfileSlot.Moving;
	// We will use the primary PID loop, not the aux
	public static final PID PID_INDEX_FOR_HOLD_POSITION            = PID.Primary;
	// We will use the primary PID loop, not the aux
	public static final PID PID_INDEX_FOR_MOVE                     = PID.Primary;
	
	/* ----------------------------------------------------------------------------------------------
	 * Constructors
	 * ---------------------------------------------------------------------------------------------- */
	
	public TalonSRX(IMotorConfiguration configuration, com.ctre.phoenix.motorcontrol.can.TalonSRX srx) {
		super(configuration);
		this.srx = srx;
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
	public boolean getCurrentLimitSwitchStatus(Direction direction) {
		if (direction.isNegative()) {
			return srx.getSensorCollection().isRevLimitSwitchClosed();
		} else {
			return srx.getSensorCollection().isFwdLimitSwitchClosed();
		}
	}
	
	@Override
	public final Length getCurrentPosition() {
		int raw = srx.getSelectedSensorPosition(PID.Primary.getPIDIndex());
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
		PID         newPIDIndex         = lastPID;
		ControlMode      newControlMode      = ControlMode.Disabled;
		double           newControlModeValue = 0;

		// Figure out the new control mode and argument
		switch(proposedState.getOperation()) {
		case DISABLED:
			newControlMode = ControlMode.Disabled;
			newSlotIndex   = PROFILE_SLOT_FOR_HOLD_POSITION;
			newPIDIndex    = PID_INDEX_FOR_HOLD_POSITION;
			newControlModeValue = 0;
			break;
		case HOLDING_CURRENT_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_HOLD_POSITION;
			newPIDIndex    = PID_INDEX_FOR_HOLD_POSITION;
			newControlModeValue = getCurrentPosition().getValue();
			break;
		case MOVING_TO_ABSOLUTE_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_TO_RELATIVE_POSITION:
			newControlMode = ControlMode.Position;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toSensorUnits(proposedState.getTargetAbsolutePosition()).getValue();
			break;
		case MOVING_IN_DIRECTION_AT_RATE:
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			newControlModeValue = toMotorUnits(proposedState.getTargetRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble();
			break;
		case CALIBRATING_SENSOR_IN_DIRECTION:
			newControlMode = ControlMode.Velocity;
			newSlotIndex   = PROFILE_SLOT_FOR_MOVE;
			newPIDIndex    = PID_INDEX_FOR_MOVE;
			if(!getCurrentLimitSwitchStatus(proposedState.getTargetDirection())) {
				newControlModeValue = toMotorUnits(configuration.getDefaultRate()).getValue() * proposedState.getTargetDirection().getMultiplierAsDouble()  / 2;
			}
		default:
			break;
		}
		
		// Select the profile to use for the control loop (this is almost certainly going to be closed loop)
		srx.selectProfileSlot(newSlotIndex.getProfileSlotIndex(), newPIDIndex.getPIDIndex());
		// Set the control mode
		srx.set(newControlMode, newControlModeValue);
		
		this.lastControlMode = newControlMode;
		this.lastControlModeValue = newControlModeValue;
		this.lastSlot = newSlotIndex;
		this.lastPID = newPIDIndex;
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
			srx.configSetParameter(
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
		srx.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, pid.getPIDIndex(), getTimeout());
		srx.setSelectedSensorPosition(rawValue, pid.getPIDIndex(), getTimeout());
		int readBack = srx.getSelectedSensorPosition(pid.getPIDIndex());
		if(Math.abs(readBack - rawValue) > SENSOR_RESET_TOLERANCE_PULSES) {
			Logger.error(this + " failed setting selected sensor " + pid.getPIDIndex() + " to " + rawValue + " (Requested " + sensorPosition + ") - got " + readBack + " instead.");
			srx.setSelectedSensorPosition(rawValue, pid.getPIDIndex(), getTimeout());
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
		srx.config_kF(profileSlot.getProfileSlotIndex(), f, getTimeout());
		srx.config_kP(profileSlot.getProfileSlotIndex(), p, getTimeout());
		srx.config_kI(profileSlot.getProfileSlotIndex(), i, getTimeout());
		srx.config_kD(profileSlot.getProfileSlotIndex(), d, getTimeout());
		// I'm not sure if someone has to call selectProfileSlot to reload the values or if it's automatically looking at the right ones (guessing the latter)
	}
	
	public String getDiagnotics() {
		return super.getDiagnostics()
				+ (configuration.hasAll(IMotorConfiguration.Disconnected) ? "" :
				  " [ControlMode=" + lastControlMode
				+ ", ControlModeValue=" + lastControlModeValue
				+ ", SlotIndex=" + lastSlot
				+ ", PIDIndex=" + lastPID
				+ "]");
	}
	
	@Override
	public void configure() {
		// Start disabled
		changeState(MotorStateFactory.createDisabled(this));
		
		// set the peak and nominal outputs, 12V means full
		srx.configNominalOutputForward(0, getTimeout());
		srx.configNominalOutputReverse(0, getTimeout());
		srx.configPeakOutputForward(1, getTimeout());
		srx.configPeakOutputReverse(-1, getTimeout());
		
//		correctRelativeEncodersFromAbsolute();
		
		// Reset PID controllers
		configurePID(PIDProfileSlot.ProfileSlot0, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot1, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot2, 0, 0, 0, 0);
		configurePID(PIDProfileSlot.ProfileSlot3, 0, 0, 0, 0);
		/*
		 * set the allowable closed-loop error, Closed-Loop output will be neutral
		 * within this range. See Table in Section 17.2.1 for native units per rotation.
		 */
		srx.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot0.getProfileSlotIndex(), 0, getTimeout());
		srx.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot1.getProfileSlotIndex(), 0, getTimeout());
		srx.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot2.getProfileSlotIndex(), 0, getTimeout());
		srx.configAllowableClosedloopError(PIDProfileSlot.ProfileSlot3.getProfileSlotIndex(), 0, getTimeout());
		// Disable clearing position on quad index, we don't support/use it and this restores SRX default.
		srx.configSetParameter(ParamEnum.eClearPositionOnQuadIdx, 0 /* disabled */, 0 /* unused */, 0 /* unused */, getTimeout());
		
		// Set forward hard limits.  NB: You won't have both local and remote, so it's ok that they both are writing to clear flag here.
		if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.LocalForwardHardLimitSwitch)) {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, configuration.getForwardHardLimitSwitchNormal(), getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
		} else if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteForwardHardLimitSwitch)) {
			srx.configForwardLimitSwitchSource(configuration.getRemoteForwardHardLimitSwitchSource(), configuration.getForwardHardLimitSwitchNormal(), configuration.getRemoteForwardHardLimitSwitchDeviceId(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, configuration.getForwardHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);			
		} else {
			srx.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.FORWARD, false);
		}
		// Set reverse hard limits.  NB: You won't have both local and remote, so it's ok that they both are writing to clear flag here.
		if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.LocalReverseHardLimitSwitch)) {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, configuration.getReverseHardLimitSwitchNormal(), getTimeout());
//			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);			
		} else if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.RemoteReverseHardLimitSwitch)) {
			srx.configReverseLimitSwitchSource(configuration.getRemoteReverseHardLimitSwitchSource(), configuration.getReverseHardLimitSwitchNormal(), configuration.getRemoteReverseHardLimitSwitchDeviceId(), getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, configuration.getReverseHardLimitSwitchResetsEncoder());
		} else {
			srx.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, getTimeout());
			setHardLimitSwitchClearsPositionAutomatically(Direction.REVERSE, false);
		}
		// Set forward soft limit
		if(configuration.hasAll(IMotorConfiguration.Forward|IMotorConfiguration.LimitPosition|IMotorConfiguration.ForwardSoftLimitSwitch)) {
			srx.configForwardSoftLimitEnable(true, getTimeout());
			srx.configForwardSoftLimitThreshold(configuration.getForwardSoftLimit().convertTo(configuration.getNativeSensorLengthUOM()).getValueAsInt(), getTimeout());
		} else {
			srx.configForwardSoftLimitThreshold(0, getTimeout()); // Clear it so it's not confusing us in RoboRio Web UI
			srx.configForwardSoftLimitEnable(false, getTimeout());
		}
		// Set reverse soft limit
		if(configuration.hasAll(IMotorConfiguration.Reverse|IMotorConfiguration.LimitPosition|IMotorConfiguration.ReverseSoftLimitSwitch)) {
			srx.configReverseSoftLimitEnable(true, getTimeout());
			srx.configReverseSoftLimitThreshold(configuration.getReverseSoftLimit().convertTo(configuration.getNativeSensorLengthUOM()).getValueAsInt(), getTimeout());
		} else {
			srx.configReverseSoftLimitThreshold(0, getTimeout()); // Clear it so it's not confusing us in RoboRio Web UI
			srx.configReverseSoftLimitEnable(false, getTimeout());
		}
		srx.setSensorPhase(configuration.getSensorPhaseIsReversed());
		srx.setInverted(configuration.getMotorPhaseIsReversed());
		// Set neutral mode, if specified - otherwise leave it with pre-configured value
		if(configuration.hasAny(IMotorConfiguration.NeutralMode)) {
			srx.setNeutralMode(configuration.getNeutralMode());
		} else { 
			srx.setNeutralMode(NeutralMode.EEPROMSetting); 
		}

		// Configure the PID profiles
		Iterator<PIDConfiguration> pidProfiles = configuration.getPIDConfigurations().iterator();
		while(pidProfiles.hasNext()) {
			PIDConfiguration pidConfiguration = pidProfiles.next();
	 	    configurePID(
	 	    		PIDProfileSlot.get(pidConfiguration.getProfileIndex()),
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
		return configuration.getNativeSensorRateUOM().create(srx.getSelectedSensorVelocity(lastPID.getPIDIndex()));
	}
	protected boolean isUsingPIDSlotIndexForHolding() {
		return lastSlot != null && lastSlot.equals(PROFILE_SLOT_FOR_HOLD_POSITION);
	}
	
	protected boolean updatePIDSlotIndex(boolean holding) {
		if(null == lastPID || null == lastSlot) {
			return false;
		}
		PIDProfileSlot newSlot = holding ? PROFILE_SLOT_FOR_HOLD_POSITION : PROFILE_SLOT_FOR_MOVE;	
		srx.selectProfileSlot(newSlot.getProfileSlotIndex(), lastPID.getPIDIndex());
		this.lastSlot = newSlot;
		return true;
	}
}
