package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;

import java.util.List;

public interface Cmd {

    Result<?> execute(App app);

    Result<Void> parse(List<String> args);

    default void reset() {}
}
