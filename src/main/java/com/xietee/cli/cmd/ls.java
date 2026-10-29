package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class ls implements Cmd {

    @Setter
    private App app;

    private String object = "";

    private boolean allBook = false;

    private final Set<String> objectsForStudent = new HashSet<>(Set.of(
            "record", "book"
    ));
    private final Set<String> objectsForAdmin = new HashSet<>(Set.of(
            "student", "record", "book"
    ));
    private final Set<String> objectsForDeveloper = new HashSet<>(Set.of(
            "student", "record", "book", "admin", "developer"
    ));

    private static final String optionForStudent = "要列出的对象是? \n[0]取消 \n[1]书籍 \n[2]记录";
    private static final String optionForAdmin = "要列出的对象是? \n[0]取消 \n[1]书籍 \n[2]记录 \n[3]学生";
    private static final String optionForDeveloper = "要列出的对象是? \n[0]取消 \n[1]书籍 \n[2]记录 \n[3]学生 \n[4]管理员 \n[5]开发者";

    public ls(App app) {
        this.app = app;
    }

    private String getOption() {
        switch (App.getUser()) {
            case DEVELOPER -> {
                return optionForDeveloper;
            }
            case ADMIN -> {
                return optionForAdmin;
            }
            default -> {
                return optionForStudent;
            }
        }
    }

    private boolean objectsContains(String object) {
        switch (App.getUser()) {
            case DEVELOPER -> {
                return objectsForDeveloper.contains(object);
            }
            case ADMIN -> {
                return objectsForAdmin.contains(object);
            }
            default -> {
                return objectsForStudent.contains(object);
            }
        }
    }

    @Override
    public Result<Void> parse(List<String> args) {

        if (args.isEmpty()) {
            return Result.success(NONE);
        }

        for (int i = 0; i < args.size(); i++) {

            String arg = args.get(i);
            if (i == 0) {
                if (objectsContains(arg)) {
                    this.object = arg;
                } else {
                    return Result.warn(COMMAND_PARAM_ERROR);
                }
            } else if (i == 1) {
                if (object.equals("book") && arg.equals("--all")) {
                    allBook = true;
                }
            }
        }
        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute() {

        final AppUI ui = app.getUi();

        if (object.isEmpty()) {
            ui.barPrintOption(getOption());
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("3")) {
            List<Student> allStudents = app.getAdminService().findAllStudent();
            List<String> visualized = allStudents.stream().map(Student::toString).toList();
            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(visualized);

            return Result.success(NONE);
        } else if (object.equals("book") || object.equals("1")) {

            List<Book> allBooks;

            if (allBook) {
                if (App.getUser() == UserType.STUDENT) {
                    allBooks = app.getStudentService().findAllAvailableBook();
                } else {
                    allBooks = app.getAdminService().findAllBook();
                }
            } else {
                if (App.getUser() == UserType.STUDENT) {
                    allBooks = app.getStudentService().findAllAvailableBookWithCount();
                } else {
                    allBooks = app.getAdminService().findAllBookWithCount();
                }
            }

            List<String> visualized = allBooks.stream().map(Book::toString).toList();

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
        this.allBook = false;
    }
}
