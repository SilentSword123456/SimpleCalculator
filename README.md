# SimpleCalculator

A Java-based calculator application with a GUI that provides basic arithmetic operations and number base conversion capabilities.

## Features

- Basic arithmetic operations (+, -, *, /)
- Number base conversion (between base 2 and base 10)
- Graphical user interface (GUI)
- Input validation
- Comprehensive logging system (file-based and Windows Event Viewer)

## Technical Details

### Requirements

- Windows operating system
- To enable Event Viewer logging, run this command in an **Administrator PowerShell**:

  New-EventLog -LogName Application -Source "SimpleCalculator"

  *(This step is optional but recommended for full logging support.)*

### Key Features

- **Input Validation**: Ensures only valid characters and operations are processed.
- **Base Conversion**: Convert numbers between base 2 (binary) and base 10 (decimal).
- **Expression Evaluation**: Handles simple arithmetic expressions with proper operator precedence.
- **Error Handling**: Comprehensive error detection and user-friendly feedback.
- **Logging**: Detailed activity logging for debugging and monitoring, both locally and in the Windows Event Viewer.

## Installation

- Go to the [Releases](https://github.com/SilentSword123456/SimpleCalculator/releases) page and download the `.msi` installer.
- Alternatively, you can build the project yourself from the source code.

## Usage

1. Launch the application.
2. Use the calculator interface to:
   - Type arithmetic expressions.
   - Convert numbers between bases.
   - View results.
3. Press **Enter** to evaluate expressions.
4. Use dedicated buttons for base conversion.
5. Press **C** to clear the input.
6. Press **Backspace** to delete the last character.

## Logging

The application maintains logs in two locations:

- **File logs**:  
  `C:\Users\[username]\AppData\Local\SimpleCalculator\logs.txt`
- **Windows Event Viewer**:  
  Under the application name **"SimpleCalculator"** (Application log)

## Version

Current version: **2.0**

## Notes

- The application requires **Windows OS** for full functionality due to Event Viewer integration.
- Maximum equation length: **100 characters**.
- Supported characters: digits (`0-9`) and basic arithmetic operators (`+`, `-`, `*`, `/`).
- **Floating-point numbers are not supported.**
- **Negative numbers are not supported.**
- Maximum value: fits within an `int` (up to **9 digits**).

## Source Code

You can view or contribute to the project here:  
🔗 [SimpleCalculator GitHub Repository](https://github.com/SilentSword123456/SimpleCalculator)
