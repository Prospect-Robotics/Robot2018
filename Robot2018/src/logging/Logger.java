package logging;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @author Adrian Guerra
 *
 */
public class Logger {
	private static LogLevel loggingLevel = LogLevel.ISSUE;
	private static ArrayList<String> knownClasses = new ArrayList<String>();

	public static void setLoggingLevel(LogLevel level) {
		loggingLevel = level;
	}

	public static LogLevel getLoggingLevel() {
		return loggingLevel;
	}

	/**
	 * the code<br>
	 * <code>
	 * Logger.print(LogType.DEBUG, "Hello", "World", 1, 2, 3, new int[] { 1, 2, 3 });
	 * </code><br>
	 * will return<br>
	 * <code>
	 * [Hello, World, 1, 2, 3, [1, 2, 3]]
	 * </code><br>
	 * <br>
	 * the code<br>
	 * <code>
	 * Logger.print(LogType.DEBUG, "Hello World");
	 * </code><br>
	 * will return<br>
	 * <code>
	 * Hello World
	 * </code>
	 * 
	 * @param severity
	 *            - {@link LogType} object used along with {@link LogLevel} to
	 *            determie what should be printed
	 * @param objects
	 *            - see examples above
	 * 
	 * @author Adrian Guerra
	 */
	public static void print(LogType severity, Object... objects) {
		String finalPrint = "";
		if (loggingLevel.showTrace) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println(Arrays.toString(trace));// TODO remove getStackTrace() from trace
			// TODO remove 'org.usfirst.frc2813.Robot2018' from trace print to help
			// readability
		} else {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for (int i = trace.length - 1; i > 0; i--) {
				if (knownClasses.contains(trace[i].getClassName())) {
					finalPrint += simplifyPackage(trace[i].getClassName());
					finalPrint += " ";
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
	 * 
	 * @param severity
	 *            - {@link LogType} object used along with {@link LogLevel} to
	 *            determie what should be printed
	 * @param format
	 *            - format string passed into
	 *            {@link String#format(String, Object...) String.format}
	 * @param objects
	 *            - args passed into {@link String#format(String, Object...)
	 *            String.format}
	 * 
	 * @see String#format(String, Object...)
	 * @see Logger#print(LogType, Object...)
	 */
	public static void printFormat(LogType severity, String format, Object... objects) {
		String formatted = String.format(format, objects);
		print(severity, formatted);
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
				knownClasses.add(trace[i + 1].getClassName());// TODO double check this
				break;
			}
		}
	}

	private static String simplifyPackage(String longName) {
		String[] segments = longName.split("\\.");
		if (segments.length < 2) {
			return longName;
		} else {
			return segments[segments.length - 1];
		}
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#ALWAYS ALWAYS}
	 * 
	 * @param objects
	 * @see #print(LogType, Object...)
	 */
	public static void always(Object... objects) {
		print(LogType.ALWAYS, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#DEBUG DEBUG}
	 * 
	 * @param objects
	 * @see #print(LogType, Object...)
	 */
	public static void debug(Object... objects) {
		print(LogType.DEBUG, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#INFO INFO}
	 * 
	 * @param objects
	 * @see #print(LogType, Object...)
	 */
	public static void info(Object... objects) {
		print(LogType.INFO, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#WARNING WARNING}
	 * 
	 * @param objects
	 * @see #print(LogType, Object...)
	 */
	public static void warning(Object... objects) {
		print(LogType.WARNING, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#ERROR ERROR}
	 * 
	 * @param objects
	 * @see #print(LogType, Object...)
	 */
	public static void error(Object... objects) {
		print(LogType.ERROR, objects);
	}

	/**
	 * Version of {@link #printFormat(Logtype,String, Object...)} with a
	 * {@link LogType} of {@link LogType#ALWAYS ALWAYS}
	 * 
	 * @param format
	 * @param objects
	 * @see #printFormat(Logtype,String, Object...)
	 */
	public static void formatAlways(String format, Object... objects) {
		printFormat(LogType.ALWAYS,format, objects);
	}

	/**
	 * Version of {@link #printFormat(Logtype,String, Object...)} with a
	 * {@link LogType} of {@link LogType#DEBUG DEBUG}
	 * 
	 * @param format
	 * @param objects
	 * @see #printFormat(Logtype,String, Object...)
	 */
	public static void formatDebug(String format, Object... objects) {
		printFormat(LogType.DEBUG,format, objects);
	}

	/**
	 * Version of {@link #printFormat(Logtype,String, Object...)} with a
	 * {@link LogType} of {@link LogType#INFO INFO}
	 * 
	 * @param format
	 * @param objects
	 * @see #printFormat(Logtype,String, Object...)
	 */
	public static void formatInfo(String format, Object... objects) {
		printFormat(LogType.INFO,format, objects);
	}

	/**
	 * Version of {@link #printFormat(Logtype,String, Object...)} with a
	 * {@link LogType} of {@link LogType#WARNING WARNING}
	 * 
	 * @param format
	 * @param objects
	 * @see #printFormat(Logtype,String, Object...)
	 */
	public static void formatWarning(String format, Object... objects) {
		printFormat(LogType.WARNING,format, objects);
	}

	/**
	 * Version of {@link #printFormat(Logtype,String, Object...)} with a
	 * {@link LogType} of {@link LogType#ERROR ERROR}
	 * 
	 * @param format
	 * @param objects
	 * @see #printFormat(Logtype,String, Object...)
	 */
	public static void formatError(String format, Object... objects) {
		printFormat(LogType.ERROR,format, objects);
	}

}
