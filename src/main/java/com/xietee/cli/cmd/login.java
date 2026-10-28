package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;
import com.xietee.ui.AppUI;
import com.xietee.util.TimeUtils;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class login implements Cmd {

    private static final Set<String> users = new HashSet<>(Set.of(
            "student", "admin"
    ));

    private String user = "";

    private final App app;

    public login(App app) {
        this.app = app;
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (user.isEmpty()) {
            ui.barPrintOption("你是 \n[0]取消 \n[1]学生 \n[2]管理员");
            user = ui.readInput("Option > ").trim();
            if (user.equals("0")) {
                return Result.success("", null);
            } else if (user.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (user.equals("student") || user.equals("1")) {

            String studentID = ui.readInput("学号 > ").trim();
            if (studentID.isEmpty()) {
                return Result.warn(PARAM_ERROR.getCode(), "学号不能为空");
            } else if (studentID.length() != 12) {
                return Result.warn(PARAM_ERROR.getCode(), "学号应为 12 位数");
            }

            Student student;

            try {
                student = app.getAdminService().findStudentByStudentID(studentID);
            } catch (BusinessException e) {
                return Result.warn(UNEXISTED_STUDENT_ID);
            }

            String password = ui.readPassword("密码 > ").trim();
            if (password.isEmpty()) {
                return Result.warn(PARAM_ERROR.getCode(), "密码不能为空");
            } else if (password.length() < 8 || password.length() > 32) {
                return Result.warn(PARAM_ERROR.getCode(), "密码长度至少为 8 ，最长不超过 32");
            }

            if (student.getPassword().equals(password)) {
                String message = String.format("登录成功 %s好, %s", TimeUtils.nowTime(), student.getName());
                return Result.success(message, null);
            } else {
                return Result.warn(INCORRECT_PASSWORD);
            }
        } else if (user.equals("admin") || user.equals("2")) {
            return Result.warn(FEATURE_NOT_DEVELOPED);
        } else {
            return Result.warn(PARAM_ERROR);
        }
    }

    @Override
    public void reset() {
        user = "";
    }

    @Override
    public Result<Void> parse(List<String> args) {
        if (args.isEmpty()) {
            this.user = "";
            return Result.success(NONE);
        }

        String  argsUser = args.getFirst();

        if (users.contains(argsUser)) {
            this.user = argsUser;
            return Result.success(NONE);
        } else {
            return Result.warn(COMMAND_PARAM_ERROR);
        }
    }
}
