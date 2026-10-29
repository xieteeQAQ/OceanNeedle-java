package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class search implements Cmd {

    private final App app;

    private String object = "";

    private String keyword = "";

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

    private static final String optionForStudent = "要搜搜的对象是? \n[0]取消 \n[1]书籍 \n[2]记录";
    private static final String optionForAdmin = "要搜搜的对象是? \n[0]取消 \n[1]书籍 \n[2]记录 \n[3]学生";
    private static final String optionForDeveloper = "要搜搜的对象是? \n[0]取消 \n[1]书籍 \n[2]记录 \n[3]学生 \n[4]管理员 \n[5]开发者";

    public search(App app) {
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
    public void reset() {
        this.object = "";
        this.keyword = "";
        this.allBook = false;
    }


    // 考虑用户体验 暂时只提供操作最简单的模糊搜索
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

            if (keyword.isEmpty()) {

                ui.barPrintOption("模糊搜索(学号 姓名 手机号)");
                keyword = ui.readInput("Keyword > ").trim();
            }

            // 搜索要操作的对象 让用户选择 返回序号和搜索结果
            List<Student> students = new ArrayList<>(app.getAdminService().fuzzySearchStudent(keyword));
            // 查不到就返回 交给其他函数处理
            if (students.isEmpty()) return Result.warn(STUDENT_NOT_FOUND);

            // 数据可视化
            List<String> lines = new ArrayList<>(students.stream().map(Student::toString).toList());

            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(lines);

            return Result.success("查询成功", null);
        } else if (object.equals("book") || object.equals("2")) {

            if (keyword.isEmpty()) {

                ui.barPrintOption("模糊搜索(ISBN-13 书名 作者)");
                keyword = ui.readInput("Keyword > ").trim();
            }

            List<Book> books;
            // 搜索要操作的对象 让用户选择 返回序号和搜索结果
            if (allBook) {
                if (App.getUser() == UserType.STUDENT) {
                    books = app.getStudentService().fuzzySearchAvailableBook(keyword);
                } else {
                    books = app.getAdminService().findAllBook();
                }
            } else {
                if (App.getUser() == UserType.STUDENT) {
                    books = app.getStudentService().fuzzySearchAvailableBookWithCount(keyword);
                } else {
                    books = app.getAdminService().findAllBookWithCount();
                }
            }
            // 查不到就返回 交给其他函数处理
            if (books.isEmpty()) return App.getUser() == UserType.STUDENT
                    ? Result.warn(AVAILABLE_BOOK_NOT_FOUND)
                    : Result.warn(BOOK_NOT_FOUND);

            // 数据可视化
            List<String> lines = new ArrayList<>(books.stream().map(Book::toString).toList());

            ui.consoleWriteTitle("查询结果");
            ui.consoleWriteList(lines);

            return Result.success("查询成功", null);
        } else {
            return Result.success(FEATURE_NOT_DEVELOPED);
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
                keyword = args.get(1);
            } else if (i == 2) {
                if (object.equals("book") && arg.equals("--all")) {
                    allBook = true;
                }
            }
        }

        return Result.success(NONE);
    }
}
