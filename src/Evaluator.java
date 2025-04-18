import com.sun.jna.platform.win32.WinNT;

/**
 * The Evaluator class provides utility methods for mathematical expression evaluation
 * and base conversion. It includes mechanisms to parse and calculate arithmetic
 * operations and convert numbers between various bases.
 */
public class Evaluator {

    /**
     * An array utilized to store numbers extracted from a mathematical equation during parsing.
     *
     * This static array represents a First-In-Last-Out (FILO) stack structure and is used internally
     * by the `Evaluator` class to evaluate mathematical expressions and perform base conversions.
     * The size of the array is determined by the maximum allowed equation length
     * (`Main.maxEcuationLenght`) plus one, to accommodate potential edge cases.
     *
     * Usage within the `Evaluator` ensures proper order and retrieval of numbers corresponding
     * to operations extracted during the processing of a given mathematical equation.
     */
    //creating 2 FILO vectors
    private static int numbers[] = new int[Main.maxEcuationLenght+1];
    /**
     * Array to store mathematical operations extracted from a given equation.
     * Each element represents a valid arithmetic operator (+, -, *, /) in the order
     * they appear in the parsed equation. The length of the array is determined
     * by the maximum allowed equation length (`Main.maxEcuationLenght`) plus one.
     *
     * This field is used in conjunction with other components (such as `numbers` in
     * the Evaluator class) for parsing and evaluating mathematical expressions.
     */
    private static char operations[] = new char[Main.maxEcuationLenght+1];

    /**
     * Represents a static integer used as a reference or count for the left-side numbers
     * in mathematical computation or parsing processes within the Evaluator class.
     * Typically utilized in conjunction with `stO` and `numbers` to manage or track the
     * state of numbers in operations.
     */
    //creating their bounds with stN being leftNumbers and stO being leftOperations
    private static int stN = 1;
    /**
     * Represents the static offset used during internal computations or processing
     * within the Evaluator class. The variable might be involved in calculation
     * adjustments or as a fixed constant during operations. Its exact usage depends
     * on the context provided by the methods in the Evaluator class.
     */
    private static int stO = 1;

    /**
     * Evaluates a mathematical expression represented as a string, performs the calculations
     * following the standard operator precedence (multiplication and division before addition
     * and subtraction), and returns the computed result.
     *
     * @param expression the mathematical expression to be evaluated. It must consist of valid
     *                   numbers and arithmetic operators (+, -, *, /).
     * @return the result of evaluating the expression as an integer.
     */
    static int evaluateExpression(String expression) {

        //reset the bonds of the FILO vectors
        stN=stO=1;

        //get the expression and extract the numbers and operations from it in their corresponding FILO
        formater(expression);

        //iterating over all the elements from operations to first calculate the higher priorities operations ('*' and '/')
        for (int i = 1; i < stO; i++)
            if (operations[i] == '*') {
                //we have found the symbol for multiplication '*', now we multiply the corresponding numbers in the FILO numbers vector which are i and i + 1
                numbers[i] = numbers[i] * numbers[i + 1];
                //we override the next number (i + 1) with the rest of the vector to delete it and update the numbers FILO vector bound
                for (int j = i + 1; j < stN; j++)
                    numbers[j] = numbers[j + 1];
                stN--;
                //we override current symbol (i) with the rest of the vector to delete it and update the operations FILO vector bound
                for (int j = i; j < stO; j++)
                    operations[j] = operations[j + 1];
                stO--;
                //if the current symbol is still a multiplication or divide one ('*'/'/') then we decrement i so that we don't jump over it
                if (operations[i] == '*' || operations[i] == '/')
                    i--;
            }
            else if (operations[i] == '/') {
                //we have found the symbol for dividing '/', now we divide the corresponding numbers in the FILO numbers vector which are i and i + 1
                numbers[i] = numbers[i] / numbers[i + 1];
                //we override the next number (i + 1) with the rest of the vector to delete it and update the numbers FILO vector bound
                for (int j = i + 1; j < stN; j++)
                    numbers[j] = numbers[j + 1];
                stN--;
                //we override current symbol (i) with the rest of the vector to delete it and update the operations FILO vector bound
                for (int j = i; j < stO; j++)
                    operations[j] = operations[j + 1];
                stO--;
                //if the current symbol is still a multiplication or divide one ('*'/'/') then we decrement i so that we don't jump over it
                if (operations[i] == '*' || operations[i] == '/')
                    i--;
            }

        //now we iterate again over operations so that we evaluate the "+" and '-' symbols
        for (int i = 1; i <= stO || stN > 1; i++) {
            //if the current element of operation[i] is '-' then we subtract, else, we add the numbers
            if (operations[1] == '-')
                numbers[1] = numbers[1] - numbers[2];
            else
                numbers[1] = numbers[1] + numbers[2];
            //we override the next number with the rest of the vector to delete it and update the numbers FILO vector bound
            for (int j = 2; j < stN; j++)
                numbers[j] = numbers[j + 1];
            stN--;
            //we override current symbol with the rest of the vector to delete it and update the operations FILO vector bound
            for (int j = 2; j < stO; j++)
                operations[j] = operations[j + 1];
            stO--;
        }

        //we log the result
        Logger.log("Evaluated expression " + expression + " to be " + numbers[1], (byte) WinNT.EVENTLOG_INFORMATION_TYPE);

        return numbers[1];
    }

