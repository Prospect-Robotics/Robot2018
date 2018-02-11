package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Arm extends Subsystem {
	public final SpeedController speedController1 = RobotMap.armSpeedController1;
	public final Encoder encoder1 = RobotMap.armQuadratureEncoder1;
	public final DigitalInput digitalInput1 = RobotMap.armDigitalInput1;
	public final DoubleSolenoid solenoidIn = RobotMap.armSolenoid1;
	public final DoubleSolenoid solenoidOut = RobotMap.armSolenoid2;

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	@Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
}

