package org.usfirst.frc2813.Robot2018.commands.drivetrain;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc2813.Robot2018.commands.RunningInstructions;
import org.usfirst.frc2813.Robot2018.commands.Lockout;
import org.usfirst.frc2813.Robot2018.commands.subsystem.SubsystemCommand;
import org.usfirst.frc2813.Robot2018.subsystems.drivetrain.DriveTrain;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 * controller to stop on N pids. At least one encoder is required to work.
 */
public class DriveTrainAutoStop extends SubsystemCommand<DriveTrain> {

	private final double Kp = 0.8, Ki = 0, Kd = 0;

	private DriveTrain driveTrain;

	private boolean initialized = false;
	private boolean isPIDEnabled = false;
	private List<PIDController> pidControllers = new ArrayList<>();
	private List<PIDOutput> speedControllers = new ArrayList<>();

	/**
	 * This class is a multiplexer that takes PIDOutput and sends to all speed controllers
	 */
	private static class AllPIDControllers implements PIDOutput {
		private final DriveTrainAutoStop parent;
		
		public AllPIDControllers(DriveTrainAutoStop parent) {
			this.parent = parent;
		}
		
		public void pidWrite(double output) {
			for (PIDOutput pid : parent.speedControllers) {
				pid.pidWrite(output);
			}
		}
	}
	
	/**
	 * This class wraps a PIDOutput and only calls the output if we are in auto.
	 * If not, it will disable the command's PID controllers
	 */
	private static class AutoOnlyPIDOutput implements PIDOutput {
		private final DriveTrainAutoStop parent;
		private final PIDOutput pidOut;
		
		public AutoOnlyPIDOutput(DriveTrainAutoStop parent, PIDOutput pidOut) {
			this.parent = parent;
			this.pidOut = pidOut;
		}
		
		public void pidWrite(double output) {
			if(!parent.isAutonomous()) {
				pidOut.pidWrite(output);
			}
		}
	}

	public DriveTrainAutoStop(DriveTrain driveTrain) {
		super(driveTrain, RunningInstructions.RUN_NORMALLY, Lockout.Disabled);
		this.driveTrain = driveTrain;
		requires(driveTrain);
	}

	/**
	 * All speedControllers go into a list, and an instance of AllPIDControllers redirects PIDOutput to each of them.
	 * Each speed controller output (including the AllPIDControllers instance) is wrapped in a wrapper class
	 * AutoOnlyPIDOutput.  AutoOnlyPIDOutput's job is to NOT output anything when we are no longer in auto,
	 * but instead shut down the left over PID controllers in the command.
	 */
	protected void initializePIDControllers() {
		if (!initialized) {
			// Build a list of all the speed controllers, we can use when we need to drive
			// them all
			speedControllers.add(driveTrain.getSpeedControllerLeftMaster());
			speedControllers.add(driveTrain.getSpeedControllerRightMaster());
			// Initialize one PID for every working encoder.
			if (driveTrain.encoderPortFunctional && driveTrain.encoderStarboardFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderLeft(),
						new AutoOnlyPIDOutput(this, driveTrain.getSpeedControllerLeftMaster())));
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderRight(),
						new AutoOnlyPIDOutput(this, driveTrain.getSpeedControllerRightMaster())));
			} else if (driveTrain.encoderPortFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderLeft(), 
						new AutoOnlyPIDOutput(this, new AllPIDControllers(this))));
			} else if (driveTrain.encoderStarboardFunctional) {
				pidControllers.add(new PIDController(Kp, Ki, Kd, driveTrain.getEncoderRight(), 
						new AutoOnlyPIDOutput(this, new AllPIDControllers(this))));
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
	
	protected void teardownPIDControllers() {
		if(initialized) {
			for (PIDController pid : pidControllers) {
				pid.reset();
				pid.free();
			}
			speedControllers.clear();
			pidControllers.clear();
			initialized = false;
		}
	}

	protected void disablePID() {
		for (PIDController pid : pidControllers) {
			pid.disable();
		}
		isPIDEnabled = false;
	}

	protected void enablePID() {
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

	protected void ghscInitialize() {
		// Enable all encoders
		action("initialize", "starting PID.");
		initializePIDControllers();
		enablePID();
	}

	protected boolean isPIDEnabled() {
		return isPIDEnabled;
	}

	protected boolean ghscIsFinished() {
		return !isAutonomous() || allEncodersOnTarget() || !isPIDEnabled;
	}

	// Called once after isFinished returns true
	protected void ghscEnd() {
		action("end", "stopping.");
		// Disable all encoders
		teardownPIDControllers();
	}
	
	/**
	 * Helper to determine if auto is still going or if we should shutdown.
	 */
	protected static boolean isAutonomous() {
		return DriverStation.getInstance().isAutonomous();
	}

	@Override
	public boolean ghscIsSubsystemRequired() {
		return true;
	}

}
