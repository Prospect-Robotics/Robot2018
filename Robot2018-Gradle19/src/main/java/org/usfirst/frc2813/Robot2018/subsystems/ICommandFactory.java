package org.usfirst.frc2813.Robot2018.subsystems;

import org.usfirst.frc2813.Robot2018.commands.GearheadsCommand;

public interface ICommandFactory<T> {
	public GearheadsCommand createCommand(T subsystem);
}

