package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import lombok.Setter;

import java.util.List;

public class borrow implements Cmd {

    @Setter
    private App app;

    public borrow(App app) {
        this.app = app;
    }

    @Override
    public Result<?> execute() {
        return null;
    }

    @Override
    public void updateUser(UserType user) {
        Cmd.super.updateUser(user);
    }

    @Override
    public void reset() {
        Cmd.super.reset();
    }

    @Override
    public Result<Void> parse(List<String> args) {
        return null;
    }
}
