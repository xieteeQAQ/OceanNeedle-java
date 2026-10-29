package com.xietee.cli.cmd;

import com.xietee.cli.UserType;
import com.xietee.common.Result;

import java.util.List;

public interface Cmd {

    Result<?> execute();

    Result<Void> parse(List<String> args);

    default void reset() {}

    default void updateUser(UserType user) {};
}
