package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class ls implements Cmd {

    private String object = "";

    private boolean allBook = false;

    private final Set<String> objectsForStudent = new HashSet<>(Set.of("book"));
    private final Set<String> objectsForAdmin = new HashSet<>(Set.of("student", "book"));
    private final Set<String> objectsForDeveloper = new HashSet<>(Set.of("student", "book", "admin", "developer"));

    private static final String optionForStudent = "要列出的对象是? \n[0]取消 \n[1]书籍";
    private static final String optionForAdmin = "要列出的对象是? \n[0]取消 \n[1]书籍 \n[2]学生";
    private static final String optionForDeveloper = "要列出的对象是? \n[0]取消 \n[1]书籍 \n[2]学生 \n[3]管理员 \n[4]开发者";

    public ls() {
    }

    private String getOption() {

        if (App.isDEBUG()) {
            return optionForDeveloper;
        } else if (Objects.requireNonNull(App.getUser()) == UserType.ADMIN) {
            return optionForAdmin;
        }
        return optionForStudent;
    }

    private boolean objectsContains(String object) {

        if (App.isDEBUG()) {
            return objectsForDeveloper.contains(object);
        } else if (Objects.requireNonNull(App.getUser()) == UserType.ADMIN) {
            return objectsForAdmin.contains(object);
        }
        return objectsForStudent.contains(object);
    }

    @Override
    public Result<Void> parse(List<String> args) {

        if (args.isEmpty()) {
            return Result.success(NONE);
        }

        for (int i = 0; i < args.size(); i++) {

            String arg = args.get(i);
            switch (arg) {
                case "-o", "--object" -> {
                    if (i + 1 >= args.size()) return Result.warn(COMMAND_PARAM_ERROR);
                    String argsObject = args.get(i + 1);

                    if (!objectsContains(argsObject)) return Result.warn(COMMAND_PARAM_ERROR);
                    this.object = argsObject;
                    i++;
                }
                case "-a", "--all" -> this.allBook = true;
            }
        }
        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute(App app) {

        final AppUI ui = app.getUi();

        if (object.isEmpty()) {

            if (App.getUser() == UserType.STUDENT) {
                object = "book";
            } else {
                ui.barPrintOption(getOption());
                object = ui.readInput("Option > ").trim();
                if (object.equals("0")) {
                    return Result.success("", null);
                } else if (object.isEmpty()) {
                    return Result.warn(INVALID_NUMBER);
                }
            }
        }

        if (object.equals("book") || object.equals("1")) {

            List<Book> allBooks;

            if (allBook) {
                allBooks = app.getAdminService().findAllBook();
            } else {
                allBooks = app.getAdminService().findAllBookWithCount();
            }

            List<String> visualized = allBooks.stream().map(Book::toString).toList();

            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(visualized);

            return Result.success("已列出数据", null);
        } else if (object.equals("student") || object.equals("2")) {
            List<Student> allStudents = app.getAdminService().findAllStudent();
            List<String> visualized = allStudents.stream().map(Student::toString).toList();
            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(visualized);

            return Result.success("已列出数据", null);
        } else {
            return Result.warn(FEATURE_NOT_DEVELOPED);
        }
    }

    @Override
    public void reset() {
        this.object = "";
        this.allBook = false;
    }
}
