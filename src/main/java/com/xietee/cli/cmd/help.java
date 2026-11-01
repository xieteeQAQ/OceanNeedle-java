package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.exception.BusinessException;
import com.xietee.ui.AnsiColors;
import com.xietee.ui.AppUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class help implements Cmd {

    private static final Set<String> commands = new HashSet<>(Set.of(
            "all",
            "help", "ls", "search", "del",
            "update", "clear", "add", "quit",
            "borrow", "ret", "login", "history"
    ));

    private String commandName = "all";

    private final Logger log = LoggerFactory.getLogger(help.class);

    public help() {}

    private String getDescription(String commandName) {

        Path path = Paths.get("src", "main", "resources", "description", commandName.trim() + ".txt");
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (NoSuchFileException e) {
            log.error("文件 {}.txt 不存在", commandName);
            throw new BusinessException(NO_SUCH_FILE.getCode(), NO_SUCH_FILE.getMessage() + ": " + commandName + ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }

    @Override
    public Result<Void> execute(App app) {

        final AppUI ui = app.getUi();


        String helpMessage;

        helpMessage = getDescription(commandName);
        ui.consoleWrite(AnsiColors.BLACK__WHITE + "  " + commandName + "  " + AnsiColors.RESET);
        ui.consoleWrite(helpMessage);

        return Result.success(NONE);
    }

    @Override
    public Result<Void> parse(List<String> args) {
        if (args.isEmpty()) {
            this.commandName = "all";
            return Result.success(NONE);
        }

        String commandName = args.getFirst();
        if (commands.contains(commandName)) {
            this.commandName = commandName;
            return Result.success(NONE);
        } else {
            return Result.warn(COMMAND_UNEXISTED);
        }
    }

    @Override
    public void reset() {
        this.commandName = "all";
    }
}
