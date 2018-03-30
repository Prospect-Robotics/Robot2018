package org.usfirst.frc2813.logging;

import java.util.Arrays;

public enum LogLevel {
	/**
	 * Like {@link LogLevel#ALL all}, but with traceback prints on <b>all</b> log levels
	 */
	TRACEBACK(true, LogType.values()),
	
	/**
	 * Prints everything, regardless of log level
	 */
	ALL(LogType.values()),
	
	/**
	 * Like {@link LogLevel#ALL all}, but more verbose
	 */
	DEBUG(LogType.values()),
	
	/**
	 * <p>Prints logs of type {@link LogType#INFO info}, {@link LogType#WARNING warning}, &amp; {@link LogType#ERROR error}.</p>
	 * <p>Does not print logs of type {@link LogType#DEBUG debug}.</p>
	 */
	INFO(LogType.INFO, LogType.WARNING, LogType.ERROR),
	
	/**
	 * <p>Prints logs of type {@link LogType#WARNING warning} &amp; {@link LogType#ERROR error}.</p>
	 * <p>Does not print logs of type {@link LogType#DEBUG debug} or {@link LogType#INFO info}.</p>
	 */
	ISSUE(LogType.WARNING, LogType.ERROR),
	
	/**
	 * <p>Prints logs of type {@link LogType#ERROR error}.</p>
	 * <p>Does not print logs of type {@link LogType#WARNING warning}, {@link LogType#DEBUG debug}, or {@link LogType#INFO info}.</p>
	 */
	ERROR(LogType.ERROR),
	
	/**
	 * <p>Prints nothing</p>
	 * <p>Logs of level {@link LogType#ALWAYS always} <b>will still be printed</b></p>
	 */
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
