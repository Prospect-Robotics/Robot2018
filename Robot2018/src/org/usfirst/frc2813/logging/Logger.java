package org.usfirst.frc2813.logging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * 
 * @author Adrian Guerra
 *
 */
public class Logger {
	private static LogLevel loggingLevel = LogLevel.INFO;
	private static ArrayList<String> knownClasses = new ArrayList<String>();
	private static Calendar calendar = Calendar.getInstance();
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
		calendar.setTimeInMillis(System.currentTimeMillis());
		finalPrint+=String.format("%s:%s:%s] ",calendar.MINUTE,calendar.SECOND,calendar.MILLISECOND );
		if (loggingLevel.showTrace) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println(Arrays.toString(trace));// TODO remove getStackTrace() from trace
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
            if (finalPrint.length() == 0) {
                System.out.println("We got an empty trace string that we're about to print to the console. That's a bug.");
                (new Throwable()).printStackTrace();
                return;
            }
            severity.level.print(finalPrint);
		}
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

	public static void printLabelled(LogType severity,String title,Object...objects) {
		String finalPrint = title+": ";
		for(int i=0;i<objects.length;i++) {
			finalPrint+=objects[i];
			if(i%2==0) {
				finalPrint+=":";
			}
			else {
				finalPrint+=i+1==objects.length?"":", ";
			}
		}
		print(severity,finalPrint);
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
		if(longName.contains("org.usfirst.2813.Robot2813")) {
			return longName.replace("org.usfirst.2813.Robot2813","");
		}
		else {
			String[] segments = longName.split("\\.");
			if (segments.length < 2) {
				return longName;
			} else {
				return segments[segments.length - 1];
			}
		}
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#ALWAYS ALWAYS}
	 * 
	 * @param objects stuff to print
	 * @see #print(LogType, Object...)
	 */
	public static void always(Object... objects) {
		print(LogType.ALWAYS, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#DEBUG DEBUG}
	 * 
	 * @param objects stuff to print
	 * @see #print(LogType, Object...)
	 */
	public static void debug(Object... objects) {
		print(LogType.DEBUG, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#INFO INFO}
	 * 
	 * @param objects stuff to print
	 * @see #print(LogType, Object...)
	 */
	public static void info(Object... objects) {
		print(LogType.INFO, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#WARNING WARNING}
	 * 
	 * @param objects stuff to print
	 * @see #print(LogType, Object...)
	 */
	public static void warning(Object... objects) {
		print(LogType.WARNING, objects);
	}

	/**
	 * Version of {@link #print(LogType, Object...)} with a {@link LogType} of
	 * {@link LogType#ERROR ERROR}
	 * 
	 * @param objects stuff to print
	 * @see #print(LogType, Object...)
	 */
	public static void error(Object... objects) {
		print(LogType.ERROR, objects);
	}
}
