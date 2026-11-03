package com.xietee.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 命令说明文档的一致性测试：
 * 每个命令都应该有非空的 description/*.txt，并且 all.txt 里能查到该命令。
 */
class CommandDocumentationTest {

    private static final Path DESCRIPTION_DIR =
            Path.of("src", "main", "resources", "description");

    private static final List<String> COMMANDS = List.of(
            "help", "login", "ls", "search", "add", "update",
            "del", "borrow", "ret", "history", "clear", "quit");

    @Test
    @DisplayName("每个命令都有对应的说明文件，且标题与命令名一致")
    void everyCommandHasDescriptionFile() throws IOException {
        for (String command : COMMANDS) {
            Path file = DESCRIPTION_DIR.resolve(command + ".txt");
            assertTrue(Files.exists(file), "缺少说明文件: " + file);
            String content = Files.readString(file, StandardCharsets.UTF_8);
            assertFalse(content.isBlank(), "说明文件为空: " + file);
            assertTrue(content.contains("> " + command),
                    "说明文件标题与命令名不一致: " + file);
        }
    }

    @Test
    @DisplayName("all.txt 索引包含所有命令")
    void allIndexListsEveryCommand() throws IOException {
        String all = Files.readString(DESCRIPTION_DIR.resolve("all.txt"), StandardCharsets.UTF_8);

        for (String command : COMMANDS) {
            assertTrue(all.contains(command), "all.txt 未列出命令: " + command);
        }
    }
}
