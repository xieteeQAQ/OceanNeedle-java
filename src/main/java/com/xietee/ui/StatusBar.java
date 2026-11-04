package com.xietee.ui;

import org.jline.reader.LineReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatusBar {

    private final LineReader lineReader;
    private final DateTimeFormatter timeFormatter;

    public StatusBar(LineReader lineReader) {
        this.lineReader = lineReader;
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }

    private void sent(String message) {
        lineReader.printAbove(message);
    }

    public void printInfo(String content) {
        if (content.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(timeFormatter);
        String message =
                String.format(
                        "%s[%s] %s%s:%s %s %s",
                        AnsiColors.BRIGHT_BLACK,
                        timestamp,
                        AnsiColors.BRIGHT_WHITE,
                        "info",
                        AnsiColors.RESET,
                        content,
                        AnsiColors.RESET);
        sent(message);
    }

    public void printSuccess(String content) {
        if (content.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(timeFormatter);
        String message =
                String.format(
                        "%s %s %s %s | %s %s",
                        AnsiColors.BLACK__BRIGHT_GREEN,
                        "S",
                        AnsiColors.BLACK__WHITE,
                        timestamp,
                        content,
                        AnsiColors.RESET);
        sent(message);
    }

    public void printWarning(String content) {
        if (content.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(timeFormatter);
        String message =
                String.format(
                        "%s %s %s %s | %s %s",
                        AnsiColors.BLACK__BRIGHT_YELLOW,
                        "W",
                        AnsiColors.BLACK__WHITE,
                        timestamp,
                        content,
                        AnsiColors.RESET);
        sent(message);
    }

    public void printError(String content) {
        if (content.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(timeFormatter);
        String message =
                String.format(
                        "%s %s %s %s | %s %s",
                        AnsiColors.BLACK__BRIGHT_RED,
                        "E",
                        AnsiColors.BLACK__WHITE,
                        timestamp,
                        content,
                        AnsiColors.RESET);
        sent(message);
    }

    public void printOption(String content) {
        if (content.isEmpty()) return;

        String timestamp = LocalDateTime.now().format(timeFormatter);
        String message =
                String.format(
                        "%s[%s] %s%s:%s%s",
                        AnsiColors.BRIGHT_BLACK,
                        timestamp,
                        AnsiColors.BRIGHT_BLUE,
                        "option",
                        AnsiColors.RESET,
                        content);
        sent(message);
    }
}
