import com.sun.jna.platform.win32.WinNT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The GUI class provides a graphical user interface for a calculator application.
 * This interface allows users to input equations, interact with buttons for base conversions,
 * and displays the results. It also handles various user inputs such as key presses and button clicks.
 */
public class GUI {

    /**
     * Represents the state of the last input received by the GUI.
     * The value of this variable can indicate the following:
     * - -1: The initial state before any input has been received.
     * -  0: Indicates that the last input was an operation (e.g., '+', '-', '*', '/').
     * -  1: Indicates that the last input was a numeric digit (e.g., '0' to '9').
     *
     * This variable is used internally by the GUI class to determine the context
     * of the last user interaction and handle subsequent inputs appropriately.
     */
    private byte lastInput = -1;
    // lastInput will be -1 for initialisation,0 if last input was an operation and 1 if last input was a digit

    /**
     * Launches the graphical user interface (GUI) for a calculator application,
     * providing the user with an interactive window to input values, convert
     * numbers between bases, and observe changes.
     *
     * This method performs the following operations:
     * 1. Initializes the main GUI window.
     * 2. Adds a label for user input display and interaction.
     * 3. Adds buttons for base conversion functionality:
     *    - Converts numbers to base 2.
     *    - Converts numbers to base 10.
     * 4. Sets up key listeners and button actions:
     *    - Captures user input via key presses.
     *    - Handles button clicks for base conversions.
     * 5. Logs significant actions:
     *    - Logs GUI-related events such as window launch and closure.
     *    - Logs user operations such as base conversion or errors during input processing.
     * 6. Handles errors gracefully using dialogs and logs to ensure feedback
     *    in case of invalid inputs.
     *
     * The window is displayed with predefined dimensions and non-resizable components,
     * accommodating user interaction and input processing functionality.
     */
    void Launch(){

        //Generate the gui window needed for the user interaction
        Logger.log("GUI launched", (byte) WinNT.EVENTLOG_INFORMATION_TYPE);
        JFrame frame = new JFrame(Main.name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.setSize(350,500);
        frame.setLayout(null);

        //generated the label in witch the user can type the equation
        JLabel label = new JLabel("Type an equation here");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        label.setPreferredSize(new Dimension(300, 100));
        label.setBounds(50, 100, 250, 50);

        //generated the button for converting the current number from in the label to base 2
        JButton convertToBase2Button = new JButton("Convert to base 2");
        convertToBase2Button.setForeground(Color.BLACK);
        convertToBase2Button.setFont(new Font("Arial", Font.BOLD, 18));
        convertToBase2Button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        convertToBase2Button.setFocusPainted(false);
        convertToBase2Button.setBounds(75, 250, 200, 25);

        //generated the button for converting the current number from in the label to base 10
        JButton convertToBase10Button = new JButton("Convert to base 10");
        convertToBase10Button.setForeground(Color.BLACK);
        convertToBase10Button.setFont(new Font("Arial", Font.BOLD, 18));
        convertToBase10Button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        convertToBase10Button.setFocusPainted(false);
        convertToBase10Button.setBounds(75, 300, 200, 25);

        //added all components to the window
        frame.add(label);
        frame.add(convertToBase2Button);
        frame.add(convertToBase10Button);
        StringBuilder text = new StringBuilder();

        //added listeners for a keypress, the 2 convert to 2/10 base buttons and for the windowClosing event
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                char keyChar = e.getKeyChar();

                //sent the information gathered to be processed
                updateInput(keyChar, keyCode, label, text);
            }
        });

        convertToBase2Button.addActionListener(e -> {
            //trying to convert the string in the label/text to a number, but it might fail if the user has simbols (ex.: "5+8" or "3*")
            try {
                int equation = Integer.parseInt(text.toString());
                text.setLength(0);
                text.append(Evaluator.convertToBaseN(equation, 10, 2));
                label.setText(text.toString());
            } catch (NumberFormatException ex) {
                //throw and error message and log the error
                JOptionPane.showMessageDialog(null, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.log("Tried to convert " + text + " to base 2!" , (byte) WinNT.EVENTLOG_ERROR_TYPE);
            }
        });

        convertToBase10Button.addActionListener(e -> {
            //trying to convert the string in the label/text to a number, but it might fail if the user has simbols (ex.: "5+8" or "3*")
            try {
                int equation = Integer.parseInt(text.toString());
                text.setLength(0);
                text.append(Evaluator.convertToBaseN(equation, 2, 10));
                label.setText(text.toString());
            } catch (NumberFormatException ex) {
                //throw and error message and log the error
                JOptionPane.showMessageDialog(null, "Please enter a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.log("Tried to convert " + text + " to base 10!" , (byte) WinNT.EVENTLOG_ERROR_TYPE);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //log the closing of the current window and closing it
                Logger.log("Calculator closed", (byte) WinNT.EVENTLOG_INFORMATION_TYPE);
                frame.dispose();
            }
        });

        //set the widow to be visible
        frame.setVisible(true);
    }

    /**
     * Updates the input field in the calculator interface based on the user input.
     * This method processes the key events, modifies the displayed label and
     * the internal data structure (StringBuilder text), ensures valid input is maintained,
     * and handles specific key actions such as evaluation, clearing, and deletion.
     *
     * @param keyChar the character of the key pressed by the user
     * @param keyCode the key code of the key pressed by the user
     * @param label the JLabel component that displays the current input or result
     * @param text the StringBuilder representing the current input expression
     */
    private void updateInput(char keyChar, int keyCode, JLabel label,StringBuilder text) {
        //check if the key pressed is allowed
        if(isCharAllowed(keyChar)){
            //check if the key is a number, also, we dont need to check for last input because we want to let the user be able to type multiple digits numbers (ex.: 234)
            if(keyChar >= 48 && keyChar <= 57) {
                //add the number to the input
                text.append(keyChar - '1' + 1);
                //update the lastInput to be 1
                lastInput = 1;
            }
            //now, if the char is allowed and is not a number, witch is the only way we gat with the execution to this point,
            // then the char must be a symbol, but if the lastInput is 0,
            // then it means that the last char was also a symbol, so we do not allow it in the StringBuilder text
            else if(keyChar == '/' && lastInput != 0) {
                text.append('/');
                lastInput = 0;
            }
            else if(keyChar == '*' && lastInput != 0) {
                text.append('*');
                lastInput = 0;
            }
            else if(keyChar == '-' && lastInput != 0) {
                text.append('-');
                lastInput= 0;
            }
            else if(lastInput != 0) {
                text.append('+');
                lastInput = 0;
            }

            //set the label to the updated text
            label.setText(text.toString());
        }
        //if the char is not an allowed char, then it is either enter,'c',or backspace, or an random char, so we check for each one
        else if(keyCode == KeyEvent.VK_ENTER){
            //evaluate the current expression that we have in text
            int a = Evaluator.evaluateExpression(text.toString());
            //reset text
            text.setLength(0);
            //set text to the result of the equation
            text.append(a);
            //update label to the updated text
            label.setText(String.valueOf(a));
            //reset lastInput
            lastInput = -1;
        }
        else if(keyCode == KeyEvent.VK_C) {
            //clear the equation/text and reset lastInput
            text.setLength(0);
            lastInput = -1;
        }
        else if(keyCode == KeyEvent.VK_BACK_SPACE && !text.isEmpty()){
            //delete the last char from the equation
            text.setLength(text.length()-1);
            //update the equation
            label.setText(text.toString());

            //make sure that the lastInput is set correctly so no wrong char can be inputted (ex.: 4+5 (press backspace) -> 4+ (press '+') -> 4++)
            if(!text.isEmpty() && text.charAt(text.length() - 1) >= 48 && text.charAt(text.length() - 1) <= 57)
                lastInput = 1;
            else if(!text.isEmpty())
                lastInput = 0;
        }

        //if the equation is empty, we reset the lastInput and set the label to 0
        if(text.isEmpty()) {
            label.setText("0");
            lastInput = -1;
        }
    }

    /**
     * Checks if the provided character is allowed based on a predefined set of allowed characters.
     *
     * @param character the character to be checked for allowance
     * @return true if the character is allowed, false otherwise
     */
    private boolean isCharAllowed(char character){
        //check if character is allowed by iterating over all the elements of Main.allowedCharacters and comparing them
        for(int i = 0; i< Main.allowedCharacters.length; i++)
            if(Main.allowedCharacters[i] == character)
                return true;

        return false;
    }
}
