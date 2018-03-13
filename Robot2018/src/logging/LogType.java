package logging;

public enum LogType {
	/**
	 * Prints within the {@link LogLevel#DEBUG debug} logging level.
	 * Use to leave not normally printed message in the code.
	 */
	DEBUG,
	/**
	 * The default log level. Prints within the {@link LogLevel#INFO info}
	 * logging level as well as any more verbose levels such as {@link LogLevel#DEBUG debug}
	 */
	INFO,
	/**
	 * Prints within all log levels <b>except</b> {@link LogLevel#NONE none}
	 */
	WARNING,
	/**
	 * Prints to all log levels
	 */
	ERROR,
	/**
	 * prints regardless of level, overrides {@link LogLevel#NONE none}
	 */
	ALWAYS;
}