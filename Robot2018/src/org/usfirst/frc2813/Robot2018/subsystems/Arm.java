package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.RobotMap;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class Arm extends Subsystem {
	public final SpeedController speedController= RobotMap.armSpeedController;
	public final Encoder encoder = RobotMap.armQuadratureEncoder;
	public boolean encoderFunctional = true;
	public final DigitalInput limitSwitch = RobotMap.armLimitSwitch;
	public final DoubleSolenoid gripper = RobotMap.armDoubleSolenoid;
	private boolean jawsOpen;
	/**
	 * Double solenoids are two independent coils.
	 * 
	 * When the gripper solenoid coil is activated, this is set to a relatively small number,
	 * whereafter it is decremented each time through the {@link Scheduler} main loop.  When
	 * it reaches zero, the coil is turned off again.
	 */
	private int disableGripper = 0;
	
	
	public final PIDController controller = new PIDController(0, 0, 0, encoder, speedController);

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	@Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
	
	@Override
	public void periodic() {
		if(limitSwitch.get()) { // If the limit switch is pressed, the elevator is at the bottom, so...
			encoder.reset();    // ...reset the encoder to zero...
			controller.reset(); // ...and reset the PID controller so that it doesn't get confused because the 
		}
		
		if(!encoderFunctional && controller.isEnabled()) {
			DriverStation.reportError("Can't use PID control on the arm - the encoder is malfunctioning.", false);
			controller.disable();
		}
		
		if(disableGripper > 0) {
			disableGripper--;
			if(disableGripper == 0)
				gripper.set(Value.kOff);
		}
	}
	
	public void grabCube() {
		gripper.set(Value.kForward);
		disableGripper = 2;
	}
	public void dropCube() {
		gripper.set(Value.kReverse);
		disableGripper = 2;
	}

	public boolean jawsOpen() {
		return jawsOpen;
	}
}

