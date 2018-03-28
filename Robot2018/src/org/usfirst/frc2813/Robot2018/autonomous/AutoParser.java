package org.usfirst.frc2813.Robot2018.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoDrive;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoStop;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSet;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggle;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class AutoParser extends CommandGroup {

	public AutoParser() {
		// Add Commands here:
		// e.g. addSequential(new Command1());
		// addSequential(new Command2());
		// these will run in order.

		// To run multiple commands at the same time,
		// use addParallel()
		// e.g. addParallel(new Command1());
		// addSequential(new Command2());
		// Command1 and Command2 will run in parallel.

		// A command group will require all of the subsystems that each member
		// would require.
		// e.g. if Command1 requires chassis, and Command2 requires arm,
		// a CommandGroup containing them would require both the chassis and the
		// arm.
	}

	public AutoParser(File f) throws FileNotFoundException {
		this();
		parse(f);
	}

	public void parse(File f) throws FileNotFoundException {
		try (Scanner s = new Scanner(f)) {
			parse(s);
		}
	}

	private boolean async;
	private double speed;
	private double startSpeedFactor = 1, endSpeedFactor = 1;

	private void parse(Scanner s) {
		Direction direction;
		while (s.hasNext()) {
			switch (s.next()) {
			case "drive":
				direction = Direction.valueOf(s.next().toUpperCase());
				double distance = s.nextDouble();
				if (s.hasNextDouble()) {
					final double curve = s.nextDouble();
					final boolean clockwise;
					String st;
					switch (st = s.next().toLowerCase()) {
					case "clockwise":
						clockwise = true;
						break;
					case "counterclockwise":
						clockwise = false;
						break;
					default:
						throw new IllegalArgumentException("expected 'clockwise' or 'counterclockwise', got " + st);
					}
					add(new DriveTrainAutoDrive(Robot.driveTrain, distance, direction, speed, startSpeedFactor,
							endSpeedFactor, curve, clockwise));
				} else
					add(new DriveTrainAutoDrive(Robot.driveTrain, distance, direction, speed, startSpeedFactor,
							endSpeedFactor));
				break;
			case "stop":
				add(new DriveTrainAutoStop(Robot.driveTrain));
				break;
			case "gripper":
				String token = s.next().toUpperCase();
				if (token.equals("TOGGLE"))
					add(new SolenoidToggle(Robot.jaws));
				else {
					add(new SolenoidSet(Robot.jaws, Direction.valueOf(token)));
				}
				break;
			case "intake":
				
			}
		}
	}

	private void add(Command cmd) {
		if (async)
			addParallel(cmd);
		else
			addSequential(cmd);
		async = false;
	}
}
