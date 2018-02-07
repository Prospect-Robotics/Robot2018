package org.usfirst.frc2813;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.PIDInterface;
import edu.wpi.first.wpilibj.Sendable;

public class WatchdogPIDInterface implements PIDInterface, MotorSafety {
	private final PIDInterface m_pid;
	private final MotorSafetyHelper m_safetyHelper = new MotorSafetyHelper(this);
	
	public WatchdogPIDInterface(PIDInterface obj) {
		m_pid = obj;
	}
	@Override
	public void setExpiration(double timeout) {
		m_safetyHelper.setExpiration(timeout);
	}

	@Override
	public double getExpiration() {
		return m_safetyHelper.getExpiration();
	}

	@Override
	public boolean isAlive() {
		return m_safetyHelper.isAlive();
	}

	@Override
	public void stopMotor() {
		m_pid.disable();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		m_safetyHelper.setSafetyEnabled(enabled);
	}

	@Override
	public boolean isSafetyEnabled() {
		return m_safetyHelper.isSafetyEnabled();
	}

	@Override
	public String getDescription() {
		if(m_pid instanceof Sendable) {
			// Most implementers of PIDInterface also implement Sendable.
			Sendable s = (Sendable) m_pid;
			return "PID controller "+s.getName()+" in subsystem "+s.getSubsystem()+" is being automatically disabled because calls to feed() stopped.\n";
		}
		return m_pid.toString();
	}

	@Override
	public void setPID(double p, double i, double d) {
		m_pid.setPID(p, i, d);
	}

	@Override
	public double getP() {
		return m_pid.getP();
	}

	@Override
	public double getI() {
		return m_pid.getI();
	}

	@Override
	public double getD() {
		return m_pid.getD();
	}

	@Override
	public void setSetpoint(double setpoint) {
		m_pid.setSetpoint(setpoint);
		feed();
	}
	/**
	 * Reset the internal watchdog timer.  If this timer expires, the PID controller will shut itself off.
	 * <p>
	 * Call this in the execute() method of your command.
	 */
	public void feed() {
		m_safetyHelper.feed();
	}
	@Override
	public double getSetpoint() {
		return m_pid.getSetpoint();
	}

	@Override
	public double getError() {
		return m_pid.getError();
	}

	@Override
	public void enable() {
		m_pid.enable();
	}

	@Override
	public void disable() {
		m_pid.disable();
	}

	@Override
	public boolean isEnabled() {
		return m_pid.isEnabled();
	}

	@Override
	public void reset() {
		m_pid.reset();
	}

}
