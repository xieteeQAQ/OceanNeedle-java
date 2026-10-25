package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class ls implements Cmd {

    private final App app;

    private String object = "";

    private final Set<String> objects = new HashSet<>(Set.of(
            "student", "admin", "developer", "record", "book"
    ));

    public ls(App app) {
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
            return Result.success(NONE);
        } else {
            return Result.warn(COMMAND_PARAM_ERROR);
        }
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (object.isEmpty()) {
            ui.barPrintOption("要列出的对象是? \n[0]取消 \n[1]学生 \n[2]书籍 \n[3]记录 \n[4]管理员 \n[5]开发者");
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("1")) {
            List<Student> allStudents = app.getAdminService().findAllStudent();
            List<String> visualized = allStudents.stream().map(Student::toString).toList();
            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(visualized);

            return Result.success(NONE);
        } else if (object.equals("book") || object.equals("2")) {
            List<Book> allStudents = app.getAdminService().findAllBook();
            List<String> visualized = allStudents.stream().map(Book::toString).toList();
            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(visualized);

            return Result.success(NONE);
        } else {
            return Result.warn(FEATURE_NOT_DEVELOPED);
        }
    }

    @Override
    public void reset() {
        this.object = "";
    }
}
