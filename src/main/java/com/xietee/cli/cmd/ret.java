package com.xietee.cli.cmd;

import com.alibaba.fastjson2.JSONObject;
import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.util.numberUtils;

import java.util.List;

import static com.xietee.exception.BusinessExceptionEnum.*;
import static com.xietee.service.LoginService.getUserFromJSON;

public class ret implements Cmd {

    private long id = 0L;

    public ret() {}

    @Override
    public Result<?> execute(App app) {

        if (id == 0L) {
            return Result.warn(COMMAND_PARAM_ERROR);
        }

        JSONObject jsonObject = getUserFromJSON();
        String studentID = jsonObject.getString("id");

        app.getStudentService().returnBookByRecordIDAndStudentID(id, studentID);

        return Result.success("归还成功", null);
    }

    @Override
    public Result<Void> parse(List<String> args) {

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg.equals("-i")) {
                if (i + 1 >= args.size()) return Result.warn(COMMAND_PARAM_ERROR);
                String argsId = args.get(i + 1);
                if (!numberUtils.isInteger(argsId)) return Result.warn(INVALID_NUMBER);
                this.id = Long.parseLong(argsId);
                i++;
            }
        }

        return Result.success(NONE);
    }

    @Override
    public void reset() {
        id = 0L;
    }
}
