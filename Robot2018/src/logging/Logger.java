package logging;

import java.util.ArrayList;
import java.util.Arrays;

public class Logger {
	private static LogLevel loggingLevel = LogLevel.ISSUE;
	private static ArrayList<String> knownClasses = new ArrayList<String>();

	public static void setLoggingLevel(LogLevel level) {
		loggingLevel = level;
	}

	public static void print(LogType severity, Object... objects) {
		String finalPrint = "";
		if (loggingLevel.showTrace) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println(Arrays.toString(trace));// TODO remove getStackTrace() from trace
			// TODO remove 'org.usfirst.frc2813.Robot2018' from trace print to help
			// readability
		} else {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for(int i=trace.length-1;i>0;i--) {
				if(knownClasses.contains(trace[i].getClassName())) {
					finalPrint+=simplifyPackage(trace[i].getClassName());
					finalPrint+=" ";
				}
			}
		}
		if (loggingLevel.isIncluded(severity)) {
			if (objects.length == 0) {
				finalPrint += "Nothing to log";
			} else if (objects.length == 1) {
				finalPrint += objects[0];
			} else {
				finalPrint += Arrays.deepToString(objects);
			}
		}
		System.out.println(finalPrint);
	}

	/**
	 * <p>
	 * Add yourself to {@link Logger}'s list of known classes
	 * </p>
	 */
	public static void addMe() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		for (int i = 0; i < trace.length; i++) {
			if (trace[i].getClassName() == Logger.class.getName()) {
				knownClasses.add(trace[i+1].getClassName());
				break;
			}
		}
	}

	public static void always(Object... objects) {
		print(LogType.ALWAYS, objects);
	}

	public static void debug(Object... objects) {
		print(LogType.DEBUG, objects);
	}

	public static void info(Object... objects) {
		print(LogType.INFO, objects);
	}

	public static void warning(Object... objects) {
		print(LogType.WARNING, objects);
	}

	public static void error(Object... objects) {
		print(LogType.ERROR, objects);
	}

	private static String simplifyPackage(String longName) {
		String[] segments = longName.split("\\.");
		System.out.println(segments.length);
		if(segments.length<2) {
			return longName;
		}
		else {
			return segments[segments.length-1];
		}
	}

}
