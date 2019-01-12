[Packages]

Each subsystem has a package under org.usfirst.frc2813.Robot2018.subsystems.<subsystem-name>
Each subsystem has a package for commands under org.usfirst.frc2813.Robot2018.commands.<subsystem-name>

[Command Names]

The naming convention is: <subsystem-or-subject-singular-in-camel-case><verb-or-action->

Examples: MotorMove, MotorStop, MotorDisable, ... kind of thing

[Command Hierarchy]

GearheadsCommand - Adds support for CommandDuration capabilities (see below).
TargettedCommand - Adds generic support for a command with a direct object/target.
SubsystemCommand - Adds generic support for a command that targets a subsystem, with enhanced functionality.  Adds Lockout capability (see below).

[Special Features]

These features are for handling special cases generically using the same commands, like locking the object to prevent changes, sync/async execution, timeouts on error, and timed commands.

CommandDuration - This configuration object has a CommandDurationType which can take four forms:
CommandDurationType.Disabled - the paramter will have no effect
CommandDurationType.Forever - the command will take effect forever, though it may still complete asynchronously -- when the side effects can be made permanent.
CommandDurationType.Timeout - the command will run until finished, interrupted, or timed out (error timeout)
CommandDurationType.Timed   - the command will run until finished, interrupted, or a specific duration of time has elapsed ( a timed command ).
CommandDurationType.Asynchronous - the command should run in the background.  isFinished will return true, regardless of what the subclass says.

Lockout.Disabled - the parameter will have no effect.
Lockout.WhileRunning - the command will take control of the subsystem and try to prevent anyone else from using it, not interruptable, but will release it if it finishes normally.
Lockout.UntilUnlocked - the command will take control of the subsystem and try to prevent anyone else from using it, not interruptable, but if it's asynchronous it will leave the subsystem locked!

[Implementation Notes]

The methods that you normally override have been made final, with equivalent protected/abstract functions for subclasses to use.  There are two layers to that- one at GearheadsCommand and one at SubsystemCommand.