    /**
     * Converts a number represented in a given base to another target base.
     * The method first translates the input number from its original base to a base-10 integer.
     * It then converts the base-10 integer to the specified target base.
     *
     * @param number      The number to be converted, represented in the original base.
     * @param base        The base of the input number (e.g., 2 for binary, 10 for decimal).
     * @param targetBase  The base to which the number should be converted.
     * @return A string representing the number in the target base.
     *         If the input number is invalid or bases are unsupported, behavior is undefined.
     */
    static public String convertToBaseN(int number, int base,int targetBase){
        //Reconstruct the number in <base>

        //we get the current number, we get its first digit, and then we multiply it by the base at the power of what position it is in the number (basic math, nothing fancy)
        int numberInBase = 0,i = 0,copyNumber = number;
        while(number != 0){
            numberInBase += number % 10 * (int) Math.pow(base, i++);
            number /= 10;
        }

        //Transform the numberInBase into that targetBase

        StringBuilder rests = new StringBuilder();

        //we now transform the number in base 10 to the target base, and we do it like we learned in school, we divide the number by the target base
        // (in school  we just learned to divide by to and transform into base 2, but it's the same principle) and we get its remainer and then construct a number with it
        // then we do that again and again until the initial number is 0
        // ex.: 25 / 2 = 12 / 2 = 6 / 2 = 3 / 2 = 1 / 2 = 0
        //         r = 1    r = 0   r = 0   r = 1   r = 1
        //new we get the remainders and construct the actual number in the targetBase by flipping the remainders
        // ex.: original remainders : 1,0,0,1,1 so the original number in targetBase will be 1,1,0,0,1
        while(numberInBase != 0){
            rests.append(numberInBase % targetBase);
            numberInBase /= targetBase;
        }

        StringBuilder numberInBaseN = new StringBuilder();

        //flip the remainders to get the actual number
        while(!rests.isEmpty()){
            numberInBaseN.append(rests.charAt(rests.length() - 1));
            rests.setLength(rests.length() - 1);
        }

        //log the results
        Logger.log("Converted number " + copyNumber + " from base " + base + " into target base " + targetBase + ": " + numberInBaseN, (byte) WinNT.EVENTLOG_INFORMATION_TYPE);

        //return the result
        return numberInBaseN.toString();
    }

    /**
     * Parses a mathematical equation represented as a string, extracting numbers and operations,
     * and stores them in separate predefined arrays (`numbers` and `operations`).
     * The method ensures proper handling of consecutive numbers and valid operations, maintaining the
     * order of their appearance.
     *
     * @param equation the mathematical equation as a string to be formatted. It should consist of
     *                 numbers (0-9) and valid arithmetic operators (+, -, *, /).
     */
    static private void formater(String equation) {

        //initializing some vars
        byte lastInput = -1;
        char currentChar;
        int i;

        //iterating over all the elements from the equation
        for(int k=0;k<equation.length();k++){
            currentChar = equation.charAt(k);

            //checking if the current char in the equation is a number
            if (currentChar >= 48 && currentChar <= 57 && lastInput != 1) {
                //transforming the current char into an int corresponding to its actual value in numbers
                int number = currentChar - '1' + 1; //ex.: if the current chat is '5' witch is equal to 35, then if we subtract '1' witch is 31 we get 4, then we add 1 we get 5:) Works for 0 too

                //so, until we find a symbol that is not a number, we keep adding the current char to the number until we construct the hold number
                //we start from k+1, which is the next char, we check so that i, which is k+1 is not over the bound of the equation,
                //we also check if the char at i from the equation is a number, and if yes, we add it to the number we are constructing
                for(i=k+1;i < equation.length() && equation.charAt(i) >= 48 && equation.charAt(i) <= 57;i++)
                    number=number*10+equation.charAt(i) - '1' + 1;

                //we add the number to the FILO vector
                numbers[stN++] = number;
                //we set the lastInput to 1 since we just processed a number
                lastInput = 1;
                //we set k to the number we stopped at
                k=i-1;
            } else if (currentChar == '/' && lastInput != 0) { //we know that the current char is one allowed but is not an number,
                                                               // then we check for the possible symbols and add them to the operations FILO vector,
                                                               // and then we set the lastInput to 0 so that we know that the last input was a symbol
                operations[stO++] = '/';
                lastInput = 0;
            } else if (currentChar == '*' && lastInput != 0) {
                operations[stO++] = '*';
                lastInput = 0;
            } else if (currentChar == '-' && lastInput != 0) {
                operations[stO++] = '-';
                lastInput = 0;
            } else if (lastInput != 0) {
                operations[stO++] = '+';
                lastInput = 0;
            }
        }

        //log the results
        Logger.log("Formated " + equation + " to numbers: " + numbers.toString() + " and operations to: " + operations.toString(), (byte) WinNT.EVENTLOG_INFORMATION_TYPE);

    }
}
