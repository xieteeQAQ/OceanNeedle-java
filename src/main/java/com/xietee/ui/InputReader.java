package com.xietee.ui;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

public class InputReader {

    private final LineReader lineReader;

    public InputReader(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public String readInput(String prompt) {
        try {
            return lineReader.readLine(prompt);
        } catch (UserInterruptException e) {
            throw new UserInterruptException("Operation cancelled by user");
        } catch (EndOfFileException e) {
            throw new EndOfFileException("End of input");
        }
    }

    public String readPassword(String prompt) {
        try {
            return lineReader.readLine(prompt, '*');
        } catch (UserInterruptException e) {
            throw new UserInterruptException("Operation cancelled by user");
        } catch (EndOfFileException e) {
            throw new EndOfFileException("End of input");
        }
    }
}
