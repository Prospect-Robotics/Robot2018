package org.usfirst.frc2813;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class Double2SingleSolenoid implements Sendable, MotorSafety  {
	
	private final DoubleSolenoid m_solenoid;
	private boolean on = false;
	private final MotorSafetyHelper m_safetyHelper = new MotorSafetyHelper(this);

	public Double2SingleSolenoid(int moduleNumber, int portForward, int portReverse) {
		m_solenoid = new DoubleSolenoid(moduleNumber, portForward, portReverse);
	}
	
	public void set(boolean on) {
		this.on = on;
		m_solenoid.set(on ? Value.kForward : Value.kReverse);
	}
	
	public boolean get() {
		return on;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		m_solenoid.initSendable(builder);
	}

	@Override
	public String getName() {
		return m_solenoid.getName();
	}

	@Override
	public void setName(String name) {
		m_solenoid.setName(name);
	}

	@Override
	public String getSubsystem() {
		return m_solenoid.getSubsystem();
	}

	@Override
	public void setSubsystem(String subsystem) {
		m_solenoid.setSubsystem(subsystem);
	}
	
	@Override
	public void setName(String subsystem, String name) {
		m_solenoid.setName(subsystem, name);
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
		m_solenoid.set(Value.kOff);
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
		return getName()+" in "+getSubsystem();
	}

}
