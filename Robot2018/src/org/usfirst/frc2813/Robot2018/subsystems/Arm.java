package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;
import org.usfirst.frc2813.Robot2018.commands.Arm.ArmSolenoid;
import org.usfirst.frc2813.Robot2018.commands.Arm.MaintainArmPosition;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Arm extends Subsystem {
	public final TalonSRX srxController = RobotMap.srxArm;
	public boolean encoderFunctional = true;
	public final Solenoid gripper = RobotMap.armSingleSolenoid;
	private boolean jawsOpen = false;
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
	
	public void grabCube() {
		jawsOpen = false;
		if (RobotMap.armSingleSolenoid.get() == false) 
			new ArmSolenoid().start();
	}
	public void dropCube() {
		jawsOpen = true;
		if (RobotMap.armSingleSolenoid.get() == true)
			new ArmSolenoid().start();
	}
	/**
	 * The position of the jaws. True if open, false if closed
	 */
	public boolean jawsOpen() {
		return jawsOpen;
	}
}

