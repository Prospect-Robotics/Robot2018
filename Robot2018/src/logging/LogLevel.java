package logging;

import java.util.Arrays;

public enum LogLevel {
	TRACEBACK(true,Severity.values()),
	ALL(Severity.values()),
	DEBUG(Severity.DEBUG, Severity.WARNING, Severity.ERROR),
	ISSUE(Severity.WARNING, Severity.ERROR),
	ERROR(Severity.ERROR),
	NONE();
	private Severity[] includedLevels;
	boolean showTrace = false;
	
	LogLevel(Severity... severities) {
		includedLevels = severities;
	}
	
	LogLevel(boolean showTrace,Severity...severities){
		this(severities);
		this.showTrace = showTrace;
	}

	boolean isIncluded(Severity severity) {
		if (severity == Severity.ALWAYS) {
			return true;
		}
		return Arrays.asList(includedLevels).contains(severity);
	}
}
