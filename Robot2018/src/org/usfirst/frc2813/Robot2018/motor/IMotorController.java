package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.values.Length;

public interface IMotorController extends IMotor {
	/**
	 * Set the motor's encoder value to a specific reference position,
	 * from this point onward any relative commands will be with respect to this 
	 * reference point.  Typically this is used to set the "zero" point.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see getCurrentPosition()
	 **/
	public boolean resetEncoderSensorPosition(Length position);
	/**
	 * Return true if motor inversion can be handled automatically
	 * from this point onward.
	 */
	public boolean supportsMotorInversion();
	/* 
	 * Return true if sensor inversion can be handled automatically,
	 * from this point onward.
	 */
	public boolean supportsSensorInversion();
}
