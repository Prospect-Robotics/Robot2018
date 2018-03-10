package org.usfirst.frc2813.Robot2018;

/**
 * Very simple logger. Takes a name and saves you passing the name each time.
 */
enum LogLevel {DEBUG, INFO, WARN, ERROR}

public class Log {
	private String label;
	private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.WARN;
	private LogLevel baseLogLevel;
	public Log(String name, LogLevel defaultLogLevel) {
		baseLogLevel = defaultLogLevel;
		label = name + ": ";
	}
	public Log(String name) {
		this(name, DEFAULT_LOG_LEVEL);
	}
	public void print(String s, LogLevel level) {
		if (level == LogLevel.ERROR) {
			System.err.println(label + s);
		}
		else if (level.compareTo(baseLogLevel) >= 0) {
			System.out.println(label + s);
		}
	}
	public void print(String s) {
		print(s, LogLevel.INFO);
	}
}