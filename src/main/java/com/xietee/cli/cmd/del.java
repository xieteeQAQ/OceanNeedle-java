package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;
import static com.xietee.exception.BusinessExceptionEnum.FEATURE_NOT_DEVELOPED;
import static com.xietee.exception.BusinessExceptionEnum.PARAM_ERROR;

public class del implements Cmd {

    private final App app;

    private String object = "";

    private String key = "";

    private final Set<String> objects = new HashSet<>(Set.of(
            "student", "admin", "developer", "record", "book"
    ));

    public del(App app) {
        this.app = app;
    }

    @Override
    public Result<Void> parse(List<String> args) {

        if (args.isEmpty()) {
            return Result.success(NONE);
        }
        String argsObject = args.getFirst();

        if (objects.contains(argsObject)) {
            this.object = argsObject;
        } else {
            return Result.warn(COMMAND_PARAM_ERROR);
        }

        if (args.size() > 1) {
            key = args.get(1);
        }

        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (object.isEmpty()) {
            ui.barPrintOption("要删除的对象是? \n[0]取消 \n[1]学生 \n[2]书籍 \n[3]记录 \n[4]管理员 \n[5]开发者");
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("1")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要删除的学生的学号");
                key = ui.readInput("StudentID > ").trim();

                if (key.isEmpty()) {
                    return Result.warn(COMMAND_NEED_PARAM);
                }

            } else if (key.length() != 12) {
                return Result.warn(PARAM_ERROR.getCode(), "学生学号必须为 12 位数");
            }

            // 通过获取 id 来删除 同时可以检查该学生是否存在
            Student deleteStudent = app.getAdminService().findStudentByStudentID(key);
            app.getAdminService().deleteStudentById(deleteStudent.getId());

            return Result.success("学生数据已删除", null);

        } else if (object.equals("book") || object.equals("2")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要删除的书籍的序号");
                key = ui.readInput("序号 > ").trim();
            }

            if (key.isEmpty()) {
                return Result.warn(COMMAND_NEED_PARAM);
            } else if (!key.matches("^[1-9]\\d*$")) {
                return Result.warn(INVALID_NUMBER);
            }

            long id = Long.parseLong(key);
            // 检查是否存在
            app.getAdminService().findBookById(id);
            // 通过获取 id 来删除
            app.getAdminService().deleteBookById(id);

            return Result.success("书籍数据已删除", null);

        } else {
            return Result.success(FEATURE_NOT_DEVELOPED);
        }
    }
}
