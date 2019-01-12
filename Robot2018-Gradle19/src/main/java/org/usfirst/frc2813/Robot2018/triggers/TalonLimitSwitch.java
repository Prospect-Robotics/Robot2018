package org.usfirst.frc2813.Robot2018.triggers;

import org.usfirst.frc2813.units.Direction;

import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *
 */
public class TalonLimitSwitch extends Trigger {
	private final SensorCollection sensors;
	private final boolean whichLimit;

	public TalonLimitSwitch(TalonSRX talon, Direction whichLimitSwitch) {
		sensors = talon.getSensorCollection();
		switch (whichLimitSwitch) {
		case FORWARD:
			whichLimit = true;
			break;
		case REVERSE:
			whichLimit = false;
			break;
		default:
			throw new IllegalArgumentException(
					"whichLimitSwitch must be Direction.FORWARD or Direction.REVERSE.  Got " + whichLimitSwitch);
		}
	}

	public boolean get() {
		return whichLimit ? sensors.isFwdLimitSwitchClosed() : sensors.isRevLimitSwitchClosed();
	}
}
