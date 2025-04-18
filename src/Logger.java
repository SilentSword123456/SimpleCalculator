import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Kernel32;

/**
 * The Logger class provides functionality to log messages to a local file and the Windows Event Viewer.
 *
 * This class is designed to record application events systematically for debugging and monitoring purposes.
 * Logs include timestamps, class context information, and message details. Messages can also be routed
 * to the Windows Event Viewer with specified severity levels, ensuring robust logging for system analysis
 * and troubleshooting.
 */
public class Logger {

    /**
     * Defines the file path for logging application events locally.
     *
     * This variable represents the absolute path to a log file where all application logs
     * will be stored. The path is constructed dynamically using the current user's
     * home directory and the application name, ensuring that logs are stored in a
     * user-specific directory within their AppData folder. The directory structure
     * follows the pattern:
     * C:\Users\[username]\AppData\Local\[application name]\logs.txt
     *
     * This ensures that logs are organized and stored in a secure, application-specific
     * location. The log file serves as a persistent storage for tracking application behavior
     * and debugging purposes.
     */
    //initialize the file in witch we will be writing logs, witch is in user\appdata\Main.getName\logs.txt
    private static final String logsFilePath = "C:\\Users\\" +  System.getProperty("user.name") + "\\AppData\\Local\\" + Main.name + "\\logs.txt";

    /**
     * Logs the specified message to a file with timestamp, class context information,
     * and forwards it to the Windows Event Viewer with the specified severity level.
     *
     * This method constructs a log entry containing the current timestamp, the class
     * that invoked the method, and the provided message. It appends the log entry
     * to a specified log file. Additionally, it sends the log message to the Event Viewer
     * based on the provided severity level. If an error occurs during the file writing
     * process, the exception is printed to the standard error stream.
     *
     * @param message The message to be logged.
     * @param eventLevel The severity level of the log message (e.g., informational, warning, or error).
     */
    public static void log(String message, byte eventLevel){
        //construct the final log message
        String logEntry = "[" + getTime() + "] [Class: " + Thread.currentThread().getStackTrace()[2].getClassName() + "] " + message;

        //try to log the message
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logsFilePath, true))) {
            //log the message
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //log in the event viewer
        logInEventViewer(message, eventLevel);

    }

    /**
     * Logs the specified message to a file with timestamp and class context information.
     *
     * This method constructs a log entry containing the current timestamp, the class
     * that invoked the method, and the provided message. It appends the log entry to a
     * specified log file. If an error occurs during the file writing process, the exception
     * is printed to the standard error stream.
     *
     * @param message The message to be logged.
     */
    public static void log(String message){
        //construct the final log message
        String logEntry = "[" + getTime() + "] [Class: " + Thread.currentThread().getStackTrace()[2].getClassName() + "] " + message;

        //try to log the message
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logsFilePath, true))) {
            //log the message
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Gets the current date and time in the format "yyyy-MM-dd HH:mm:ss".
     *
     * This method uses the system's current local date and time,
     * formats it using a predefined pattern, and returns the formatted
     * date-time as a string.
     *
     * @return A string representing the current date and time in the "yyyy-MM-dd HH:mm:ss" format.
     */
    //basic function that gets the time
    private static String getTime(){
        //gets the formater
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //returns the time in a format type as formatter
        return LocalDateTime.now().format(formatter);
    }


    /**
     * Sends a log message to the Windows Event Viewer with the specified severity level.
     *
     * This method registers the application as an event source, sends the provided message
     * to the Event Viewer, and cleans up the event source handle after logging. If sending
     * the log fails, it retrieves the Windows error code and logs it using the local logging system.
     *
     * @param message The log message to be sent to the Event Viewer.
     * @param eventLevel The severity level of the log message (e.g., information, warning, or error).
     */
    private static void logInEventViewer(String message, byte eventLevel) {
        // Register the event source (usually your application name).
        // This tells Windows you're sending a log from "Main.name".
        WinNT.HANDLE hEventLog = Advapi32.INSTANCE.RegisterEventSource(null, Main.name);

        // If the handle couldn't be created, log an error and return.
        if (hEventLog == null) {
            log("Failed to register event source"); // try to at least log in the logs.txt file
            return;
        }

        // The message to send. You can send multiple lines by using more strings in this array.
        String[] messages = {message};

        // Send the event to Windows Event Viewer.
        // Parameters:
        // - hEventLog: the handle to the event source
        // - eventLevel: severity type (INFO, WARNING, ERROR)
        // - 0: event category (custom if needed, 0 is fine)
        // - 0x1000: event ID (any number you assign)
        // - null: no specific user SID (security ID)
        // - messages.length: number of message strings
        // - 0: no binary data
        // - messages: the message strings array
        // - null: no binary data attached
        boolean success = Advapi32.INSTANCE.ReportEvent(
                hEventLog,
                eventLevel,
                0,
                0x1000,
                null,
                messages.length,
                0,
                messages,
                null
        );

        // If logging failed, retrieve the Windows error code and use your own logger to record it
        if (!success) {
            int errorCode = Kernel32.INSTANCE.GetLastError(); // Get the Windows error code
            log("Failed to report event. Error code: " + errorCode, eventLevel);
        }

        // Always release the event source handle to free resources
        Advapi32.INSTANCE.DeregisterEventSource(hEventLog);
    }
}
