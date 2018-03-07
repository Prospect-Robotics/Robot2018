// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package org.usfirst.frc2813.Robot2018.commands.Arm;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc2813.Robot2018.Direction;
import org.usfirst.frc2813.Robot2018.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

/*
 * TODO: This class needs to be coded for the Talon SRX
 * Need to decide what this class is used for - SHOULD IT STILL BE USED?
 */
public class ArmLimitSwitch extends Command {
	private DigitalInput digitalInput;
	private SpeedController speedController;
	private Encoder encoder;
	private Direction direction;
	private static final double DESIRED_ENCODER_VALUE  = 1;//TODO change value

	public ArmLimitSwitch(Direction direction) {
		this.direction = direction;
		requires(Robot.arm);
	}

	// Called just before this Command runs the first time
	//@Override
	protected void initialize() {
		//FIXME! this is wrong. Command is broken for now
		//digitalInput = Robot.arm.srxController.limitSwitch;
    		//speedController = Robot.arm.speedController;
    		//encoder = Robot.arm.encoder;
	}

	protected void execute() {
		double encoder_value = DESIRED_ENCODER_VALUE;
		encoder_value *= direction == Direction.UP ? 1 : -1;
		speedController.set(encoder_value);		
	}

	protected boolean isFinished() {
		if (direction == Direction.UP) {
			//FIXME! makes no sense! What are we trying to do here?
			return digitalInput.get();
		}
		return encoder.getDistance() >= DESIRED_ENCODER_VALUE;
	}
}
