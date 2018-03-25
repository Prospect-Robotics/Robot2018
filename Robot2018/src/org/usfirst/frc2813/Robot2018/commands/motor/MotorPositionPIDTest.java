package org.usfirst.frc2813.Robot2018.commands.motor;

import org.usfirst.frc2813.Robot2018.motor.IMotor;
import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
import org.usfirst.frc2813.logging.Logger;
import org.usfirst.frc2813.units.Direction;
import org.usfirst.frc2813.units.uom.LengthUOM;
import org.usfirst.frc2813.units.values.Length;

/**
 * Move motor in given direction until interrupted.
 * Hold current position with PID when interrupted. 
 */
public final class MotorPositionPIDTest extends AbstractMotorCommand {
	private final Length minHeight; 
	private final Length maxHeight;
	private final Length marginOfError;

	private Direction targetDirection = Direction.REVERSE;
	private Length targetPosition = LengthUOM.Inches.create(0);
	
	public MotorPositionPIDTest(IMotor motor, Length minHeight, Length maxHeight) {
		super(motor, true);
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.marginOfError = motor.getConfiguration().getNativeDisplayLengthUOM().create(0.5); // NB: This is a hack
		setName(toString());
	}

	@Override
	protected void initialize() {
		super.initialize();
		motor.moveToAbsolutePosition(minHeight);
	}

	protected void execute() {
		if(motor.getCurrentPositionErrorWithin(this.marginOfError)) {
			targetDirection = targetDirection.getInverse();
			Logger.info("REVERSING.  GOING " + targetDirection + " @ " + motor.getCurrentPosition() + " Error " + motor.getCurrentPositionError() + " Goal " + targetPosition);
			Length targetPosition = targetDirection.equals(Direction.FORWARD) ? minHeight : maxHeight;
			motor.moveToAbsolutePosition(targetPosition);
		} else {
			Logger.info("NOT THERE.  GOING " + targetDirection + " @ " + motor.getCurrentPosition() + " Error " + motor.getCurrentPositionError() + " Goal " + targetPosition);
		}
	}
	
	@Override
	protected boolean isFinished() {
		return false;  // run until interrupted, even if subsystem stops
	}

	@Override
	protected void interrupted() {
		super.interrupted();
        /*
        * NOTE: Typically this is also the default command for motor subsystems, so it's kind of redundant but logical. 
        */
		motor.holdCurrentPosition();
	}

    public String toString() {
        return getClass().getSimpleName() + "(" + motor + ", minHeight=" + minHeight + ", maxHeight=" + maxHeight + ")";
    }
}
