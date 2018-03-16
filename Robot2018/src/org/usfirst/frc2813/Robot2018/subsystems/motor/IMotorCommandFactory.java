package org.usfirst.frc2813.Robot2018.subsystems.motor;

import edu.wpi.first.wpilibj.command.Command;

public interface IMotorCommandFactory {
	public Command createMotorCommand(Motor motor);
}
