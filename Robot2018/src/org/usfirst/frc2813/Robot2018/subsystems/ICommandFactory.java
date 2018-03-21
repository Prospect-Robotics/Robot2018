package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

import edu.wpi.first.wpilibj.command.Command;

public interface ICommandFactory<T> {
	public GearheadsCommand createCommand(T subsystem);
}

