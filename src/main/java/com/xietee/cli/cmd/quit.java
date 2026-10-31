package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;

import java.util.List;

import static com.xietee.exception.BusinessExceptionEnum.NONE;

public class quit implements Cmd {

    public quit() {}

    @Override
    public Result<Void> execute(App app) {

        app.setRunning(false);
        return Result.success(NONE);
    }

    @Override
    public Result<Void> parse(List<String> args) {
        return Result.success(NONE);
    }
}
