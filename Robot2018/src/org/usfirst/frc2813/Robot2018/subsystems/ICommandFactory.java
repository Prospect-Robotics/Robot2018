package org.usfirst.frc2813.Robot2018.subsystems;

import edu.wpi.first.wpilibj.command.Command;

public interface ICommandFactory<T> {
	public Command createCommand(T subsystem);
}

