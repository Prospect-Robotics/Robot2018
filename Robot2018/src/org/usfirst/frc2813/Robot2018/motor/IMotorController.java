package org.usfirst.frc2813.Robot2018.motor;

import org.usfirst.frc2813.units.values.Length;

public interface IMotorController extends IMotor {
	/**
	 * Set the motor's encoder value to a specific reference position,
	 * from this point onward any relative commands will be with respect to this 
	 * reference point.  Typically this is used to set the "zero" point.
	 * @return true if the command is supported and accepted
	 * @throws UnsupportedOperationException if the motor cannot support this feature.
	 * @see IMotor#getCurrentPosition()
	 * @param position The position to set the encoder to.
	 **/
	public boolean resetEncoderSensorPosition(Length position);
	/**
	 * Return true if motor inversion can be handled automatically
	 * from this point onward.
	 * @return True if this motor controller can handle motor polarity reversal.
	 */
	public boolean supportsMotorInversion();
	/** 
	 * Return true if sensor inversion can be handled automatically, from this point onward.
	 * @return True if this motor controller can handle sensor phase reversal.
	 */
	public boolean supportsSensorInversion();
}
