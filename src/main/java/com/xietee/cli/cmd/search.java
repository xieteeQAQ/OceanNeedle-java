package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.cli.UserType;
import com.xietee.common.Result;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.ui.AppUI;

import java.util.*;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class search implements Cmd {

    private String object = "";

    private String keyword = "";

    private boolean allBook = false;

    private final Set<String> objectsForStudent = new HashSet<>(Set.of(
            "book"
    ));
    private final Set<String> objectsForAdmin = new HashSet<>(Set.of(
            "student", "book"
    ));
    private final Set<String> objectsForDeveloper = new HashSet<>(Set.of(
            "student", "book", "admin", "developer"
    ));

    private static final String optionForStudent = "要搜索的对象是? \n[0]取消 \n[1]书籍";
    private static final String optionForAdmin = "要搜索的对象是? \n[0]取消 \n[1]书籍 \n[2]学生";
    private static final String optionForDeveloper = "要搜索的对象是? \n[0]取消 \n[1]书籍 \n[2]学生 \n[3]管理员 \n[4]开发者";

    public search() {
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
    public void reset() {
        this.object = "";
        this.keyword = "";
        this.allBook = false;
    }


    // 考虑用户体验 暂时只提供操作最简单的模糊搜索
    @Override
    public Result<Void> execute(App app) {

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

        if (object.equals("book") || object.equals("1")) {

            if (keyword.isEmpty()) {

                ui.barPrintOption("模糊搜索(ISBN-13 书名 作者)");
                keyword = ui.readInput("Keyword > ").trim();
            }

            List<Book> books;
            // 搜索要操作的对象 让用户选择 返回序号和搜索结果
            if (allBook) {
                books = app.getAdminService().fuzzySearchBook(keyword);
            } else {
                books = app.getAdminService().fuzzySearchBookWithCount(keyword);
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
        } else if (object.equals("student") || object.equals("2")) {

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
            switch (arg) {
                case "-o", "--object" -> {
                    if (i + 1 >= args.size()) return Result.warn(COMMAND_PARAM_ERROR);
                    String argsObject = args.get(i + 1);

                    if (!objectsContains(argsObject)) return Result.warn(COMMAND_PARAM_ERROR);
                    this.object = argsObject;
                    i++;
                }
                case "-a", "--all" -> this.allBook = true;
                case "-k", "--keyword" -> {
                    if (i + 1 >= args.size()) return Result.warn(COMMAND_PARAM_ERROR);
                    this.keyword = args.get(i + 1);
                    i++;
                }
            }
        }

        return Result.success(NONE);
    }
}
