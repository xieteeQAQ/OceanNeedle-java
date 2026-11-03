package com.xietee.ui;

import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ConsoleWriter 的排版与输出测试。
 * 用 JLine 的 DumbTerminal + 固定 80x24 尺寸，避免依赖真实终端。
 */
class ConsoleWriterTest {

    private ByteArrayOutputStream output;
    private Terminal terminal;
    private ConsoleWriter writer;

    @BeforeEach
    void setUp() throws IOException {
        output = new ByteArrayOutputStream();
        terminal = new DumbTerminal(
                "test", Terminal.TYPE_DUMB, InputStream.nullInputStream(), output, StandardCharsets.UTF_8);
        terminal.setSize(Size.of(80, 24));
        writer = new ConsoleWriter(terminal);
    }

    @AfterEach
    void tearDown() throws IOException {
        terminal.close();
    }

    private String captured() {
        terminal.flush();
        return output.toString(StandardCharsets.UTF_8);
    }

    // ---------------------------------------------------------------- 排版

    @Test
    @DisplayName("centeredWidth：短文本左侧补齐一半的空白")
    void centeredWidthPadsShortLine() {
        assertEquals(" ".repeat(38) + "abc", writer.centeredWidth("abc"));
    }

    @Test
    @DisplayName("centeredWidth：ANSI 颜色码不参与宽度计算")
    void centeredWidthIgnoresAnsiCodes() {
        String colored = AnsiColors.BRIGHT_RED + "abc" + AnsiColors.RESET;

        String line = writer.centeredWidth(colored);

        assertEquals(" ".repeat(38) + colored, line);
        assertEquals(41, AttributedString.fromAnsi(line).length());
    }

    @Test
    @DisplayName("centeredWidth：文本比终端宽时不补空格")
    void centeredWidthLeavesLongLineUntouched() {
        String longLine = "x".repeat(100);

        assertEquals(longLine, writer.centeredWidth(longLine));
    }

    @Test
    @DisplayName("centeredHeight：垂直居中，上方补一半行数的空行")
    void centeredHeightPadsHalfRows() {
        int rows = terminal.getRows() == 0 ? 40 : terminal.getRows();

        assertEquals("\n".repeat(rows / 2) + "\r" + "abc", writer.centeredHeight("abc"));
    }

    // ---------------------------------------------------------------- 输出

    @Test
    @DisplayName("writeTitle：标题带底色并居中对齐")
    void writeTitlePrintsCenteredTitle() {
        writer.writeTitle("TITLE");

        String printed = captured();
        assertTrue(printed.contains("TITLE"));
        assertTrue(printed.startsWith(" "), "标题应居中，因此左侧有空格");
        assertTrue(printed.endsWith("\n\n"));
    }

    @Test
    @DisplayName("write：正文原样输出，并追加两个空行")
    void writePrintsContent() {
        writer.write("hello");

        assertEquals("hello\n\n", captured());
    }

    @Test
    @DisplayName("writeList：逐行居中输出")
    void writeListPrintsEachCenteredLine() {
        writer.writeList(List.of("line-1", "line-2"));

        String printed = captured();
        assertTrue(printed.contains("line-1"));
        assertTrue(printed.contains("line-2"));
        assertEquals(2, printed.lines().filter(line -> !line.isBlank()).count());
    }

    @Test
    @DisplayName("空标题 / 空正文 / 空列表：不输出任何内容")
    void emptyContentProducesNoOutput() {
        writer.writeTitle("");
        writer.write("");
        writer.writeList(List.of());

        assertEquals(0, captured().length());
    }
}
