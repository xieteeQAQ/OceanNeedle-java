package com.xietee.cli.cmd;

import com.xietee.cli.App;
import com.xietee.common.Result;
import com.xietee.dto.BookUpdateDTO;
import com.xietee.dto.StudentUpdateDTO;
import com.xietee.entity.Book;
import com.xietee.entity.Student;
import com.xietee.exception.BusinessException;
import com.xietee.ui.AppUI;
import com.xietee.util.ValidationUtils;
import com.xietee.util.numberUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xietee.exception.BusinessExceptionEnum.*;

public class update implements Cmd {

    private String object = "";

    private String key = "";

    private final Set<String> objectsForAdmin = new HashSet<>(Set.of(
            "student", "record", "book"
    ));
    private final Set<String> objectsForDeveloper = new HashSet<>(Set.of(
            "student", "record", "book", "admin", "developer"
    ));

    private static final String optionForAdmin = "要更新的对象是? \n[0]取消 \n[1]书籍 \n[2]学生";
    private static final String optionForDeveloper = "要更新的对象是? \n[0]取消 \n[1]书籍 \n[2]学生 \n[3]记录 \n[4]管理员 \n[5]开发者";

    public update() {}

    private String getOption() {
        if (App.isDEBUG()) {
            return optionForDeveloper;
        }
        return optionForAdmin;
    }

    private boolean objectsContains(String object) {
        if (App.isDEBUG()) {
            return objectsForDeveloper.contains(object);
        }
        return objectsForAdmin.contains(object);
    }

    @Override
    public void reset() {
        this.object = "";
        this.key = "";
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
                case "-k", "--key" -> {
                    if (i + 1 >= args.size()) return Result.warn(COMMAND_PARAM_ERROR);
                    this.key = args.get(i + 1);
                    i++;
                }
            }
        }

        return Result.success(NONE);
    }

    @Override
    public Result<Void> execute(App app) {

        final AppUI ui = app.getUi();

        if (this.object.isEmpty()) {
            ui.barPrintOption(getOption());
            object = ui.readInput("Option > ").trim();
            if (object.equals("0")) {
                return Result.success("", null);
            } else if (object.isEmpty()) {
                return Result.warn(INVALID_NUMBER);
            }
        }

        if (object.equals("student") || object.equals("2")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要更新的学生的学号");
                key = ui.readInput("学号 > ").trim();

                if (key.isEmpty()) {
                    return Result.warn(COMMAND_NEED_PARAM);
                }

            } else if (key.length() != 12) {
                return Result.warn(PARAM_ERROR.getCode(), "学生学号必须为 12 位数");
            }

            Student updateStudent = app.getAdminService().findStudentByStudentID(key);
            // 获取要更新的数据 直接按回车即可跳过
            ui.getStatusBar().printInfo("(按回车可跳过)");
            String password = ui.readInput("密码(" + updateStudent.getPassword() + ") > ").trim();
            String phoneNumber = ui.readInput("电话号码(" + updateStudent.getPhoneNumber() + ") > ").trim();

            // 创建学生 DTO 对象
            StudentUpdateDTO studentUpdateDTO =
                    new StudentUpdateDTO(
                            password.isEmpty() ? updateStudent.getPassword() : password,
                            phoneNumber.isEmpty() ? updateStudent.getPhoneNumber() : phoneNumber);

            // 没更新数据视作更新成功
            if (studentUpdateDTO.equals(updateStudent)) return Result.success(DATA_UNCHANGED);

            try {
                ValidationUtils.validateOrThrow(studentUpdateDTO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PARAM_ERROR.getCode(), e.getMessage());
            }

            // 检查是否违反 unique 字段
            if (app.getAdminService().existedPhoneNumber(phoneNumber)) {
                return Result.warn(STUDENT_CONTRACT_NUMBER_EXISTED);
            }

            // 通过之前查到的学生对象获取其在数据库中的 id
            // 按 id 更新对象
            app.getAdminService().updateStudentById(updateStudent.getId(), studentUpdateDTO);

            return Result.success("更新成功", null);
        } else if (object.equals("book") || object.equals("1")) {

            if (key.isEmpty()) {

                ui.barPrintOption("请输入要更新的书籍的序号");
                key = ui.readInput("序号 > ").trim();

                if (key.isEmpty()) {
                    return Result.warn(COMMAND_NEED_PARAM);
                }

            } else if (!numberUtils.isInteger(key)) {
                return Result.warn(INVALID_NUMBER);
            }

            long id = Long.parseLong(key);

            Book updateBook = app.getAdminService().findBookById(id);
            // 获取要更新的数据 直接按回车即可跳过
            ui.getStatusBar().printInfo("(按回车可跳过)");
            String title = ui.readInput("书名(" + updateBook.getTitle() + ") > ").trim();
            String author = ui.readInput("作者(" + updateBook.getAuthor() + ") > ").trim();

            // 书籍的可借状态由借阅/归还流程维护，管理员不能手动修改
            BookUpdateDTO bookUpdateDTO =
                    new BookUpdateDTO(
                            title.isEmpty() ? updateBook.getTitle() : title,
                            author.isEmpty() ? updateBook.getAuthor() : author);

            // 没更新数据视作更新成功
            if (bookUpdateDTO.equals(updateBook)) return Result.success(DATA_UNCHANGED);

            try {
                ValidationUtils.validateOrThrow(bookUpdateDTO);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PARAM_ERROR.getCode(), e.getMessage());
            }

            // 通过之前查到的学生对象获取其在数据库中的 id
            // 按 id 更新对象
            app.getAdminService().updateBookById(id, bookUpdateDTO);

            return Result.success("更新成功", null);
        } else {
            return Result.success(FEATURE_NOT_DEVELOPED);
        }
    }
}
