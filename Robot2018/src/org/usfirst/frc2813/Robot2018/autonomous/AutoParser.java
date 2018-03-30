//package org.usfirst.frc2813.Robot2018.autonomous;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Scanner;
//
//import org.usfirst.frc2813.Robot2018.Robot;
//import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoDrive;
//import org.usfirst.frc2813.Robot2018.commands.drivetrain.DriveTrainAutoStop;
//import org.usfirst.frc2813.Robot2018.commands.motor.MotorMoveToAbsolutePosition;
//import org.usfirst.frc2813.Robot2018.subsystems.motor.Motor;
//import org.usfirst.frc2813.Robot2018.subsystems.solenoid.Solenoid;
//import org.usfirst.frc2813.units.Direction;
//import org.usfirst.frc2813.units.SystemOfMeasurement;
//import org.usfirst.frc2813.units.uom.LengthUOM;
//import org.usfirst.frc2813.units.uom.UOM;
//import org.usfirst.frc2813.units.values.Length;
//
//import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.command.CommandGroup;
//
///**
// * 
// * A parser for what is by any reasonable definition COBOL.
// *
// */
//public class AutoParser extends CommandGroup {
//
//	public AutoParser() {
//		// Add Commands here:
//		// e.g. addSequential(new Command1());
//		// addSequential(new Command2());
//		// these will run in order.
//
//		// To run multiple commands at the same time,
//		// use addParallel()
//		// e.g. addParallel(new Command1());
//		// addSequential(new Command2());
//		// Command1 and Command2 will run in parallel.
//
//		// A command group will require all of the subsystems that each member
//		// would require.
//		// e.g. if Command1 requires chassis, and Command2 requires arm,
//		// a CommandGroup containing them would require both the chassis and the
//		// arm.
//
//		if (!initialized)
//			initialize();
//	}
//
//	public AutoParser(File f) throws FileNotFoundException {
//		this();
//		parse(f);
//	}
//
//	public void parse(File f) throws FileNotFoundException {
//		try (BufferedReader b = new BufferedReader(new FileReader(f)) {
//			parse(b);
//		}
//	}
//
//	private boolean async;
//	private double speed;
//	private double startSpeedFactor = 1, endSpeedFactor = 1;
//	private double default_speed;
//	private double current_speed;
//	private double current_direction = 0;
//
//	private static boolean initialized = false;
//	// final means the object itself cannot be changed. its contents, however, can.
//	private static final HashMap<String, Motor> motors = new HashMap<String, Motor>();
//	private static final HashMap<String, Solenoid> solenoids = new HashMap<String, Solenoid>();
//
//	// this can't be a static block because Robot.arm, etc is null until the end of
//	// robotInit.
//	@SuppressWarnings("unused") // It IS used, just from the constructor, but Java isn't smart enough to realize
//								// that.
//	private static void initalize() {
//		initialized = true;
//		motors.put("arm", Robot.arm);
//		motors.put("elevator", Robot.elevator);
//		solenoids.put("gripper", Robot.jaws);
//		solenoids.put("jaws", Robot.jaws);
//		solenoids.put("gears", Robot.gearShifter);
//		solenoids.put("climberBar", Robot.climbingBar);
//		solenoids.put("ratchet", Robot.ratchet);
//
//	}
//
//	private void parse(BufferedReader fin) {
//		while (true) {
//			// declare all local variables up front because it errors out if we don't because...
//			// because Java scoping is weird.  I have no idea why.
//			
//			Direction direction;
//			// boxed values because they can be null
//			Boolean clockwise = null;
//			Double radius = null;
//			double speed = default_speed;
//			Length distance;
//			
//			try (Scanner s = new Scanner(fin.readLine().toLowerCase())) {
//				if(!s.hasNext()) return;
//				switch (s.next()) {
//				// DRIVE 3 FEET FORWARD 68% RADIUS 5 CLOCKWISE
//				// (tokens can be in any order!)
//				case "drive":
//					while (s.hasNext()) {
//						if (s.hasNext("(forward|backward)(s)?")) {
//							direction = Direction.valueOf(s.next().toUpperCase());
//						} else if (s.hasNext("//i(radius)")) {
//							s.next();
//							radius = s.nextDouble();
//						} else if (s.hasNext("(clockwise|counterclockwise)"))
//							clockwise = s.next().equalsIgnoreCase("clockwise");
//						else if(s.hasNext("speed"))
//							speed = s.nextDouble();
//						else if(s.hasNext("[0-9]{1:2}(\\.[0-9]*)?%")) {
//							String percentage = s.next();
//							speed = Double.valueOf(percentage.substring(0, percentage.length()-1)) / 100.0;
//							else if(s.hasNextDouble()) {
//								
//							}
//						}
//					}
//
//					
//					if(clockwise == null && radius == null) {
//						// no turn
//						add(new DriveTrainAutoDrive(Robot.driveTrain, speed, direction, distance.convertTo(LengthUOM.Inches).getValue()));
//					} else if(clockwise != null && radius != null) {
//						add(new DriveTrainAutoDrive(Robot.driveTrain, speed, direction, distance.convertTo(LengthUOM.Inches).getValue(), current_speed, speed, radius, clockwise));
//					}
//
//					/*
//					 * direction = Direction.valueOf(s.next().toUpperCase()); double distance =
//					 * s.nextDouble(); if (s.hasNext()) { String token = s.next(); if
//					 * (s.next().equalsIgnoreCase("RADIUS")) { s.next(); final double curve =
//					 * s.nextDouble(); final boolean clockwise; String st; switch (st =
//					 * s.next().toLowerCase()) { case "clockwise": clockwise = true; break; case
//					 * "counterclockwise": clockwise = false; break; default: throw new
//					 * IllegalArgumentException( "expected 'clockwise' or 'counterclockwise', got "
//					 * + st); } add(new DriveTrainAutoDrive(Robot.driveTrain, distance, direction,
//					 * speed, startSpeedFactor, endSpeedFactor, curve, clockwise)); } else throw new
//					 * ParseException("") } else add(new DriveTrainAutoDrive(Robot.driveTrain,
//					 * distance, direction, speed, startSpeedFactor, endSpeedFactor));
//					 */
//					break;
//				case "stop":
//					add(new DriveTrainAutoStop(Robot.driveTrain));
//					break;
//
//				case "move":
//					Motor motor = motors.get(s.next().toLowerCase());
//					String token = s.next().toUpperCase();
//					if (token.equals("TO")) {
//						if (s.hasNextDouble()) {
//
//							double position = s.nextDouble();
//							LengthUOM unit = null;
//							String unitName = s.next();
//							for (@SuppressWarnings("rawtypes")
//							UOM thisUnit : UOM.allUnits.get(SystemOfMeasurement.Length)) {
//								if ((position == 1 ? thisUnit.getUnitNameSingular() : thisUnit.getUnitNamePlural())
//										.equalsIgnoreCase(unitName)) {
//									unit = (LengthUOM) thisUnit;
//									break;
//								}
//							}
//							if (unit == null) {
//								throw new IllegalArgumentException("Unknown unit of measurement: " + unitName);
//							}
//
//							add(new MotorMoveToAbsolutePosition(motor, unit.create(position)));
//						}
//					} else {
//
//					}
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private void add(Command cmd) {
//		if (async)
//			addParallel(cmd);
//		else
//			addSequential(cmd);
//		async = false;
//	}
//}
