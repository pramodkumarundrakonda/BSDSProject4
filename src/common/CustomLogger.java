package common;

/**
 * A simple logging class to standardize log messages for the application.
 * For simplicity, it just prints messages to the standard output.
 * In a real-world application, you'd likely want to use a more sophisticated logging framework.
 */
public class CustomLogger {

    // Enum for log levels to control the verbosity
    public enum LogLevel {
        INFO,
        DEBUG,
        WARN,
        ERROR
    }

    private LogLevel logLevel;

    public CustomLogger() {
        // Default log level set to INFO
        this.logLevel = LogLevel.INFO;
    }

    public CustomLogger(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    // Logs an informational message
    public void info(String message) {
        if (this.logLevel.ordinal() <= LogLevel.INFO.ordinal()) {
            System.out.println("[INFO] " + message);
        }
    }

    // Logs a debug message
    public void debug(String message) {
        if (this.logLevel.ordinal() <= LogLevel.DEBUG.ordinal()) {
            System.out.println("[DEBUG] " + message);
        }
    }

    // Logs a warning message
    public void warn(String message) {
        if (this.logLevel.ordinal() <= LogLevel.WARN.ordinal()) {
            System.out.println("[WARN] " + message);
        }
    }

    // Logs an error message
    public void error(String message) {
        if (this.logLevel.ordinal() <= LogLevel.ERROR.ordinal()) {
            System.err.println("[ERROR] " + message);
        }
    }

    // Set the log level for the logger
    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    // Get the current log level
    public LogLevel getLogLevel() {
        return logLevel;
    }
}
