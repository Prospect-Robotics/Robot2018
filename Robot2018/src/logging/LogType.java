package logging;

public enum LogType {
	/**
	 * Prints within the {@link LogLevel#DEBUG debug} logging level
	 */
	DEBUG,
	/**
	 * Prints within the {@link LogLevel#INFO info} logging level
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