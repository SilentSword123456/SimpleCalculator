import com.sun.jna.platform.win32.WinNT;

import java.io.File;

/**
 * The Main class serves as the entry point for the SimpleCalculator application.
 * It initializes necessary configurations and launches the graphical user interface (GUI).
 */
public class Main {

    /**
     * Represents the current version of the application.
     * This value is used to display the software version and track updates.
     */
    //Initialize initial values for the base program
    public static String version = "2.0";
    /**
     * The name of the application. This variable holds the title or identifier
     * for the SimpleCalculator application, used for display or logging purposes.
     */
    public static String name = "SimpleCalculator";
    /**
     * A static array of allowed characters for input validation within the SimpleCalculator application.
     * This array defines the set of characters that can be used in arithmetic expressions,
     * including numeric digits and basic arithmetic operators.
     */
    public static char[] allowedCharacters = {'1','2','3','4','5','6','7','8','9','0','+','-','*','/'};
    /**
     * Represents the maximum allowed length for an equation in the SimpleCalculator application.
     * This variable ensures that user inputs do not exceed a predefined character limit,
     * maintaining operational consistency and preventing overly complex calculations.
     */
    public static int maxEcuationLenght = 100;

    /**
     * The main method serves as the entry point of the SimpleCalculator application.
     * It initializes the application's configuration and launches the graphical
     * user interface for user interaction.
     *
     * @param args Command-line arguments passed to the application. These arguments
     *             are not utilized in this implementation.
     */
    public static void main(String[] args) {
        //Call the base functions to start the program

        Initialize();

        //Generate a calculator
        GUI calculator = new GUI();
        //launch the calculator
        calculator.Launch();
    }

    /**
     * Initializes the application by creating necessary local directories and logging the startup process.
     *
     * This method performs the following actions:
     * 1. Ensures that a dedicated folder exists in the user's AppData directory to store application files.
     *    If the folder does not exist, it is created.
     * 2. Logs the application's launch event using the logging system and writes a message to the Event Viewer.
     * 3. Outputs a welcome message with the application name and version to the console.
     */
    private static void Initialize(){
        //check if we have a dedicated folder in <username>\appdata to write essential fiels to
        File folder = new File("C:\\Users\\" +  System.getProperty("user.name") + "\\AppData\\Local\\" + name);
        if (!folder.exists())
            //create the folder if it dosent exist
            folder.mkdirs();

        //log the start of the application
        Logger.log("Calculator launched", (byte) WinNT.EVENTLOG_INFORMATION_TYPE);
        System.out.println("Welcome to " + name + " v." + version);
    }

}