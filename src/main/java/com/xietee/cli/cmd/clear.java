package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;

import java.util.List;

import static com.xietee.exception.BusinessExceptionEnum.NONE;

public class clear implements Cmd {

    private final App app;

    public clear(App app) {
        this.app = app;
    }

    @Override
    public Result<Void> execute() {

        app.getUi().clearConsole();
        return Result.success(NONE);
    }

    @Override
    public Result<Void> parse(List<String> args) {
        return Result.success(NONE);
    }
}
