package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Arm.MaintainArmPosition;
import org.usfirst.frc2813.Robot2018.commands.Arm.Obsolete.ArmSolenoid;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Arm extends Subsystem {
	public final TalonSRX srxController = RobotMap.srxArm;
	public boolean encoderFunctional = true;
	public static final Solenoid gripper = RobotMap.armSingleSolenoid;
	/**
	 * Double solenoids are two independent coils.
	 * 
	 * When the gripper solenoid coil is activated, this is set to a relatively small number,
	 * whereafter it is decremented each time through the {@link Scheduler} main loop.  When
	 * it reaches zero, the coil is turned off again.
	 */
	@SuppressWarnings("unused")
	private int disableGripper = 0;

	// Put methods for controlling this subsystem
	// here. Call these from Commands.
	@Override
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
		setDefaultCommand(new MaintainArmPosition());
	}

	@Override
	public void periodic() {
		if(getCurrentCommand() == null) {
			new MaintainArmPosition().start();
		}
	}

	// Manage jaws
	public static void closeJaws() {
		if (!jawsAreOpen()) {
			new ArmSolenoid().start();
		}
	}
	public static void openJaws() {
		if (jawsAreOpen()) {
			new ArmSolenoid().start();
		}
	}
	// accessor for state of jaws
	public static Boolean jawsAreOpen() { return gripper.get(); }
}

