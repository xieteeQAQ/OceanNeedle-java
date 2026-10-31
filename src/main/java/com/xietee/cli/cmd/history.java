package com.xietee.cli.cmd;

import com.alibaba.fastjson2.JSONObject;
import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import com.xietee.entity.Record;
import com.xietee.exception.LoginException;
import com.xietee.service.LoginService;
import com.xietee.ui.AppUI;

import java.util.ArrayList;
import java.util.List;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class history implements Cmd {

    private boolean onlyUnReturned = false;

    public history() {};

    @Override
    public Result<Void> execute(App app) {

        AppUI ui = app.getUi();

        List<Record> records;

        if (App.getUser() == UserType.STUDENT) {

            JSONObject jsonObject = LoginService.getUserFromJSON();
            String studentID = jsonObject.getString("id");

            if (onlyUnReturned) {
                records = app.getStudentService().findUnReturnedRecordByStudentID(studentID);
            } else {
                records = app.getStudentService().findAllRecordByStudentID(studentID);
            }

        } else if (App.getUser() == UserType.ADMIN) {

            if (onlyUnReturned) {
                records = app.getAdminService().findUnReturnedRecord();
            } else {
                records = app.getAdminService().findAllRecord();
            }

        } else if (App.getUser() == UserType.DEVELOPER) {
            return Result.warn(FEATURE_NOT_DEVELOPED);
        } else {
            throw new LoginException(LOGIN_SESSION_EXPIRED);
        }

        // 数据可视化
        List<String> lines = new ArrayList<>(records.stream().map(Record::toString).toList());

         ui.consoleWriteTitle("历史记录");
        ui.consoleWriteList(lines);
        return Result.success("已列出历史记录", null);
    }

    @Override
    public Result<Void> parse(List<String> args) {

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (arg.equals("-u")) {
                this.onlyUnReturned = true;
            }
        }

        return Result.success(NONE);
    }

    @Override
    public void reset() {
        this.onlyUnReturned = false;
    }
}
