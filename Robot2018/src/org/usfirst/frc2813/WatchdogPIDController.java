package org.usfirst.frc2813;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

/**
 * A PIDController that implements MotorSafety, i.e. it has a built-in "watchdog
 * timer".
 * <p>
 * A watchdog timer is a device used to automatically reset a computer chip when
 * it freezes. This is done by having the chip constantly send pulses to the
 * watchdog timer as long as it's working properly. If the pulses ever stop, the
 * watchdog "bites" and resets the chip.
 * <p>
 * In this instance, watchdogs are used a little differently. It works the same
 * as the motors in WPILib. You have to keep calling
 * {@link SpeedController#set()} or the motor will automatically shut off. Here,
 * you have to keep calling {@link #feedWatchdog()} or the PID controller will
 * automatically shut off.
 * <p>
 * In other words, use this just as you would a normal PIDController, but call
 * its {@link #feedWatchdog()} method from your {@code execute()}. If it stops
 * getting called, the PID controller will shut itself off for you in case you
 * forgot to do it yourself.
 * 
 * 
 * @author Sean
 *
 */

public class WatchdogPIDController extends PIDController implements MotorSafety {

	private final MotorSafetyHelper m_safetyHelper = new MotorSafetyHelper(this);

	public WatchdogPIDController(double Kp, double Ki, double Kd, PIDSource source, PIDOutput output) {
		super(Kp, Ki, Kd, source, output);
	}

	public WatchdogPIDController(double Kp, double Ki, double Kd, PIDSource source, PIDOutput output, double period) {
		super(Kp, Ki, Kd, source, output, period);
	}

	public WatchdogPIDController(double Kp, double Ki, double Kd, double Kf, PIDSource source, PIDOutput output) {
		super(Kp, Ki, Kd, Kf, source, output);
	}

	public WatchdogPIDController(double Kp, double Ki, double Kd, double Kf, PIDSource source, PIDOutput output,
			double period) {
		super(Kp, Ki, Kd, Kf, source, output, period);
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
		super.disable();
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
		return toString();
	}
	
	/**
	 * Reset an internal "watchdog timer". If this timer ever expires, the PID
	 * controller will shut itself off for safety. Call this from execute() of any
	 * commands that need the PID controller to be running.
	 * <p>
	 *
	 * This is in case you forget to call disable() at the end of your command,
	 * which, if the safety weren't there, would the PIDController to be left
	 * running (remember that in WPILib, PID control loops run in their own thread),
	 * which would be very very bad. Say you're using it to control the drive train.
	 * After the command finishes, the {@link Scheduler} automatically returns
	 * control of the drive train to the driver. If the command forgot to call
	 * {@link #disable()} on the PID controller, the PID control loop will continue
	 * running as if nothing had happened.  The PID controller still thinks it has
	 * permission to control the drive train and will fight the driver for control
	 * of the robot -- and likely win.  This will probably result in an E-stop
	 * and some very mad looking drive team members and (if you're at competition)
	 * judges.
	 */
	public void feedWatchdog() {
		enable();
		m_safetyHelper.feed();
	}

}
