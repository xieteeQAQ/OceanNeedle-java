package com.xietee.cli;

import com.xietee.cli.cmd.*;
import com.xietee.common.Result;

import java.util.*;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class Command {

    private static final Map<String, Cmd> GeneralCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> StudentCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> AdminCommandsMap =
            new HashMap<>();

    public Command() {

        GeneralCommandsMap.put("help", new help());
        GeneralCommandsMap.put("quit", new quit());
        GeneralCommandsMap.put("clear", new clear());
        GeneralCommandsMap.put("login", new login());
        GeneralCommandsMap.put("ls", new ls());
        GeneralCommandsMap.put("search", new search());
        GeneralCommandsMap.put("history", new history());

        StudentCommandsMap.put("borrow", new borrow());
        StudentCommandsMap.put("ret", new ret());

        AdminCommandsMap.put("update", new update());
        AdminCommandsMap.put("add", new add());
        AdminCommandsMap.put("del", new del());
    }

    private Optional<Cmd> getCommand(String commandName) {

        switch (App.getUser()) {
            case NULL -> {
                return Optional.ofNullable(GeneralCommandsMap.get(commandName));
            }
            case STUDENT -> {
                return GeneralCommandsMap.containsKey(commandName) ?
                        Optional.ofNullable(GeneralCommandsMap.get(commandName)) :
                        Optional.ofNullable(StudentCommandsMap.get(commandName));
            }
            case ADMIN -> {
                return GeneralCommandsMap.containsKey(commandName) ?
                        Optional.ofNullable(GeneralCommandsMap.get(commandName)) :
                        Optional.ofNullable(AdminCommandsMap.get(commandName));
            }
            case DEVELOPER -> {
                // 待开发
                return Optional.empty();
            }
            default -> {
                return Optional.empty();
            }
        }
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

        Optional<Cmd> cmdOptional = getCommand(commandName);
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
