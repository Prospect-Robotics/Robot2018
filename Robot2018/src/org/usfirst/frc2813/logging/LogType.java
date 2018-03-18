package org.usfirst.frc2813.logging;

public enum LogType {
	/**
	 * Prints within the {@link LogLevel#DEBUG debug} logging level.
	 * Use to leave not normally printed message in the code.
	 */
	DEBUG(PrintLevel.DEFAULT),
	/**
	 * The default log level. Prints within the {@link LogLevel#INFO info}
	 * logging level as well as any more verbose levels such as {@link LogLevel#DEBUG debug}
	 */
	INFO(PrintLevel.DEFAULT),
	/**
	 * Prints within all log levels <b>except</b> {@link LogLevel#NONE none}
	 */
	WARNING(PrintLevel.WARNING),
	/**
	 * Prints to all log levels
	 */
	ERROR(PrintLevel.ERROR),
	/**
	 * Prints regardless of level, overrides {@link LogLevel#NONE none}
	 */
	ALWAYS(PrintLevel.DEFAULT);
	
	PrintLevel level;
	LogType(PrintLevel level){
		this.level=level;
	}
}