package org.usfirst.frc2813.Robot2018.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.usfirst.frc2813.Robot2018.Robot;
import org.usfirst.frc2813.Robot2018.commands.PIDStop;
import org.usfirst.frc2813.Robot2018.commands.drivetrain.AutoDriveSync;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidSetStateInstant;
import org.usfirst.frc2813.Robot2018.commands.solenoid.SolenoidToggleStateInstant;
import org.usfirst.frc2813.units.Direction;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *  Not yet complete lexer for this little lanuage I thought up (it's case insensitive):
 * <p><code>
 * OPEN JAWS
 * RUN INTAKE IN FOREVER
 * DRIVE FORWARD 1 FEET
 * CLOSE JAWS
 * WAIT 0.1 SECONDS
 * STOP INTAKE
 * ASYNC MOVE ELEVATOR TO 54 INCHES
 * ASYNC MOVE ARM TO 45 DEGREES
 * DRIVE FORWARD 5 FEET RADIUS 5 CLOCKWISE
 * AWAIT ;; wait for all commands started with async to finish
 * DRIVE FORWARD 6 INCHES RADIUS 5 CLOCKWISE
 * RUN INTAKE OUT FOR 2 SECONDS
 * </code></p><p>
 * So it's basically COBOL.
 * <p/><p/><p/><p/>
 * Which is at least better than Java.
 * <p><code>
 * autoCmdList.drive.drive(Direction.FORWARD, LengthUOM.FEET.create(1);
 * autoCmdList.intake.moveSync(Direction.FORWARD, 
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
		if(!initialized)
			initialize();
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
	private HashMap<String, Command> vars = new HashMap<String, Command>();
	private String assigningVariable;
	
	private static final HashMap<String, Motor> motorSubsystems;
	private static final HashMap<String, Solenoid> solenoidSubsystems;
	private static boolean initialized=false;
	
	private static void initialize() {
		initialized=true;
		motorSubsystems.put("arm", Robot.arm);
		motorSubsystems.put("elevator", Robot.elevator);
		motorSubsystems.put("intake", Robot.intake);
		solenoidSubsystems.put("gripper", Robot.jaws); // alias for the jaws
		solenoidSubsystems.put("jaws", Robot.jaws);
	}

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
					add(new AutoDriveSync(Robot.driveTrain, distance, direction, speed, startSpeedFactor,
							endSpeedFactor, curve, clockwise));
				} else
					add(new AutoDriveSync(Robot.driveTrain, distance, direction, speed, startSpeedFactor,
							endSpeedFactor));
				break;
			case "stop":
				add(new PIDStop());
				break;
			case "gripper":
				String token = s.next().toUpperCase();
				if (token.equals("TOGGLE"))
					addInstant(new SolenoidToggleStateInstant(Robot.jaws));
				else
					addInstant(new SolenoidSetStateInstant(Robot.jaws, Direction.valueOf(token)));
				break;
			case "intake":
				add(new MotorRunInDirectionSync(Robot.intake, Direction.valueOf(s.next().toUpperCase()));
				break;
			case "elevator":
				String token = s.next().toUpperCase();
				if(token.equals("goto")) {
					double pos = s.nextDouble();
					add(new MotorMoveToPositionSync(Robot.elevator, LengthUOM.valueOf(s.next().toUpperCase()).create(pos));
				} else {
					add(new MotorMoveInDirectionAsync());
				}
				break;  
			
			// TODO ADD MORE CASES
			
			// command architecture		    
			case "move":		    
				Motor motor = motorSubsystems.get(s.next().toLower());
				String action = s.next().toUpperCase();
				if(action.equals("TO"))
					if(s.hasNextDouble()) {
						// it's a position
						double position  = s.nextDouble();
						LengthUOM unit = LengthUOM.valueOf(s.next().toUpperCase());
						addInstant(new MotorMoveToPositionAsync(motor, unit.create(position));
					}
					else {
						String position = s.next().toLowerCase();
						if(position.equals("bottom"))
			case "cancel":
				Command cmdToCancel = 
			// variables
			default:
				String varname = s.next();
				if(!s.next().equals("="))
					throw new IllegalArgumentException("Unknown command "+varname);
				assigningVariable = varname;
				break;
			}
		}
	}

	private void add(Command cmd) {
		if (async) {
			addParallel(cmd);
			if(assigningVariable != null) {
				variables.put(assigningVariable, cmd);
				assigningVariable=null;
			}
		} else {
			assert assigningVariable == null : "Cannot assign a non-async command to a variable";
			addSequential(cmd);
		}
		async = false;
		
	}
					    
	private void addInstant(InstantCommand cmd) {
		assert !async : "Cannot async an instant command";
		assert assigningVariable==null : "Cannot assign instant commands to variables (they complete instantly, what would be the point?)";
		addSequential(cmd);
}
							   
							   
/* DRIVE FORWARD 2 FEET RADIUS 1 CLOCKWISE
 * MOVE INTAKE OUT FOR 1 SECOND
 * DRIVE BACKWARD 1 FEET
 * ASYNC MOVE ELEVATOR TO BOTTOM
 * ASYNC MOVE ARM TO LEVEL_POSITION
 * DRIVE BACKWARD 1 FEET
 * AWAIT           ; wait for all commands started with ASYNC to complete
 * OPEN JAWS
 * RUN INTAKE IN FOREVER
 * DRIVE FORWARD 1 FEET
 * CLOSE JAWS
 * STOP INTAKE
 * ASYNC MOVE ELEVATOR TO 54 INCHES
 * DRIVE BACKWARD 5 FEET RADIUS 1 COUNTERCLOCKWISE
 * DRIVE FORWARD 30 FEET RADIUS 5 CLOCKWISE
 * AWAIT
 * DRIVE FORWARD 2 FEET RADIUS 5 CLOCKWISE
 * OPEN JAWS
 */
