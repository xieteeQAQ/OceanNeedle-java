package com.xietee.cli;

import com.xietee.cli.cmd.*;
import com.xietee.common.Result;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class Command {

    private static final Map<String, Cmd> GeneralCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> StudentCommandsMap =
            new HashMap<>();
    private static final Map<String, Cmd> AdminCommandsMap =
            new HashMap<>();
    private @Getter UserType user;

    public void setUser(UserType user) {
        this.user = user;
        GeneralCommandsMap.forEach((key, cmd) -> cmd.updateUser(user));
        StudentCommandsMap.forEach((key, cmd) -> cmd.updateUser(user));
        AdminCommandsMap.forEach((key, cmd) -> cmd.updateUser(user));
    }

    public Command(App app) {

        this.user = app == null ? UserType.NULL : App.getUser();

        GeneralCommandsMap.put("help", new help(app));
        GeneralCommandsMap.put("quit", new quit(app));
        GeneralCommandsMap.put("clear", new clear(app));
        GeneralCommandsMap.put("login", new login(app));
        GeneralCommandsMap.put("ls", new ls(app));
        GeneralCommandsMap.put("search", new search(app));

        StudentCommandsMap.put("borrow", new borrow(app));

        AdminCommandsMap.put("update", new update(app));
        AdminCommandsMap.put("add", new add(app));
        AdminCommandsMap.put("del", new del(app));
    }

    private Optional<Cmd> getCommand(String commandName) {

        switch (user) {
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
