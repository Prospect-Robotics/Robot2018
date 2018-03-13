package logging;

import java.util.Arrays;

public class Logger {
	public static LogLevel loggingLevel = LogLevel.ISSUE;
	public static void setLoggingLevel(LogLevel level) {
		loggingLevel = level;
	}

	public static void print(LogType severity, Object... objects) {
		if (loggingLevel.showTrace) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println(Arrays.toString(trace));//TODO remove getStackTrace() from trace
			//TODO remove 'org.usfirst.frc2813.Robot2018' from trace print to help with readability
		}
		if (loggingLevel.isIncluded(severity)) {
			if (objects.length == 0) {
				System.err.println("Nothing to log...");
			} else if (objects.length == 1) {
				System.out.println(objects[0]);
			} else {
				System.out.println(Arrays.deepToString(objects));
			}
		}
	}
	public static void always(Object...objects) {
		print(LogType.ALWAYS,objects);
	}
	
	public static void debug(Object...objects) {
		print(LogType.DEBUG,objects);
	}
	
	public static void info(Object...objects) {
		print(LogType.INFO,objects);
	}
	
	public static void warning(Object...objects) {
		print(LogType.WARNING,objects);
	}
	
	public static void error(Object...objects) {
		print(LogType.ERROR,objects);
	}
	
}
