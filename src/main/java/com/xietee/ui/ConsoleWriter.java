package com.xietee.ui;

import org.jline.terminal.Terminal;

import java.util.List;

public class ConsoleWriter {

    private final Terminal terminal;

    public ConsoleWriter(Terminal terminal) {
        this.terminal = terminal;
    }


    public void writeTitle(String title) {
        if (title.isEmpty()) {
            return;
        }
        terminal.writer().print(
                centeredWidth(
                        AnsiColors.BLACK__WHITE + "  " + title + "  " + AnsiColors.RESET)
                +"\n".repeat(2));
        terminal.writer().flush();
    }

    public void write(String content) {
        if (content.isEmpty()) {
            return;
        }
        terminal.writer().print(content + "\n".repeat(2));
        terminal.writer().flush();

    }

    public void writeList(List<String> stringList) {
        if (!stringList.isEmpty()) {
            stringList.forEach(s -> terminal.writer().println(centeredWidth(s)));
            terminal.writer().print("\n".repeat(2));
            terminal.writer().flush();
        }
    }

    public String centeredWidth(String line) {

        String centered;

        int terminalColumns = terminal.getColumns() == 0 ? 80 : terminal.getColumns();
        int visualWidth = org.jline.utils.AttributedString.fromAnsi(line).length();
        int padding_left = 0;

        if (terminalColumns > visualWidth) {
            padding_left = (terminalColumns - visualWidth) / 2;
        }

        centered = " ".repeat(padding_left) + line;
        return centered;
    }

    public String centeredHeight(String line) {

        String centered;

        int terminalColumns = terminal.getRows() == 0 ? 40 : terminal.getRows();
        int padding_upper = terminalColumns / 2;

        centered = "\n".repeat(padding_upper) + "\r" + line;
        return centered;
    }
}
