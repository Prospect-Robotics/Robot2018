package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 * controller to stop on N pids. At least one encoder is required to work.
 */
public class DriveTrainStop extends Command {

	private final double Kp = 0.8, Ki = 0, Kd = 0;

	private DriveTrain driveTrain;

	private boolean initialized = false;
	private boolean isPIDEnabled = false;
	private List<PIDController> pidControllers = new ArrayList<>();
	private List<PIDOutput> speedControllers = new ArrayList<>();

	// Write to a list of PID controllers
	private PIDOutput driveAll = new PIDOutput() {
		public void pidWrite(double output) {
			for (PIDOutput pid : speedControllers) {
				pid.pidWrite(output);
			}
		}
	};

	public DriveTrainStop(DriveTrain driveTrain) {
		this.driveTrain = driveTrain;
		requires(driveTrain);
	}

	protected void initializePIDControllers() {
		if (!initialized) {
			// Build a list of all the speed controllers, we can use when we need to drive
			// them all
			speedControllers.add(driveTrain.getSpeedControllerPort());
			speedControllers.add(driveTrain.getSpeedControllerStarboard());
			// Initialize one PID for every working encoder.
			if (driveTrain.encoderPortFunctional && driveTrain.encoderStarboardFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderPort(),
						driveTrain.getSpeedControllerPort()));
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderStarboard(),
						driveTrain.getSpeedControllerStarboard()));
			} else if (driveTrain.encoderPortFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderPort(), driveAll));
			} else if (driveTrain.encoderStarboardFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderStarboard(), driveAll));
			} else {
				DriverStation.reportWarning("Can't PIDStop, BOTH ENCODERS OFFLINE", true);
			}
			// Configure the speed PID controller common values
			for (PIDController pid : pidControllers) {
				pid.setAbsoluteTolerance(20); // 20 encoder ticks +/-, close enough for whatever encoder is used
				pid.setOutputRange(-1.0, 1.0);
				pid.disable();
			}
			initialized = true;
		}
	}

	protected void disablePID() {
		initializePIDControllers();
		for (PIDController pid : pidControllers) {
			pid.disable();
		}
		isPIDEnabled = false;
	}

	protected void enablePID() {
		initializePIDControllers();
		for (PIDController pid : pidControllers) {
			pid.enable();
		}
		isPIDEnabled = true;
	}

	public boolean allEncodersOnTarget() {
		initializePIDControllers();
		for (PIDController pid : pidControllers)
			if (!pid.onTarget())
				return false;
		return true;
	}

	protected void initialize() {
		// Enable all encoders
		initializePIDControllers();
		enablePID();
	}

	protected boolean isPIDEnabled() {
		return isPIDEnabled;
	}

	protected boolean isFinished() {
		return allEncodersOnTarget() || !isPIDEnabled;
	}

	// Called once after isFinished returns true
	protected void end() {
		// Disable all encoders
		disablePID();
	}
}
