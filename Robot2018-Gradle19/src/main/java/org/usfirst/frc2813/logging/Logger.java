package org.usfirst.frc2813.logging;

import java.util.ArrayList;

import org.usfirst.frc2813.util.Formatter;

/**
 * 
 * @author Adrian Guerra
 *
 */
public class Logger {
	private static LogLevel loggingLevel = LogLevel.INFO;
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
		if (!loggingLevel.isIncluded(severity)) {
			return;
		}//TODO make cleaner
		StringBuilder finalPrint = new StringBuilder();
		finalPrint.append(readableTime(System.currentTimeMillis()));
		if (loggingLevel.showTrace) {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			System.out.println(Formatter.concat((Object[]) trace));// TODO remove getStackTrace() from trace
		} else {
			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			for (int i = trace.length - 1; i > 0; i--) {
				if (knownClasses.contains(trace[i].getClassName())) {
					finalPrint.append(simplifyPackage(trace[i].getClassName()));
					finalPrint.append(' ');
				}
			}
		}
		if (loggingLevel.isIncluded(severity)) {
			if (objects.length == 0) {
				finalPrint.append("Nothing to log");
			} else {
				for(Object o : objects) finalPrint.append(o.toString());
			}
            if (finalPrint.length() == 0) {
                System.out.println("We got an empty trace string that we're about to print to the console. That's a bug.");
                (new Throwable()).printStackTrace();
                return;
            }
            severity.level.print(finalPrint.toString());
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
		if (!loggingLevel.isIncluded(severity)) {
			return;
		}//TODO make cleaner
		String formatted = Formatter.safeFormat(format, objects);
		print(severity, formatted);
	}

	//TODO jaavdoc
	public static void printLabelled(LogType severity,String title,Object...objects) {
		if (!loggingLevel.isIncluded(severity)) {
			return;
		}//TODO make cleaner
		StringBuilder finalPrint = new StringBuilder();
		finalPrint.append(title).append(": ");
		for(int i=0;i<objects.length;i++) {
			finalPrint.append(objects[i]);
			if(i%2==0) {//check if current object is label or value
				finalPrint.append(":");//put colon between pair
			}
			else {
				finalPrint.append(i+1==objects.length?"":", ");//put comma after pair if not on the last pair
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
			if (trace[i].getClassName().equals(Logger.class.getName())) {
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
	
	/**
	 * Converts {@link System#currentTimeMillis()} to easily readable format
	 * 
	 * @param ms - {@link System#currentTimeMillis()}
	 * @return {@code [minutes:seconds:milliseconds]}
	 */
	public static String readableTime(long ms) {
		short milliseconds = (short) (ms%1000);
		short seconds = (short) ((ms/1000)%60);
		short minutes = (short) ((ms/60000)%60);
		return Formatter.safeFormat("[%02d:%02d:%03d]",minutes,seconds,milliseconds);
	}
}
