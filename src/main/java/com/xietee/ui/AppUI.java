package com.xietee.ui;

import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppUI implements AutoCloseable {

    @Getter private final Terminal terminal;

    @Getter private final LineReader lineReader;

    @Getter private final InputReader inputReader;

    @Getter private final StatusBar statusBar;

    @Getter private final ConsoleWriter consolePrinter;

    public AppUI() throws IOException {

        this.terminal =
                TerminalBuilder.builder().name("OceanNeedle--java-style").system(true).build();
        this.lineReader =
                LineReaderBuilder.builder()
                        .completer(new StringsCompleter("help", "quit", "ls", "clear",
                                "search", "update", "add", "del",
                                "borrow", "ret", "history", "login"))
                        .terminal(terminal)
                        .build();
        this.inputReader = new InputReader(lineReader);
        this.statusBar = new StatusBar(lineReader);
        this.consolePrinter = new ConsoleWriter(terminal);

        System.setProperty("org.jline.terminal.dumb", "true");
    }

    private static final String BANNER =
"""
                                                  ░██           ░██                                                                  \s
    ░███████      ░███████   ░█████████   ░██████   ░██      ░██  ░██      ░██ ░█████████ ░█████████ ░███████    ░███      ░█████████\s
  ░███     ░███ ░███     ░██ ░██         ░███   ░█  ░███     ░██  ░███     ░██ ░██        ░██        ░██    ░██  ░██       ░██       \s
  ░██       ░██ ░██          ░██░████   ░██     ░██ ░██ ░██  ░██  ░██ ░██  ░██ ░██░████   ░██░████   ░██     ░██ ░██       ░██░████  \s
  ░██       ░██ ░██          ░██░█      ░██████████ ░██  ░██ ░██  ░██  ░██ ░██ ░██░█      ░██░█      ░██     ░██ ░██       ░██░█     \s
  ░███     ░███ ░███      ░█ ░██        ░██     ░██ ░██    ░█░██  ░██    ░█░██ ░██        ░██        ░██    ░██  ░███      ░██       \s
    ░████████     ░███████   ░█████████ ░██     ░██ ░██     ░███  ░██     ░███ ░█████████ ░█████████ ░███████    ░████████ ░█████████\s
                                                              ░█            ░█                                                       \s
""";
    private static final int BANNER_WIDTH = 132;

    private static final String welcomeTitle = "图书馆管理系统 (author: xieteeQAQ)";

    private static final List<String> welcomeOptions = new ArrayList<>(List.of(
            "列出所有        --ls",
            "查询           --search",
            "更新           --update",
            "新增           --add",
            "删除           --del",
            "命令指南        --help"));

    public void showBanner() {
        terminal.writer().println("\n".repeat(2) + AnsiColors.CYAN + BANNER + AnsiColors.RESET);
    }

    public void showWelcome() {
        consoleWriteTitle(welcomeTitle);
        consoleWriteList(welcomeOptions);
    }

    public void initTerminal() {
        // 清屏 + 光标归位 + 清滚动缓冲；JLine 启动时的能力查询（[?2026$p 之类）也一并盖掉
        terminal.writer().print("\u001b[2J\u001b[3J\u001b[H");
        terminal.flush();
    }

    public void clearConsole() {
        try {
            terminal.writer().print("\u001b[H\u001b[2J");
            terminal.flush();
        } catch (Exception e) {
            terminal.writer().print("\n".repeat(50));
            terminal.writer().flush();
        }
    }

    public String readInput(String prompt) {
        return inputReader.readInput(prompt);
    }

    public String readPassword(String prompt) {
        return inputReader.readPassword(prompt);
    }

    public void consoleWriteTitle(String title) { consolePrinter.writeTitle(title); }

    public void consoleWrite(String content) { consolePrinter.write(content); }

    public void consoleWriteList(List<String> stringList) { consolePrinter.writeList(stringList); }

    public void barPrintInfo(String content) {
        statusBar.printInfo(content);
    }

    public void barPrintSuccess(String content) {
        statusBar.printSuccess(content);
    }

    public void barPrintWarning(String content) {
        statusBar.printWarning(content);
    }

    public void barPrintError(String content) {
        statusBar.printError(content);
    }

    public void barPrintOption(String content) {
        statusBar.printOption(content);
    }

    @Override
    public void close() {
        try {
            if (terminal != null) {
                terminal.close();
            }
        } catch (Exception e) {
            // pass: 忽略关闭错误
        }
    }
}
