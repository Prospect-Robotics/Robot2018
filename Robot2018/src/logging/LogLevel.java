package logging;

import java.util.Arrays;

public enum LogLevel {
	/**
	 * Like {@link LogLevel#ALL all}, but with traceback
	 */
	TRACEBACK(true, LogType.values()),
	/**
	 * Prints everything
	 */
	ALL(LogType.values()),
	/**
	 * Prints {@link LogType#DEBUG debugs} along with {@link LogType#WARNING
	 * warnings} and {@link LogType#ERROR errors}, <b>does not</b> print
	 * {@link LogType#INFO info}
	 */
	DEBUG(LogType.INFO, LogType.DEBUG, LogType.WARNING, LogType.ERROR),
	INFO(LogType.INFO, LogType.WARNING, LogType.ERROR),
	ISSUE(LogType.WARNING, LogType.ERROR),
	ERROR(LogType.ERROR),
	NONE();
	private LogType[] includedLevels;
	boolean showTrace = false;//

	LogLevel(LogType... severities) {
		includedLevels = severities;
	}

	LogLevel(boolean showTrace, LogType... severities) {
		this(severities);
		this.showTrace = showTrace;
	}

	boolean isIncluded(LogType severity) {
		if (severity == LogType.ALWAYS) {
			return true;
		}
		return Arrays.asList(includedLevels).contains(severity);
	}
}
