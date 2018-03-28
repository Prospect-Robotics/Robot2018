package org.usfirst.frc2813.Robot2018.commands;

/**
 * Create a command directed at a target object 
 */
public abstract class TargetedCommand<TARGET_TYPE> extends GearheadsCommand {
	/**
	 * The target
	 */
	protected final TARGET_TYPE target;	
	/**
	 * Create a command with a target object
	 * @param target the target of the command
	 */
	public TargetedCommand(TARGET_TYPE target, CommandDuration duration) {
		super(duration);
		this.target = target;
		if(this.target == null) {
			throw new IllegalArgumentException("Constructor was passed a null value.");
		}
		addArg("target", target);
	}
	/**
	 * Create a command with a target object
	 * @param target the target of the command
	 */
	public TargetedCommand(TARGET_TYPE target) {
		this(target, CommandDuration.DISABLED);
	}
	
	/**
	 * Get the target object this command operates on
	 * @param subsystem The target object
	 */
	public final TARGET_TYPE getTarget() {
		return this.target;
	}
}
