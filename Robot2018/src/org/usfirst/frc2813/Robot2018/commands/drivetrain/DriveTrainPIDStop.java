package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.CommandDuration;
import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveTrainPIDStop extends SubsystemCommand<DriveTrain> {
	
	public static final double Kp = 0, Ki = 0, Kd = 0; // TODO placeholder values
	
	private PIDController pid1, pid2;
	private final DriveBothWheels driveBothWheelsCallback;
	private boolean PIDenabled = false;

	class DriveBothWheels implements PIDOutput {
		private final DriveTrainPIDStop pidStop;
		
		DriveBothWheels(DriveTrainPIDStop pidStop) {
			this.pidStop = pidStop;
		}
		@Override
		public void pidWrite(double output) {
			pidStop.driveBothWheels(output);
		}
	};

    public DriveTrainPIDStop(DriveTrain driveTrain) {
    	super(driveTrain, CommandDuration.DISABLED, Lockout.Disabled);
    	this.driveBothWheelsCallback = new DriveBothWheels(this);
    }

    // Called just before this Command runs the first time
    @Override
    protected void subsystemInitializeImpl() {
    	if(pid1 == null) {
    		if(Robot.driveTrain.encoderPortFunctional && subsystem.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, subsystem.getEncoderPort(), subsystem.getSpeedControllerPort());
    			pid2 = new PIDController(Kp, Ki, Kd, subsystem.getEncoderStarboard(), subsystem.getSpeedControllerStarboard());
    		}
    		else if(Robot.driveTrain.encoderPortFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, subsystem.getEncoderPort(), driveBothWheelsCallback);
    		} else if(Robot.driveTrain.encoderStarboardFunctional) {
    			pid1 = new PIDController(Kp, Ki, Kd, subsystem.getEncoderStarboard(), driveBothWheelsCallback);
    		} else {
    			DriverStation.reportWarning("Can't PIDStop, BOTH ENCODERS OFFLINE", true);
    			return;
    		}
    	}
   
		enablePID();
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    protected boolean subsystemIsFinishedImpl() {
//    	if(pid1 == null)
//    		// can't do anything with only one controller
//    		return true;
//        return pid1.onTarget() && pid2 == null ? true : pid2.onTarget();
    	return false;
    }

    // Called when we stop or switch modes
    protected void disablePID() {
    	if(pid1 != null) pid1.reset();
    	if(pid2 != null) pid2.reset();
    	this.PIDenabled = false; 
    }

    protected boolean isPIDEnabled() {
    	return PIDenabled;
    }
    
    // Called when we stop or switch modes
    protected void enablePID() {
    	pid1.enable();
    	if(pid2 != null) pid2.enable();
    	this.PIDenabled = true; 
    }
    
    // Called once after we are interrupted or end normally
    @Override
    protected void interruptedWhileWaitingImpl() {
    	disablePID();
    }

    private void driveBothWheels(double speed) {
    	subsystem.arcadeDrive(speed, 0);
    }

	@Override
	public boolean isSubsystemRequired() {
		return true;
	}
}
