package com.xietee.cli;

import com.xietee.cli.cmd.*;
import com.xietee.common.Result;

import java.util.*;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class Command {

    private static final Map<String, Cmd> commandsMap =
            new HashMap<>();

    public Command(App app) {

        commandsMap.put("help", new help(app));
        commandsMap.put("quit", new quit(app));
        commandsMap.put("exit", new quit(app));
        commandsMap.put("clear", new clear(app));
        commandsMap.put("ls", new ls(app));
        commandsMap.put("add", new add(app));
        commandsMap.put("search", new search(app));
        commandsMap.put("update", new update(app));
        commandsMap.put("del", new del(app));
        commandsMap.put("login", new login(app));
    }

    public Result<Cmd> parseAndGetCommand(String line) {

        List<String> parsedLine;

        if (line.isEmpty()) {
            return Result.success(NONE);
        }

        parsedLine = Arrays.stream(line.split(" ")).toList();

        String commandName = "";
        List<String> args = new ArrayList<>();

        for (int i = 0; i < parsedLine.size(); i++) {
            if (i == 0) {
                commandName = parsedLine.get(i);
            } else {
                args.add(parsedLine.get(i));
            }
        }

        Optional<Cmd> cmdOptional = Optional.ofNullable(commandsMap.get(commandName));
        if (cmdOptional.isEmpty()) {
            return Result.warn(COMMAND_ERROR);
        }

        var parseResult = cmdOptional.get().parse(args);
        if (parseResult.code() != 200) {
            return Result.warn(parseResult.code(), parseResult.message());
        }

        return Result.success(cmdOptional.get());
    }
}
